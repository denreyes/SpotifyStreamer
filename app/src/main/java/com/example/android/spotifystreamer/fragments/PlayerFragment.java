package com.example.android.spotifystreamer.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.spotifystreamer.R;
import com.example.android.spotifystreamer.object.TopObject;
import com.example.android.spotifystreamer.service.PlayerService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DJ on 6/25/2015.
 */
public class PlayerFragment extends Fragment{

    @InjectView(R.id.txtTrackArtist) TextView txtArtist;
    @InjectView(R.id.txtTrackAlbum) TextView txtAlbum;
    @InjectView(R.id.imgTrack) ImageView imgTrack;
    @InjectView(R.id.txtTrackTitle) TextView txtTitle;
    @InjectView(R.id.txtStart) TextView txtStart;
    @InjectView(R.id.txtEnd) TextView txtEnd;
    @InjectView(R.id.btnPrevious) CircleButton btnPrevious;
    @InjectView(R.id.btnPlay) CircleButton btnPlay;
    @InjectView(R.id.btnNext) CircleButton btnNext;
    @InjectView(R.id.seekBar) SeekBar seekBar;
    ArrayList<TopObject> list;
    int pos;
    boolean boolMusicPlaying = false;
    Uri link;
    String prog;
    Intent playerService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_player,container,false);
        ButterKnife.inject(this, rootView);
        playerService = new Intent(getActivity(),PlayerService.class);

        if(savedInstanceState!=null) {
            txtStart.setText(savedInstanceState.getString("CURRENT"));
            pos = savedInstanceState.getInt("POSITION");
            list = savedInstanceState.getParcelableArrayList("TOP_OBJECT");
        }else {
            Intent i = getActivity().getIntent();
            list = i.getParcelableArrayListExtra("TOP_OBJECT");
            pos = i.getIntExtra("POSITION", 0);
        }
        initTrack(pos);
        setListeners();

        return rootView;
    }

    private void initTrack(int position){

        txtTitle.setText(list.get(position).trackTitle);
        txtAlbum.setText(list.get(position).trackAlbum);
        txtArtist.setText(list.get(position).trackArtist);
        if(list.get(position).trackImage!="")
            Picasso.with(getActivity()).load(list.get(position).trackImage).fit().centerCrop().into(imgTrack);
        else
            Picasso.with(getActivity()).load(R.drawable.noimg).fit().centerCrop().into(imgTrack);

        link = Uri.parse(list.get(position).trackPlay);
    }


    private void setListeners() {
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos > 0) {
                    initTrack(--pos);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() - 1 > pos) {
                    initTrack(++pos);
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.setImageResource(R.drawable.ic_pause);
                onPausePlay();
            }
        });
    }

    private void onPausePlay(){
        if(!boolMusicPlaying){
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
            playAudio();
        }else{
            btnPlay.setBackgroundResource(R.drawable.ic_play);
            stopAudio();
        }
    }

    private void playAudio() {
        playerService.putExtra("LINK",link.toString());
        //TODO:SURROUND W/ TRY-CATCH
        getActivity().startService(playerService);
        boolMusicPlaying=true;
    }

    private void stopAudio() {
        //TODO:SURROUND W/ TRY-CATCH
        getActivity().stopService(playerService);
        boolMusicPlaying=false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CURRENT", prog);
        outState.putParcelableArrayList("TOP_OBJECT", list);
        outState.putInt("POSITION", pos);
    }
}