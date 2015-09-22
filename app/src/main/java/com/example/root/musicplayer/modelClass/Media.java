package com.example.root.musicplayer.modelClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by root on 8/9/15.
 */
public class Media implements Parcelable {
    private boolean isChecked;
    private  long _id;
    private String song_path;
    private String song_title;
    private long maxDuration;

    public long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void setSong_path(String song_path) {
        this.song_path = song_path;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public long get_id() {
        return _id;
    }

    public String getSong_path() {
        return song_path;
    }

    public String getSong_title() {
        return song_title;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    File file;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
