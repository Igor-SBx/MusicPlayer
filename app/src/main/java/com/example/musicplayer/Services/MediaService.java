package com.example.musicplayer.Services;

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

import com.example.musicplayer.EqualizerSystem.AudioEqualizer;
import com.example.musicplayer.notification.MediaNotificationManager;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;

public class MediaService extends Service {

    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public static final String STOP = "STOP";

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private MediaSessionCompat mediaSession;
    private MediaNotificationManager notificationManager;
    private AudioEqualizer audioEqualizer;

    private int currentSongId = R.raw.song_1;
    private boolean isPlaying = false;
    private int numBands = 5;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MediaService", "Criado");
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mediaSession = new MediaSessionCompat(this, "MediaService");

        audioEqualizer = new AudioEqualizer();
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(mp -> {
            audioEqualizer.init(mediaPlayer.getAudioSessionId(), 44100, numBands);
            for (int i = 0; i < numBands; i++) {
                audioEqualizer.setBandGain(i, 1.0f);
            }
        });

        mediaPlayer.setOnCompletionListener(mediaPlayer -> releaseMediaPlayer());

        notificationManager = new MediaNotificationManager(this, mediaSession);
        startForeground(1, notificationManager.createNotification(isPlaying, currentSongId));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null) {
            String action = intent.getAction() == null ? "NULL_ACTION" : intent.getAction();
            Log.d("MediaService", "Recebida ação: " + action); // ← LOG PARA DEBUG
            switch (action) {
                case PLAY:
                    currentSongId = intent.getIntExtra("path", R.raw.song_1);
                    playAudio(currentSongId);
                    break;
                case PAUSE:
                    pauseAudio();
                    break;
                case STOP:
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
//            mediaPlayer = MediaPlayer.create(this, songId);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setOnCompletionListener(mp -> stopAudio());
//            mediaPlayer.start();
//            isPlaying = true;
//            Log.d("MediaService", "Tocando música: " + songId);
//            notificationManager.updateNotification(isPlaying, currentSongId);

            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(this, songId);
            mediaPlayer.start();
            isPlaying = true;
            notificationManager.updateNotification(isPlaying, currentSongId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            notificationManager.updateNotification(isPlaying, currentSongId);
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        isPlaying = false;
        stopForeground(true);
        stopSelf();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();

        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
