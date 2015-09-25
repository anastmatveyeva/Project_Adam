package adamsite.com.projectadam.model;

public class VKAudio {

    private final String audioArtist;
    private final String audioTitle;
    private final int audioID;

    public VKAudio(String audioArtist, String audioTitle, int audioID) {
        this.audioArtist = audioArtist;
        this.audioTitle = audioTitle;
        this.audioID = audioID;
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
}
