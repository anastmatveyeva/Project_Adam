package com.adamsite.projectadam.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.adamsite.projectadam.MainActivity;
import com.adamsite.projectadam.R;
import com.adamsite.projectadam.model.VKAudio;
import com.adamsite.projectadam.receiver.AudioIntentReceiver;

import java.io.IOException;
import java.util.List;

public class AudioService extends Service {

    public static final String ACTION_PLAY = "com.adamsite.projectadam.action.play";
    public static final String ACTION_PAUSE = "com.adamsite.projectadam.action.pause";
    public static final String ACTION_REWIND = "com.adamsite.projectadam.action.rewind";
    public static final String ACTION_FAST_FORWARD = "com.adamsite.projectadam.action.fast_forward";
    public static final String ACTION_NEXT = "com.adamsite.projectadam.action.next";
    public static final String ACTION_PREVIOUS = "com.adamsite.projectadam.action.previous";
    public static final String ACTION_STOP = "com.adamsite.projectadam.action.stop";
    public static final String ACTION_NOISY = "com.adamsite.projectadam.action.noisy";

    public static final int NOTIFICATION_ID = 1;

    private MediaPlayer mMediaPlayer;
    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat mMediaController;
    private PlaybackStateCompat playbackState;
    private AudioIntentReceiver audioIntentReceiver;

    private List<VKAudio> trackList;
    private int position;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MediaPlayerService", "onCreate");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
//        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        audioIntentReceiver = new AudioIntentReceiver();
        getApplicationContext().registerReceiver(audioIntentReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equalsIgnoreCase(ACTION_PLAY) && intent.getParcelableArrayListExtra("tracklist") != null) {
            trackList = intent.getParcelableArrayListExtra("tracklist");
            position = intent.getIntExtra("position", 0);
        }

        if (mMediaSession == null) {
            initMediaSessions();
        }

        handleIntent(intent);

        Log.d("MediaPlayerService", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MediaPlayerService", "onDestroy");
        getApplicationContext().unregisterReceiver(audioIntentReceiver);
        stopForeground(true);
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();

        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            mMediaController.getTransportControls().play();
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            mMediaController.getTransportControls().pause();
        } else if (action.equalsIgnoreCase(ACTION_FAST_FORWARD)) {
            mMediaController.getTransportControls().fastForward();
        } else if (action.equalsIgnoreCase(ACTION_REWIND)) {
            mMediaController.getTransportControls().rewind();
        } else if (action.equalsIgnoreCase(ACTION_PREVIOUS)) {
            mMediaController.getTransportControls().skipToPrevious();
        } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
            mMediaController.getTransportControls().skipToNext();
        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            mMediaController.getTransportControls().stop();
        } else if (action.equalsIgnoreCase(ACTION_NOISY)) {
            Toast.makeText(getApplicationContext(), "Headphones Disconnected", Toast.LENGTH_SHORT).show();
            mMediaController.getTransportControls().pause();
        }
    }

    @NonNull
    private NotificationCompat.Action generateAction(int icon, String title, String intentAction) {
        Intent actionIntent = new Intent(getApplicationContext(), AudioService.class);
        actionIntent.setAction(intentAction);
        PendingIntent actionPendingIntent = PendingIntent.getService(getApplicationContext(), 1, actionIntent, 0);
        return new NotificationCompat.Action.Builder(icon, title, actionPendingIntent).build();
    }

    private void buildNotification(NotificationCompat.Action action, String actionString) {
        Intent myAudioFragmentIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent myAudioFragmentPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, myAudioFragmentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopServiceIntent = new Intent(getApplicationContext(), AudioService.class);
        stopServiceIntent.setAction(ACTION_STOP);
        PendingIntent stopServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, stopServiceIntent, 0);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setWhen(0)
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_album_default))
                .setContentTitle(trackList.get(position).getAudioTitle())
                .setContentText(trackList.get(position).getAudioArtist())
                .setContentIntent(myAudioFragmentPendingIntent)
                .setDeleteIntent(stopServicePendingIntent)
                .addAction(generateAction(R.drawable.ic_skip_previous_white_24dp, "Previous", ACTION_PREVIOUS))
                .addAction(generateAction(R.drawable.ic_fast_rewind_white_24dp, "Rewind", ACTION_REWIND))
                .addAction(action)
                .addAction(generateAction(R.drawable.ic_fast_forward_white_24dp, "Forward", ACTION_FAST_FORWARD))
                .addAction(generateAction(R.drawable.ic_skip_next_white_24dp, "Next", ACTION_NEXT))
                .setStyle(new NotificationCompat.MediaStyle()
                                .setMediaSession(mMediaSession.getSessionToken())
                                .setShowActionsInCompactView(0, 2, 4)
                                .setShowCancelButton(true)
                                .setCancelButtonIntent(stopServicePendingIntent)
                );

        switch (actionString) {
            case ACTION_PAUSE:
                builder.setSmallIcon(R.drawable.ic_pause_white_24dp);
                stopForeground(false);
                break;
            default:
                startForeground(NOTIFICATION_ID, builder.build());
                break;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void initMediaSessions() {
        Toast.makeText(getApplicationContext(), "init mediasession", Toast.LENGTH_SHORT).show();
        Log.d("MediaPlayerService", "init mediasession");

        playbackState = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SEEK_TO | PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID  | PlaybackStateCompat.ACTION_PLAY_FROM_URI)
                .setState(PlaybackStateCompat.STATE_PLAYING, position, 1.0f, SystemClock.elapsedRealtime())
                .build();

        mMediaSession = new MediaSessionCompat(getApplicationContext(), "mMediaSession");
        mMediaSession.setPlaybackToLocal(AudioManager.STREAM_MUSIC);
        mMediaSession.setPlaybackState(playbackState);
        mMediaSession.setActive(true);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mMediaPlayer != null && !(!mMediaPlayer.isPlaying() && mMediaPlayer.getCurrentPosition() > 1)) {
                    mMediaPlayer.start();
                }
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getApplicationContext(), "track end", Toast.LENGTH_SHORT).show();
            }
        });


        try {
            mMediaController = new MediaControllerCompat(getApplicationContext(), mMediaSession.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
                                      private String actionString = "stopped";

                                      @Override
                                      public void onPlay() {
                                          super.onPlay();
                                          if (!mMediaPlayer.isPlaying() && mMediaPlayer.getCurrentPosition() > 1 && actionString.equalsIgnoreCase("paused")) {
                                              mMediaPlayer.start();
                                              actionString = "playing";

                                              Log.d("MediaPlayerService", "onPlay");
                                              Toast.makeText(getApplicationContext(), "onPlay", Toast.LENGTH_SHORT).show();
//                                          Toast.makeText(getApplicationContext(), String.valueOf(playbackState.getState()), Toast.LENGTH_SHORT).show();
                                          } else {
                                              try {
                                                  mMediaPlayer.reset();
                                                  mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(trackList.get(position).getAudioURL()));
                                                  mMediaPlayer.prepareAsync();
                                                  actionString = "playing";
                                              } catch (IOException e) {
                                                  e.printStackTrace();
                                                  Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_SHORT).show();
                                              } catch (IllegalStateException e) {
                                                  e.printStackTrace();
                                                  Toast.makeText(getApplicationContext(), "IllegalStateException", Toast.LENGTH_SHORT).show();
                                              }
                                          }
                                          buildNotification(generateAction(R.drawable.ic_pause_white_24dp, "Pause", ACTION_PAUSE), ACTION_PLAY);
                                      }

                                      @Override
                                      public void onPause() {
                                          super.onPause();
                                          if (mMediaPlayer.isPlaying() && mMediaPlayer.getCurrentPosition() > 1) {
                                              mMediaPlayer.pause();
                                              actionString = "paused";

                                              Log.d("MediaPlayerService", "onPause");
                                              Toast.makeText(getApplicationContext(), "onPause", Toast.LENGTH_SHORT).show();
//                                          Toast.makeText(getApplicationContext(), String.valueOf(playbackState.getState()), Toast.LENGTH_SHORT).show();
                                          }
                                          buildNotification(generateAction(R.drawable.ic_play_arrow_white_24dp, "Play", ACTION_PLAY), ACTION_PAUSE);
                                      }

                                      @Override
                                      public void onSkipToNext() {
                                          super.onSkipToNext();
                                          if (position < trackList.size() - 1) {
                                              position++;
                                          } else {
                                              position = 0;
                                          }
                                          actionString = "playing";

                                          Log.d("MediaPlayerService", "onSkipToNext");
                                          Toast.makeText(getApplicationContext(), "onSkipToNext", Toast.LENGTH_SHORT).show();
//                                          Toast.makeText(getApplicationContext(), String.valueOf(playbackState.getState()), Toast.LENGTH_SHORT).show();

                                          onPlay();
                                          buildNotification(generateAction(R.drawable.ic_pause_white_24dp, "Pause", ACTION_PAUSE), ACTION_NEXT);
                                      }

                                      @Override
                                      public void onSkipToPrevious() {
                                          super.onSkipToPrevious();
                                          if (position > 0) {
                                              position--;
                                          } else {
                                              position = trackList.size() - 1;
                                          }
                                          actionString = "playing";

                                          Log.d("MediaPlayerService", "onSkipToPrevious");
                                          Toast.makeText(getApplicationContext(), "onSkipToPrevious", Toast.LENGTH_SHORT).show();
//                                          Toast.makeText(getApplicationContext(), String.valueOf(playbackState.getState()), Toast.LENGTH_SHORT).show();

                                          onPlay();
                                          buildNotification(generateAction(R.drawable.ic_pause_white_24dp, "Pause", ACTION_PAUSE), ACTION_PREVIOUS);
                                      }

                                      @Override
                                      public void onRewind() {
                                          super.onRewind();
                                          if (actionString.equalsIgnoreCase("playing")) {
                                              mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 10000);
                                              Log.d("MediaPlayerService", "onRewind");
                                              Toast.makeText(getApplicationContext(), "onRewind", Toast.LENGTH_SHORT).show();
                                          }
                                      }

                                      @Override
                                      public void onFastForward() {
                                          super.onFastForward();
                                          if (actionString.equalsIgnoreCase("playing")) {
                                              mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + 10000);
                                              Log.d("MediaPlayerService", "onFastForward");
                                              Toast.makeText(getApplicationContext(), "onFastForward", Toast.LENGTH_SHORT).show();
                                          }
                                      }

                                      @Override
                                      public void onStop() {
                                          super.onStop();
                                          try {
                                              if (mMediaPlayer.isPlaying()) {
                                                  mMediaPlayer.stop();
                                              }
                                              mMediaPlayer.release();
                                              mMediaPlayer = null;
                                              actionString = "stopped";

                                              Log.d("MediaPlayerService", "onStop");
                                              Toast.makeText(getApplicationContext(), "onStop", Toast.LENGTH_SHORT).show();
//                                          Toast.makeText(getApplicationContext(), String.valueOf(playbackState.getState()), Toast.LENGTH_SHORT).show();
                                          } catch (IllegalStateException e) {
                                              Log.e("MediaPlayerService", e.getMessage());
                                          }
                                          NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                          notificationManager.cancel(NOTIFICATION_ID);
                                          Intent intent = new Intent(getApplicationContext(), AudioService.class);
                                          stopService(intent);
                                      }

                                      @Override
                                      public void onSeekTo(long pos) {
                                          super.onSeekTo(pos);
                                          actionString = "playing";
                                      }
                                  }
        );
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaSession.release();
        return super.onUnbind(intent);
    }
}