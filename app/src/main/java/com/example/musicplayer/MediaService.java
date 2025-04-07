package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Objects;

public class MediaService extends Service {

    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public static final String STOP = "STOP";

    private static final String CHANNEL_ID = "MusicPlayer";
    private static final String CHANNEL_NAME = "MusicPlayer";
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private MediaSessionCompat mediaSession;

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mediaSession = new MediaSessionCompat(this, "MediaService");

        createNotificationChannel();
        Notification notification = createNotification();
        startForeground(1, notification);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private Notification createNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            Notification notification = null;

            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentText("Tocando agora: ")
                    .setContentTitle("MusicPlayer")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build();

            return notification;
        }

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent != null){
            String action = intent.getAction() == null ? "NULL_ACTION" : intent.getAction();
            switch (action){
                case MediaService.PLAY:
                   playAudio(intent.getIntExtra("path", 18000000));
                    break;
                case MediaService.PAUSE:
                   pauseAudio();
                    break;
                case MediaService.STOP:
                    stopAudio();
                    break;
                default:
                    Log.e("MediaService", "Action inválida");
                    break;
            }
        }

        return START_STICKY;
    }

    private void playAudio(int songId) {
        try {
            mediaPlayer.reset();

            //mediaPlayer = MediaPlayer.create(this, songId);
            mediaPlayer = MediaPlayer.create(this, R.raw.song_1);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(completionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void pauseAudio() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    private void stopAudio() {
        mediaPlayer.stop();
        stopForeground(true);
        stopSelf();
    }

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.release();
        mediaPlayer = null;

        mediaSession.release();
        mediaSession = null;
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }
}