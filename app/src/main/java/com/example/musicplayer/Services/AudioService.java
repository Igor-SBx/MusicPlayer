package com.example.musicplayer.Services;

// AudioService.java

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



// ... (Importações necessárias) ...
import androidx.core.app.NotificationCompat;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;

public class AudioService extends Service {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private MediaSessionCompat mediaSession;
    private static final String CHANNEL_ID = "AudioServiceChannel";

    private int notificationId = 1;



    @Override

    public void onCreate() {

        super.onCreate();
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mediaSession = new MediaSessionCompat(this, "AudioService");

        createNotificationChannel();
        startForeground(notificationId, createNotification());

    }

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String action = intent.getAction();

            // Processar ações como PLAY, PAUSE, STOP, etc.

            if(action != null)
                switch (action){
                    case "PLAY": playAudio(intent.getStringExtra("path")); break;
                    case "PAUSE": pauseAudio(); break;
                    case "STOP": stopAudio(); break;
                    default: break;
                }

        }
        return START_STICKY; // ou START_NOT_STICKY, dependendo das suas necessidades.
    }

    private void playAudio(String path){

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pauseAudio() {

        if(mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    private void stopAudio() {

        mediaPlayer.stop();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
        mediaSession.release();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    // ... (Métodos para criação e gerenciamento de notificações) ...

    private void createNotificationChannel(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Audio Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification(){

        Intent notificationIntent = new Intent(this, MainActivity.class); // Substitua MainActivity pelo nome da sua Activity

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Audio Player")
                .setContentText("Tocando música...")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Substitua pelo seu ícone
                .setContentIntent(pendingIntent)
                .build();
    }

}
