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
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.adamsite.projectadam.R;
import com.adamsite.projectadam.receiver.AudioIntentReceiver;

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
    private MediaSessionManager mManager;
    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat mMediaController;
    private AudioIntentReceiver audioIntentReceiver;

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
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        audioIntentReceiver = new AudioIntentReceiver();
        getApplicationContext().registerReceiver(audioIntentReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mManager == null) {
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
        Intent stopServiceIntent = new Intent(getApplicationContext(), AudioService.class);
        stopServiceIntent.setAction(ACTION_STOP);
        PendingIntent stopServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, stopServiceIntent, 0);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setOngoing(true)
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
//                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.vk_white))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_album_default))
                .setContentTitle("Title")
                .setContentText("Artist")
//                .setContentIntent()
                .setDeleteIntent(stopServicePendingIntent)
                .addAction(generateAction(R.drawable.ic_skip_previous_white_24dp, "Previous", ACTION_PREVIOUS))
                .addAction(action)
                .addAction(generateAction(R.drawable.ic_skip_next_white_24dp, "Next", ACTION_NEXT))
                .setStyle(new NotificationCompat.MediaStyle()
                                .setMediaSession(mMediaSession.getSessionToken())
                                .setShowActionsInCompactView(0, 1, 2)
                                .setShowCancelButton(true)
                                .setCancelButtonIntent(stopServicePendingIntent)
                );

        switch (actionString) {
            case ACTION_PAUSE:
                builder.setOngoing(false);
                break;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void initMediaSessions() {
        mMediaPlayer = new MediaPlayer();

        mMediaSession = new MediaSessionCompat(getApplicationContext(), "simple player session");
        try {
            mMediaController = new MediaControllerCompat(getApplicationContext(), mMediaSession.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
                                      @Override
                                      public void onPlay() {
                                          super.onPlay();
                                          Log.d("MediaPlayerService", "onPlay");
                                          Toast.makeText(getApplicationContext(), "onPlay", Toast.LENGTH_SHORT).show();
                                          buildNotification(generateAction(R.drawable.ic_pause_white_24dp, "Pause", ACTION_PAUSE), ACTION_PLAY);
                                      }

                                      @Override
                                      public void onPause() {
                                          super.onPause();
                                          Log.d("MediaPlayerService", "onPause");
                                          Toast.makeText(getApplicationContext(), "onPause", Toast.LENGTH_SHORT).show();
                                          buildNotification(generateAction(R.drawable.ic_play_arrow_white_24dp, "Play", ACTION_PLAY), ACTION_PAUSE);
                                      }

                                      @Override
                                      public void onSkipToNext() {
                                          super.onSkipToNext();
                                          Log.d("MediaPlayerService", "onSkipToNext");
                                          Toast.makeText(getApplicationContext(), "onSkipToNext", Toast.LENGTH_SHORT).show();
                                          //Change media here
                                          buildNotification(generateAction(R.drawable.ic_pause_white_24dp, "Pause", ACTION_PAUSE), ACTION_NEXT);
                                      }

                                      @Override
                                      public void onSkipToPrevious() {
                                          super.onSkipToPrevious();
                                          Log.d("MediaPlayerService", "onSkipToPrevious");
                                          Toast.makeText(getApplicationContext(), "onSkipToPrevious", Toast.LENGTH_SHORT).show();
                                          //Change media here
                                          buildNotification(generateAction(R.drawable.ic_pause_white_24dp, "Pause", ACTION_PAUSE), ACTION_PREVIOUS);
                                      }

                                      @Override
                                      public void onFastForward() {
                                          super.onFastForward();
                                          Log.d("MediaPlayerService", "onFastForward");
                                          Toast.makeText(getApplicationContext(), "onFastForward", Toast.LENGTH_SHORT).show();
                                          //Manipulate current media here
                                      }

                                      @Override
                                      public void onRewind() {
                                          super.onRewind();
                                          Log.e("MediaPlayerService", "onRewind");
                                          Toast.makeText(getApplicationContext(), "onRewind", Toast.LENGTH_SHORT).show();
                                          //Manipulate current media here
                                      }

                                      @Override
                                      public void onStop() {
                                          super.onStop();
                                          Log.d("MediaPlayerService", "onStop");
                                          Toast.makeText(getApplicationContext(), "onStop", Toast.LENGTH_SHORT).show();
                                          //Stop media player here
                                          try {
                                              mMediaPlayer.release();
                                              mMediaPlayer = null;
                                              mManager = null;
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
                                      }
                                  }
        );
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaSession.release();
        return super.onUnbind(intent);
    }
}