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
 * Created by DJ on 6/11/2015.
 */
public class SearchAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<SearchObject> list;

    public SearchAdapter(Context context,ArrayList<SearchObject> list){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.list=list;
    }

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
        SearchHolder holder;
        if (view != null) {
            holder = (SearchHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.list_item_search,parent,false);
            holder = new SearchHolder(view);
            view.setTag(holder);
        }

        holder.txtArtist.setText(list.get(position).artistNames);
        if(list.get(position).artistImages!="")
            Picasso.with(context).load(list.get(position).artistImages).resize(250, 250).centerCrop().into(holder.imgArtist);
        else
            Picasso.with(context).load(R.drawable.noimg).resize(250, 250).centerCrop().into(holder.imgArtist);
        return view;
    }

    static class SearchHolder{
        @InjectView(R.id.txtSearchName) TextView txtArtist;
        @InjectView(R.id.imgSearchArtist) ImageView imgArtist;

        public SearchHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
