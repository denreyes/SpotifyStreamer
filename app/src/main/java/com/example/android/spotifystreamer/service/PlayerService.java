package com.example.android.spotifystreamer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.android.spotifystreamer.R;
import com.example.android.spotifystreamer.activities.PlayerActivity;
import com.example.android.spotifystreamer.fragments.PlayerFragment;
import com.example.android.spotifystreamer.object.TopObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DJ on 6/26/2015.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {
    private static final String LOG_TAG = PlayerService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ArrayList<TopObject> list;
    private int pos;

    int mediaPos;
    int mediaMax;
    private final Handler handler = new Handler();
    private static int songEnded;
    public static final String BROADCAST_ACTION = "com.example.android.spotifystreamer.service.seekprogress";
    Intent seekIntent;

    @Override
    public void onCreate() {
        seekIntent = new Intent(BROADCAST_ACTION);

        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        registerReceiver(seekReceiver, new IntentFilter(PlayerFragment.BROADCAST_SEEKBAR));
        registerReceiver(pauseplayReceiver, new IntentFilter(PlayerFragment.BROADCAST_PAUSEPLAY));

        list = intent.getParcelableArrayListExtra("TOP_OBJECT");
        pos = intent.getIntExtra("POSITION", 0);
        initNotification();
        mediaPlayer.reset();
        if (!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.setDataSource(list.get(pos).trackPlay);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        setupHandler();
        return START_STICKY;
    }

    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.post(sendUpdatesToUI);
//        handler.postDelayed(sendUpdatesToUI, 1000);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        @Override
        public void run() {
            LogMediaPosition();
            handler.post(this);
        }
    };

    private void LogMediaPosition(){
        if(mediaPlayer.isPlaying()){
            mediaPos = mediaPlayer.getCurrentPosition();

            mediaMax = mediaPlayer.getDuration();
            seekIntent.putExtra("counter", mediaPos);
            seekIntent.putExtra("mediamax", mediaMax);
            sendBroadcast(seekIntent);
        }
    }

    private BroadcastReceiver seekReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);
        }
    };

    public void updateSeekPos(Intent intent) {
        int seekPos = intent.getIntExtra("seekpos", 0);
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(sendUpdatesToUI);
            mediaPlayer.seekTo(seekPos);
            setupHandler();
        }

    }

    private BroadcastReceiver pauseplayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mediaPlayer.isPlaying()){
                pauseMedia();
            }else{
                playMedia();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }

        cancelNotification();
        handler.removeCallbacks(sendUpdatesToUI);
        unregisterReceiver(pauseplayReceiver);
        unregisterReceiver(seekReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "Media error: Not valid for progressive playback " + extra,
                        Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "Media error: Server Died " + extra,
                        Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "Media error: Unknown " + extra,
                        Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        playMedia();
    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void stopMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void initNotification() {
        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(notificationService);
        Notification notification = new Notification(R.drawable.ic_play,
                "Music in Service",System.currentTimeMillis());
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(getApplicationContext(), list.get(pos).trackTitle, list.get(pos).trackArtist, contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID,notification);
    }

    private void cancelNotification() {
        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(notificationService);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}