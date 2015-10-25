package com.adamsite.projectadam;

public interface IMusicFocusable {
    void onGainedAudioFocus();
    void onLostAudioFocus(boolean canDuck);
}
