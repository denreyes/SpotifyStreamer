package com.example.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DJ on 6/14/2015.
 */
public class TopObject implements Parcelable {

    String trackTitle,trackAlbum,trackImage;

    public TopObject(String trackTitle,String trackAlbum,String trackImage){
        this.trackTitle = trackTitle;
        this.trackAlbum = trackAlbum;
        this.trackImage = trackImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackTitle);
        dest.writeString(trackAlbum);
        dest.writeString(trackImage);
    }

    private TopObject(Parcel in){
        trackTitle=in.readString();
        trackAlbum=in.readString();
        trackImage=in.readString();
    }

    public static final Parcelable.Creator<TopObject> CREATOR
            = new Parcelable.Creator<TopObject>() {
        public TopObject createFromParcel(Parcel in) {
            return new TopObject(in);
        }

        public TopObject[] newArray(int size) {
            return new TopObject[size];
        }
    };
}
