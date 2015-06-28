package com.example.android.spotifystreamer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import com.example.android.spotifystreamer.R;
import com.example.android.spotifystreamer.activities.PlayerActivity;

import java.io.IOException;

/**
 * Created by DJ on 6/26/2015.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {
    private static final String LOG_TAG = PlayerService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String link;

    @Override
    public void onCreate() {
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
        initNotification();

        link = intent.getExtras().getString("LINK");
        mediaPlayer.reset();
        if (!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.setDataSource(link);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return START_STICKY;
    }

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

    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
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
        notification.setLatestEventInfo(getApplicationContext(), "Title", "Content", contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID,notification);
    }

    private void cancelNotification() {
        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(notificationService);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}