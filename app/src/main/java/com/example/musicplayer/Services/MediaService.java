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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MediaService extends Service {

    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public static final String STOP = "STOP";

    private MediaPlayer mediaPlayer;
    private AudioPlayer audioPlayer;
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

//        audioEqualizer = new AudioEqualizer();
//        audioEqualizer = AudioEqualizer.getInstance();
//        audioEqualizer.init(mediaPlayer.getAudioSessionId(), 44100, numBands);

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
                    Log.d("MediaService", "Execução: " + action); // ← LOG PARA DEBUG
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

    public void playAudio(int songId) {


        //_____________________________
//        String path = "android.resource://" + getPackageName() + "/" + songId;
        if (audioPlayer != null && audioPlayer.isPlaying()) audioPlayer.stop();
        audioPlayer = new AudioPlayer();

        try{
            // Copia o recurso para arquivo temporário
            InputStream inputStream = getResources().openRawResource(songId);
            File tempFile = File.createTempFile("audio", ".mp3", getCacheDir());
            FileOutputStream out = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            inputStream.close();

            audioPlayer.play(tempFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MediaService", "Erro ao abrir/copiar áudio", e);
            //throw new RuntimeException(e);
        }
        Log.d("MediaService", "Executando Música");
        //_____________________________

//        try{
//            if (mediaPlayer != null) {
//                mediaPlayer.release();  // Libera o anterior
//            }
//
//            mediaPlayer = MediaPlayer.create(this, songId);
//            if (mediaPlayer != null){
//                mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());
//                isPlaying = true;
//
//                // Logs para debug
//                Log.d("MediaService", "MediaPlayer criado, ID do áudio: " + songId);
//                Log.d("Equalizer", "Iniciando equalizador...");
//
//
//                // ✅ Inicializa equalizador aqui!
//                audioEqualizer.init(mediaPlayer.getAudioSessionId(), 44100, numBands);
//                Log.d("Equalizer", "Equalizer inicializado com sessionId: " + mediaPlayer.getAudioSessionId());
//
//                for (int i = 0; i < numBands; i++) {
//                    audioEqualizer.setBandGain(i, 1.0f);
//                    Log.d("Equalizer", "Ganho da banda " + i + " setado para 1.0f");
//                }
//
//                mediaPlayer.start();
//                notificationManager.updateNotification(isPlaying, currentSongId);
//            } else{
//                Log.e("MediaService", "MediaPlayer retornou null!");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("MediaService", "Erro ao tocar áudio", e);
//        }

        //_____________________________
        
    }

    public void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            notificationManager.updateNotification(isPlaying, currentSongId);
        }
    }

    public void stopAudio() {
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
