package com.adamsite.projectadam.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.adamsite.projectadam.service.AudioService;

public class AudioIntentReceiver extends BroadcastReceiver {
    public AudioIntentReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            context.startService(new Intent(context, AudioService.class).setAction(AudioService.ACTION_NOISY));
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);

            if (keyEvent != null) {
                if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) {
                    return;
                }

                switch (keyEvent.getKeyCode()) {
//                case KeyEvent.KEYCODE_HEADSETHOOK:
//                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
//                    context.startService(new Intent(AudioService.ACTION_TOGGLE_PLAYBACK));
//                    break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        context.startService(new Intent(context, AudioService.class).setAction(AudioService.ACTION_PLAY));
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        context.startService(new Intent(context, AudioService.class).setAction(AudioService.ACTION_PAUSE));
                        break;
                    case KeyEvent.KEYCODE_MEDIA_STOP:
                        context.startService(new Intent(context, AudioService.class).setAction(AudioService.ACTION_STOP));
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        context.startService(new Intent(context, AudioService.class).setAction(AudioService.ACTION_NEXT));
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        // TODO: ensure that doing this in rapid succession actually plays the previous song
                        context.startService(new Intent(context, AudioService.class).setAction(AudioService.ACTION_PREVIOUS));
                        break;
                }
            }
        }
    }
}
