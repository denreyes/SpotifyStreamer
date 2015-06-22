package com.example.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DJ on 6/14/2015.
 */
public class TopObject implements Parcelable {

    String trackTitle,trackAlbum,trackImage,trackArtist,trackPlay;
    double trackDuration;

    public TopObject(String trackTitle,String trackAlbum,String trackImage,
                     String trackArtist,String trackPlay,double trackDuration){
        this.trackTitle = trackTitle;
        this.trackAlbum = trackAlbum;
        this.trackImage = trackImage;
        this.trackArtist = trackArtist;
        this.trackPlay = trackPlay;
        this.trackDuration = trackDuration;
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
        dest.writeString(trackArtist);
        dest.writeString(trackPlay);
        dest.writeDouble(trackDuration);
    }

    private TopObject(Parcel in){
        trackTitle=in.readString();
        trackAlbum=in.readString();
        trackImage=in.readString();
        trackArtist=in.readString();
        trackPlay=in.readString();
        trackDuration=in.readDouble();
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
