package com.adamsite.projectadam.model;

public class VKAudio {

    private final String audioArtist;
    private final String audioTitle;
    private final int audioID;
    private final String audioURL;
    private boolean isPlaying;

    public VKAudio(String audioArtist, String audioTitle, int audioID, String audioURL) {
        this.audioArtist = audioArtist;
        this.audioTitle = audioTitle;
        this.audioID = audioID;
        this.audioURL = audioURL;
    }

    public String getAudioArtist() {
        return audioArtist;
    }

    public String getAudioTitle() {
        return audioTitle;
    }

    public int getAudioID() {
        return audioID;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
}
