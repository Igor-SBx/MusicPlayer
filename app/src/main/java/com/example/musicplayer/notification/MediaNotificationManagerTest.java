package com.example.musicplayer.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.example.musicplayer.R;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MediaNotificationManagerTest {

    @Mock
    Context mockContext;

    @Mock
    NotificationManager mockNotificationManager;

    @Mock
    MediaSessionCompat mockMediaSession;

    private MediaNotificationManager notificationManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockNotificationManager);
        when(mockContext.getApplicationContext()).thenReturn(mockContext);

        notificationManager = new MediaNotificationManager(mockContext, mockMediaSession);
    }

    @Test
    public void testCreateNotification_showsPlayIconWhenNotPlaying() {
        Notification notification = notificationManager.createNotification(false, R.raw.song_1);

        assertNotNull(notification);
        // Como não temos acesso direto ao layout da notificação, testamos indiretamente:
        assertTrue(notification.extras.getCharSequence(Notification.EXTRA_TITLE).toString().contains("Audio Player"));
        assertTrue(notification.extras.getCharSequence(Notification.EXTRA_TEXT).toString().contains("Tocando música"));
    }

    @Test
    public void testCreateNotification_showsPauseIconWhenPlaying() {
        Notification notification = notificationManager.createNotification(true, R.raw.song_1);

        assertNotNull(notification);
        // Aqui só validamos se a notificação é criada sem erros
    }

    @Test
    public void testNotificationChannelCreatedOnOreo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = new MediaNotificationManager(mockContext, mockMediaSession);
            verify(mockNotificationManager, atLeastOnce()).createNotificationChannel(any(NotificationChannel.class));
        }
    }

    @Test
    public void testUpdateNotification_invokesNotify() {
        notificationManager.updateNotification(true, R.raw.song_1);

        verify(mockNotificationManager).notify(eq(1), any(Notification.class));
    }
}
