// MediaNotificationManager.java
package com.example.musicplayer.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaSessionCompat;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.Services.MediaService;

public class MediaNotificationManager {

    private static final String CHANNEL_ID = "MusicPlayer";
    private static final String CHANNEL_NAME = "Music Player";
    private final Context context;
    private final MediaSessionCompat mediaSession;
    private final NotificationManager notificationManager;

    public MediaNotificationManager(Context context, MediaSessionCompat mediaSession) {
        this.context = context;
        this.mediaSession = mediaSession;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notification createNotification(boolean isPlaying, int currentSongId) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE);

        // Ação Play/Pause
        Intent actionIntent = new Intent(context, MediaService.class);
        PendingIntent actionPendingIntent;
        String actionTitle;
        int actionIcon;

        if (isPlaying) {
            actionIntent.setAction(MediaService.PAUSE);
            actionPendingIntent = PendingIntent.getService(context, 1, actionIntent, PendingIntent.FLAG_IMMUTABLE);
            actionTitle = "Pause";
            actionIcon = R.drawable.ic_pause;
        } else {
            actionIntent.setAction(MediaService.PLAY);
            actionIntent.putExtra("path", currentSongId);
            actionPendingIntent = PendingIntent.getService(context, 1, actionIntent, PendingIntent.FLAG_IMMUTABLE);
            actionTitle = "Play";
            actionIcon = R.drawable.ic_play_arrow;
        }

        // Ação Stop
        Intent stopIntent = new Intent(context, MediaService.class);
        stopIntent.setAction(MediaService.STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(context, 2, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Reproduzindo música")
                .setContentText("Minha faixa personalizada 🎵")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentIntent)
                .addAction(actionIcon, actionTitle, actionPendingIntent)
                .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                .setStyle(new MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1))
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    public void updateNotification(boolean isPlaying, int currentSongId) {
        Notification notification = createNotification(isPlaying, currentSongId);
        notificationManager.notify(1, notification);
    }
}
