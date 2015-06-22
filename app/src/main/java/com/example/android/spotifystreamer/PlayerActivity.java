package com.example.android.spotifystreamer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import at.markushi.ui.CircleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DJ on 6/21/2015.
 */
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener,
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
    boolean stat = true;
    int lengthInMilli;
    Uri link;
    MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.inject(this);

        Intent i = getIntent();
        txtTitle.setText(i.getStringExtra("TRACK_TITLE"));
        txtAlbum.setText(i.getStringExtra("TRACK_ALBUM"));
        txtArtist.setText(i.getStringExtra("TRACK_ARTIST"));
        if(i.getStringExtra("TRACK_IMAGE")!="")
            Picasso.with(this).load(i.getStringExtra("TRACK_IMAGE")).fit().centerCrop().into(imgTrack);
        else
            Picasso.with(this).load(R.drawable.noimg).fit().centerCrop().into(imgTrack);

        link = Uri.parse(i.getStringExtra("TRACK_PLAY"));
        seekBar.setMax(99); // It means 100% .0-99
        seekBar.setOnTouchListener(this);
        btnPlay.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    /** Method which updates the SeekBar primary progress by current song playing position*/
    private void primarySeekBarProgressUpdater() {
        float progFloat = (((float)mediaPlayer.getCurrentPosition()/lengthInMilli)*100);
        long progLong = mediaPlayer.getCurrentPosition();
        seekBar.setProgress((int) progFloat); // This math construction give a percentage of "was playing"/"song length"
        String prog = String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(progLong),
                TimeUnit.MILLISECONDS.toSeconds(progLong) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progLong)));
        txtStart.setText(""+prog);
//        txtStart.setTextSize(currentMilli);

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification,1000);
        }
    }


    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnPlay){
            try {
                mediaPlayer.setDataSource(getApplicationContext(), link);// setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
                mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
            } catch (Exception e) {
                e.printStackTrace();
            }

            lengthInMilli = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL
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
        /** MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
        btnPlay.setImageResource(R.drawable.ic_play);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.seekBar){
            /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
            if(mediaPlayer.isPlaying()){
                SeekBar sb = (SeekBar)v;
                int playPositionInMillisecconds = (lengthInMilli / 100) * sb.getProgress();
                mediaPlayer.seekTo(playPositionInMillisecconds);
            }
        }
        return false;
    }
}
