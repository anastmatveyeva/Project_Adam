package com.adamsite.projectadam.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaButtonReceiver;
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

public class AudioService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {

    public static final String TAG_SESSION = "com.adamsite.projectadam.mediasession";

    public static final String ACTION_PLAY = "com.adamsite.projectadam.action.play";
    public static final String ACTION_PAUSE = "com.adamsite.projectadam.action.pause";
    public static final String ACTION_REWIND = "com.adamsite.projectadam.action.rewind";
    public static final String ACTION_FAST_FORWARD = "com.adamsite.projectadam.action.fast_forward";
    public static final String ACTION_NEXT = "com.adamsite.projectadam.action.next";
    public static final String ACTION_PREVIOUS = "com.adamsite.projectadam.action.previous";
    public static final String ACTION_STOP = "com.adamsite.projectadam.action.stop";
    public static final String ACTION_NOISY = "com.adamsite.projectadam.action.noisy";

    private static final int NOTIFICATION_ID = 1;

    private MediaPlayer mMediaPlayer;
    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat mMediaController;
    private PlaybackStateCompat mPlaybackState;
    private AudioIntentReceiver audioIntentReceiver;

    private WifiManager.WifiLock wifiLock;

    private List<VKAudio> trackList;
    private int currentAudioID;
    private int position;

    private MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            Log.d("MediaPlayerService", "onPlay");
            Toast.makeText(getApplicationContext(), "onPlay", Toast.LENGTH_SHORT).show();

            switch (mPlaybackState.getState()) {
                case PlaybackStateCompat.STATE_PAUSED:
                    if (currentAudioID == trackList.get(position).getAudioID()) {
                        mMediaPlayer.start();

                        mPlaybackState = new PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_PLAYING, mMediaPlayer.getCurrentPosition(), 1.0f)
                                .build();
                        mMediaSession.setPlaybackState(mPlaybackState);
                        wifiLock.acquire();

                        buildNotification(generateAction(R.drawable.ic_pause_white_24dp, "Pause", ACTION_PAUSE));
                        break;
                    } else {
                        currentAudioID = trackList.get(position).getAudioID();
                    }

                case PlaybackStateCompat.STATE_PLAYING:
                case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                case PlaybackStateCompat.STATE_NONE:
                    mMediaPlayer.reset();
                    try {
                        mMediaPlayer.setDataSource(trackList.get(position).getAudioURL());
                    } catch (IOException e) {
                        mPlaybackState = new PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_ERROR, 0, 0.0f)
                                .build();
                        mMediaSession.setPlaybackState(mPlaybackState);
                        buildNotification(generateAction(R.drawable.ic_play_arrow_white_24dp, "Play", ACTION_PLAY));

                        Log.e("MediaPlayerService", e.getMessage());
                        Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    mPlaybackState = new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_CONNECTING, 0, 0.0f)
                            .build();
                    mMediaSession.setPlaybackState(mPlaybackState);
                    wifiLock.acquire();

                    mMediaPlayer.prepareAsync();
                    buildNotification(generateAction(R.drawable.ic_pause_white_24dp, "Pause", ACTION_PAUSE));
                    break;
            }
        }

        @Override
        public void onPause() {
            switch (mPlaybackState.getState()) {
                case PlaybackStateCompat.STATE_CONNECTING:
                case PlaybackStateCompat.STATE_PLAYING:
                    Log.d("MediaPlayerService", "onPause");
                    Toast.makeText(getApplicationContext(), "onPause", Toast.LENGTH_SHORT).show();

                    mMediaPlayer.pause();
                    mPlaybackState = new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PAUSED, mMediaPlayer.getCurrentPosition(), 0.0f)
                            .build();
                    mMediaSession.setPlaybackState(mPlaybackState);

                    if (wifiLock.isHeld()) {
                        wifiLock.release();
                    }

                    buildNotification(generateAction(R.drawable.ic_play_arrow_white_24dp, "Play", ACTION_PLAY));
                    break;
            }
        }

        @Override
        public void onSkipToNext() {
            Log.d("MediaPlayerService", "onSkipToNext");
            Toast.makeText(getApplicationContext(), "onSkipToNext", Toast.LENGTH_SHORT).show();

            if (position < trackList.size() - 1) {
                position++;
            } else {
                position = 0;
            }

            mPlaybackState = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, mMediaPlayer.getCurrentPosition(), 0.0f)
                    .build();
            mMediaSession.setPlaybackState(mPlaybackState);
            onPlay();
        }

        @Override
        public void onSkipToPrevious() {
            Log.d("MediaPlayerService", "onSkipToPrevious");
            Toast.makeText(getApplicationContext(), "onSkipToPrevious", Toast.LENGTH_SHORT).show();

            if (position > 0) {
                position--;
            } else {
                position = trackList.size() - 1;
            }

            mPlaybackState = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, mMediaPlayer.getCurrentPosition(), 0.0f)
                    .build();
            mMediaSession.setPlaybackState(mPlaybackState);
            onPlay();
        }

        @Override
        public void onFastForward() {
            switch (mPlaybackState.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + 10000);

                    Log.d("MediaPlayerService", "onFastForward");
                    Toast.makeText(getApplicationContext(), "onFastForward", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onRewind() {
            switch (mPlaybackState.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 10000);

                    Log.d("MediaPlayerService", "onRewind");
                    Toast.makeText(getApplicationContext(), "onRewind", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onStop() {
            mPlaybackState = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_STOPPED, 0, 0.0f)
                    .build();
            mMediaSession.setPlaybackState(mPlaybackState);

            if (wifiLock.isHeld()) {
                wifiLock.release();
            }

            Log.d("MediaPlayerService", "onStop");
            Toast.makeText(getApplicationContext(), "onStop", Toast.LENGTH_SHORT).show();

            stopSelf();
        }

        @Override
        public void onSeekTo(long pos) {
            mMediaPlayer.seekTo((int)pos);
        }
    };

    @Override
    public void onPrepared(MediaPlayer mp) {
        switch (mPlaybackState.getState()) {
            case PlaybackStateCompat.STATE_CONNECTING:
                mMediaPlayer.start();

                mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                        .build();
                mMediaSession.setPlaybackState(mPlaybackState);
                break;
        }
        Log.d("MediaPlayerService", "onPrepared");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(getApplicationContext(), "track end", Toast.LENGTH_SHORT).show();
        switch (mPlaybackState.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mMediaController.getTransportControls().skipToNext();
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MediaPlayerService", "onCreate");

        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "wifiLock");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        audioIntentReceiver = new AudioIntentReceiver();
        getApplicationContext().registerReceiver(audioIntentReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MediaPlayerService", "onDestroy");

        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 0.0f)
                .build();
        mMediaSession.setPlaybackState(mPlaybackState);

        mMediaPlayer.release();
        mMediaSession.release();
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }
        getApplicationContext().unregisterReceiver(audioIntentReceiver);
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getParcelableArrayListExtra("tracklist") != null) {
                trackList = intent.getParcelableArrayListExtra("tracklist");
                position = intent.getIntExtra("position", 0);
                if (currentAudioID == 0) {
                    currentAudioID = intent.getIntExtra("id", 0);
                }
            }

            if (mMediaSession == null) {
                initMediaSessions();
            }

            handleIntent(intent);
        }
        Log.d("MediaPlayerService", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaSessions() {
        Toast.makeText(getApplicationContext(), "init mediasession", Toast.LENGTH_SHORT).show();
        Log.d("MediaPlayerService", "init mediasession");

        ComponentName eventReceiver = new ComponentName(getApplicationContext().getPackageName(), AudioIntentReceiver.class.getName());
        PendingIntent buttonReceiverIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                0,
                new Intent(Intent.ACTION_MEDIA_BUTTON),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mPlaybackState = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        | PlaybackStateCompat.ACTION_SEEK_TO)
                .setState(PlaybackStateCompat.STATE_NONE, 0, 0.0f, SystemClock.elapsedRealtime())
                .build();

        mMediaSession = new MediaSessionCompat(getApplicationContext(), TAG_SESSION, eventReceiver, buttonReceiverIntent);
//        mMediaSession.setPlaybackToLocal(AudioManager.STREAM_MUSIC);
        mMediaSession.setPlaybackState(mPlaybackState);
        mMediaSession.setActive(true);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS |
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mMediaSession.setCallback(mMediaSessionCallback);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        try {
            mMediaController = new MediaControllerCompat(getApplicationContext(), mMediaSession.getSessionToken());
        } catch (RemoteException e) {
            Log.e("MediaPlayerService", e.getMessage());
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        MediaButtonReceiver.handleIntent(mMediaSession, intent);

        switch (intent.getAction()) {
            case ACTION_PLAY:
                mMediaController.getTransportControls().play();
                break;
            case ACTION_PAUSE:
                mMediaController.getTransportControls().pause();
                break;
            case ACTION_FAST_FORWARD:
                mMediaController.getTransportControls().fastForward();
                break;
            case ACTION_REWIND:
                mMediaController.getTransportControls().rewind();
                break;
            case ACTION_PREVIOUS:
                mMediaController.getTransportControls().skipToPrevious();
                break;
            case ACTION_NEXT:
                mMediaController.getTransportControls().skipToNext();
                break;
            case ACTION_STOP:
                mMediaController.getTransportControls().stop();
                break;
            case ACTION_NOISY:
                mMediaController.getTransportControls().pause();
                Log.d("MediaPlayerService", "onAudioBecomingNoisy");
                Toast.makeText(getApplicationContext(), "Headphones Disconnected", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @NonNull
    private NotificationCompat.Action generateAction(int icon, String title, String intentAction) {
        Intent actionIntent = new Intent(getApplicationContext(), AudioService.class);
        actionIntent.setAction(intentAction);
        PendingIntent actionPendingIntent = PendingIntent.getService(getApplicationContext(), 1, actionIntent, 0);
        return new NotificationCompat.Action.Builder(icon, title, actionPendingIntent).build();
    }

    private void buildNotification(NotificationCompat.Action action) {
        Intent contentIntent = new Intent(getApplicationContext(), MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent myAudioFragmentPendingIntent =PendingIntent.getActivity(
                getApplicationContext(), 0, contentIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent deleteIntent = new Intent(getApplicationContext(), AudioService.class);
        deleteIntent.setAction(ACTION_STOP);
        PendingIntent stopServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, deleteIntent, 0);

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

        switch (mPlaybackState.getState()) {
            case PlaybackStateCompat.STATE_ERROR:
            case PlaybackStateCompat.STATE_PAUSED:
                stopForeground(false);
                break;
            default:
                startForeground(NOTIFICATION_ID, builder.build());
                break;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}