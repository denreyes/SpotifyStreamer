package com.example.android.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by DJ on 6/12/2015.
 */
public class TopAdapter extends BaseAdapter{
    String[] trackTitle, trackAlbum, trackImage;
    LayoutInflater inflater;
    Context context;

    public TopAdapter(Context context, String[] trackTitle, String[] trackAlbum, String[] trackImage){
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.trackTitle = trackTitle;
        this.trackAlbum = trackAlbum;
        this.trackImage = trackImage;
    }

    @Override
    public int getCount() {
        return trackTitle.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = inflater.inflate(R.layout.list_item_top,parent,false);

        ImageView imgTopArtist = (ImageView)rootView.findViewById(R.id.imgTopArtist);
        TextView txtTrackTitle = (TextView)rootView.findViewById(R.id.txtTrackTitle);
        TextView txtAlbum = (TextView)rootView.findViewById(R.id.txtAlbum);

        txtTrackTitle.setText(trackTitle[position]);
        txtAlbum.setText(trackAlbum[position]);
        if(trackImage[position]!=null)
            Picasso.with(context).load(trackImage[position]).resize(250, 250).centerCrop().into(imgTopArtist);
        else
            Picasso.with(context).load(R.drawable.noimg).resize(250, 250).centerCrop().into(imgTopArtist);

        return rootView;
    }
}
