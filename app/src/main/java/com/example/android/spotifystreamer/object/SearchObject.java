package com.example.android.spotifystreamer.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DJ on 6/14/2015.
 */
public class SearchObject implements Parcelable{

    public String artistNames;
    public String artistImages;
    public String spotifyId;

    public SearchObject(String artistNames,String artistImages,String spotifyId){
        this.artistNames=artistNames;
        this.artistImages=artistImages;
        this.spotifyId=spotifyId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(artistNames);
        out.writeString(artistImages);
        out.writeString(spotifyId);
    }

    private SearchObject(Parcel in){
        artistNames=in.readString();
        artistImages=in.readString();
        spotifyId=in.readString();
    }

    public static final Parcelable.Creator<SearchObject> CREATOR
            = new Parcelable.Creator<SearchObject>() {
        public SearchObject createFromParcel(Parcel in) {
            return new SearchObject(in);
        }

        public SearchObject[] newArray(int size) {
            return new SearchObject[size];
        }
    };
}
