package com.example.android.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DJ on 6/12/2015.
 */
public class TopAdapter extends BaseAdapter{
    LayoutInflater inflater;
    Context context;
    ArrayList<TopObject> list;

    public TopAdapter(Context context, ArrayList<TopObject> list){
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(int position, View view, ViewGroup parent) {
        TopHolder holder;
        if (view != null) {
            holder = (TopHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.list_item_top,parent,false);
            holder = new TopHolder(view);
            view.setTag(holder);
        }

        holder.txtTrackTitle.setText(list.get(position).trackTitle);
        holder.txtAlbum.setText(list.get(position).trackAlbum);
        if(list.get(position).trackImage!=null)
            Picasso.with(context).load(list.get(position).trackImage).fit().centerCrop().into(holder.imgTopArtist);
        else
            Picasso.with(context).load(R.drawable.noimg).fit().centerCrop().into(holder.imgTopArtist);

        return view;
    }

    static class TopHolder{
        @InjectView(R.id.imgTopArtist) ImageView imgTopArtist;
        @InjectView(R.id.txtTrackTitle) TextView txtTrackTitle;
        @InjectView(R.id.txtAlbum) TextView txtAlbum;

        public TopHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
