package com.adamsite.projectadam.audiofocus;

import android.content.Context;
import android.media.AudioManager;

public class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {

    AudioManager mAudioManager;
    IMusicFocusable mFocusable;

    public AudioFocusHelper(Context context, IMusicFocusable focusable) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mFocusable = focusable;
    }

    public boolean requestFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public boolean abandonFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (mFocusable == null) return;
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                mFocusable.onGainedAudioFocus();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mFocusable.onLostAudioFocus(false);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mFocusable.onLostAudioFocus(true);
                break;
            default:
        }
    }
}