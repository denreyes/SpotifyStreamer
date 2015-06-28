package com.example.android.spotifystreamer.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import java.util.concurrent.TimeUnit;

import at.markushi.ui.CircleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DJ on 6/25/2015.
 */
public class PlayerDialogFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

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
    private ArrayList<TopObject> list;
    private int pos;
    int switchPlay = 0;
    String prog;
    Intent playerService;
    boolean mBroadcastIsRegistered;

    public static final String BROADCAST_PAUSEPLAY = "com.example.android.spotifystreamer.fragments.sendpauseplay";
    public static final String BROADCAST_SEEKBAR = "com.example.android.spotifystreamer.fragments.sendseekbar";
    Intent seekIntent, pauseplayIntent;

    public static PlayerDialogFragment newInstance(){
        PlayerDialogFragment frag = new PlayerDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_player,container,false);
        ButterKnife.inject(this, rootView);
        playerService = new Intent(getActivity(),PlayerService.class);
        seekIntent = new Intent(BROADCAST_SEEKBAR);
        pauseplayIntent = new Intent(BROADCAST_PAUSEPLAY);

        if(savedInstanceState!=null) {
            txtStart.setText(savedInstanceState.getString("CURRENT"));
            pos = savedInstanceState.getInt("POSITION");
            list = savedInstanceState.getParcelableArrayList("TOP_OBJECT");
            switchPlay = savedInstanceState.getInt("SWITCH_PLAY");
            initTrack(pos);
            initPlay();
        }else {
            Bundle b = getArguments();
            list = b.getParcelableArrayList("TOP_OBJECT");
            pos = b.getInt("POSITION", 0);
            initTrack(pos);
            initPlay();
        }
        setListeners();

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mBroadcastIsRegistered){
            getActivity().unregisterReceiver(broadcastReceiver);
            mBroadcastIsRegistered = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mBroadcastIsRegistered) {
            getActivity().registerReceiver(broadcastReceiver,
                    new IntentFilter(PlayerService.BROADCAST_ACTION));
            mBroadcastIsRegistered=true;
        }
    }

    private void initTrack(int position){

        txtTitle.setText(list.get(position).trackTitle);
        txtAlbum.setText(list.get(position).trackAlbum);
        txtArtist.setText(list.get(position).trackArtist);
        if(list.get(position).trackImage!="")
            Picasso.with(getActivity()).load(list.get(position).trackImage).fit().centerCrop().into(imgTrack);
        else
            Picasso.with(getActivity()).load(R.drawable.noimg).fit().centerCrop().into(imgTrack);

        playerService.putParcelableArrayListExtra("TOP_OBJECT", list);
        playerService.putExtra("POSITION", pos);
    }

    private String formatMinutes(long millis){
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }


    private void setListeners() {
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos > 0) {
                    initTrack(--pos);
                    stopAudio();
                    initPlay();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() - 1 > pos) {
                    initTrack(++pos);
                    stopAudio();
                    initPlay();
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchPlay==0)
                    initPlay();
                else
                    onPausePlay();
            }
        });

        seekBar.setOnSeekBarChangeListener(this);
    }

    private void onPausePlay(){
        if(switchPlay==1){
            btnPlay.setImageResource(R.drawable.ic_play);
            switchPlay=2;
        }else if(switchPlay==2){
            btnPlay.setImageResource(R.drawable.ic_pause);
            switchPlay=1;
        }
        getActivity().sendBroadcast(pauseplayIntent);
    }

    private void initPlay(){
        btnPlay.setImageResource(R.drawable.ic_pause);
        if(switchPlay==0){
            playAudio();
            switchPlay=1;
        }
    }

    private void playAudio() {
        getActivity().startService(playerService);

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(
                PlayerService.BROADCAST_ACTION));
        mBroadcastIsRegistered = true;
    }

    private void stopAudio() {
        if(mBroadcastIsRegistered) {
            getActivity().unregisterReceiver(broadcastReceiver);
            mBroadcastIsRegistered = false;
        }

        getActivity().stopService(playerService);
        switchPlay = 0;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CURRENT", prog);
        outState.putParcelableArrayList("TOP_OBJECT", list);
        outState.putInt("POSITION", pos);
        outState.putInt("SWITCH_PLAY",switchPlay);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent playerService) {
        int seekProgress = playerService.getIntExtra("counter",0);
        int seekMax = playerService.getIntExtra("mediamax",0);

        seekBar.setMax(seekMax);
        seekBar.setProgress(seekProgress);
        txtStart.setText(formatMinutes(seekProgress));
        txtEnd.setText(formatMinutes(seekMax));

        if(formatMinutes(seekProgress).equals(formatMinutes(seekMax))){
            btnPlay.setImageResource(R.drawable.ic_play);
            txtStart.setText("0:00");
            seekBar.setProgress(0);
            stopAudio();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            int seekPos = seekBar.getProgress();
            seekIntent.putExtra("seekpos",seekPos);
            getActivity().sendBroadcast(seekIntent);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}