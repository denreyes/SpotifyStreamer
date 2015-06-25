package com.example.android.spotifystreamer.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.spotifystreamer.R;
import com.example.android.spotifystreamer.object.TopObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import at.markushi.ui.CircleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DJ on 6/25/2015.
 */
public class PlayerFragment extends Fragment implements View.OnClickListener, View.OnTouchListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

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
    int lengthInMilli,pos;
    Uri link;
    MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    String prog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_player,container,false);
        ButterKnife.inject(this, rootView);

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
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos > 0) {
                    stopPlayer();
                    initTrack(--pos);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() - 1 > pos) {
                    stopPlayer();
                    initTrack(++pos);
                }
            }
        });

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
        seekBar.setMax(99);
        seekBar.setOnTouchListener(this);
        btnPlay.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    Runnable notification = new Runnable() {
        public void run() {
            primarySeekBarProgressUpdater();
        }
    };

    private void primarySeekBarProgressUpdater() {
        float progFloat = (((float)mediaPlayer.getCurrentPosition()/lengthInMilli)*100);
        long progLong = mediaPlayer.getCurrentPosition();
        seekBar.setProgress((int) progFloat);
        prog = String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(progLong),
                TimeUnit.MILLISECONDS.toSeconds(progLong) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progLong)));
        txtStart.setText(""+prog);

        if (mediaPlayer.isPlaying()) {
            handler.postDelayed(notification,1000);
        }else{
            mediaPlayer.pause();
            txtStart.setText("0:00");
            seekBar.setProgress(0);
        }
    }

    private void stopPlayer(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnPlay){
            try {
                mediaPlayer.setDataSource(getActivity(), link);
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }

            lengthInMilli = mediaPlayer.getDuration();
            txtEnd.setText(String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(lengthInMilli),
                    TimeUnit.MILLISECONDS.toSeconds(lengthInMilli) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(lengthInMilli))));

            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
                btnPlay.setImageResource(R.drawable.ic_pause);
            }else {
                mediaPlayer.pause();
                btnPlay.setImageResource(R.drawable.ic_play);
            }

            primarySeekBarProgressUpdater();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        btnPlay.setImageResource(R.drawable.ic_play);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.seekBar){
            if(mediaPlayer.isPlaying()){
                SeekBar sb = (SeekBar)v;
                int playPositionInMillisecconds = (lengthInMilli / 100) * sb.getProgress();
                mediaPlayer.seekTo(playPositionInMillisecconds);
            }
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CURRENT", prog);
        outState.putParcelableArrayList("TOP_OBJECT", list);
        outState.putInt("POSITION", pos);
    }
}