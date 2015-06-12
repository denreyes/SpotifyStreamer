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
 * Created by DJ on 6/11/2015.
 */
public class SearchAdapter extends BaseAdapter {
    String[] artistNames;
    String[] artistImages;
    LayoutInflater inflater;
    Context context;

    public SearchAdapter(String[] artistNames,String[] artistImages,Context context){
        this.artistNames = artistNames;
        this.artistImages = artistImages;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public int getCount() {
        return artistNames.length;
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
        View row = inflater.inflate(R.layout.list_item_search,parent,false);
        TextView txtArtist = (TextView)row.findViewById(R.id.txtSearchName);
        ImageView imgArtist = (ImageView)row.findViewById(R.id.imgSearchArtist);

        txtArtist.setText(artistNames[position]);
        if(artistImages[position]!=null)
            Picasso.with(context).load(artistImages[position]).resize(250, 250).centerCrop().into(imgArtist);
        else
            Picasso.with(context).load(R.drawable.noimg).resize(250, 250).centerCrop().into(imgArtist);
        return row;
    }
}
