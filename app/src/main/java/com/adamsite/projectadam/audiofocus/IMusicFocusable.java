package com.adamsite.projectadam.audiofocus;

public interface IMusicFocusable {
    void onGainedAudioFocus();
    void onLostAudioFocus(boolean canDuck);
}
