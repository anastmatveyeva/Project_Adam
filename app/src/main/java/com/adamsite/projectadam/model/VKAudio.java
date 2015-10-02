package com.adamsite.projectadam.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VKAudio implements Parcelable {

    private final String audioArtist;
    private final String audioTitle;
    private final int audioID;
    private final String audioURL;
    private final int audioDuration;

    private Status audioStatus;
    public enum Status {
        PLAYING, PAUSED, OFF;
    }

    public VKAudio(String audioArtist, String audioTitle, int audioID, String audioURL, int audioDuration) {
        this.audioArtist = audioArtist;
        this.audioTitle = audioTitle;
        this.audioID = audioID;
        this.audioURL = audioURL;
        this.audioDuration = audioDuration;
        this.audioStatus = Status.OFF;
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

    public int getAudioDuration() {
        return audioDuration;
    }

    public Status getAudioStatus() {
        return audioStatus;
    }

    public void setAudioStatus(Status audioStatus) {
        this.audioStatus = audioStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.audioArtist);
        dest.writeString(this.audioTitle);
        dest.writeInt(this.audioID);
        dest.writeString(this.audioURL);
        dest.writeInt(this.audioDuration);
        dest.writeInt(this.audioStatus == null ? -1 : this.audioStatus.ordinal());
    }

    protected VKAudio(Parcel in) {
        this.audioArtist = in.readString();
        this.audioTitle = in.readString();
        this.audioID = in.readInt();
        this.audioURL = in.readString();
        this.audioDuration = in.readInt();
        int tmpAudioStatus = in.readInt();
        this.audioStatus = tmpAudioStatus == -1 ? null : Status.values()[tmpAudioStatus];
    }

    public static final Parcelable.Creator<VKAudio> CREATOR = new Parcelable.Creator<VKAudio>() {
        public VKAudio createFromParcel(Parcel source) {
            return new VKAudio(source);
        }

        public VKAudio[] newArray(int size) {
            return new VKAudio[size];
        }
    };
}