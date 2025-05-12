/* package com.example.musicplayer;

import android.content.Intent;

import com.example.musicplayer.Services.MediaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class MediaServiceTest {

    private MediaService service;

    @BeforeEach
    void setup() {
        // Usamos um "spy" para podermos testar chamadas reais e falsas
        service = Mockito.spy(new MediaService());
    }

    @Test
    void testPlayActionCallsPlayAudio() {
        Intent intent = new Intent();
        intent.setAction(MediaService.PLAY);
        intent.putExtra("path", 123);  // valor fictício

        doNothing().when(service).playAudio(anyInt());

        service.onStartCommand(intent, 0, 0);

        verify(service, times(1)).playAudio(123);
    }

    @Test
    void testPauseActionCallsPauseAudio() {
        Intent intent = new Intent();
        intent.setAction(MediaService.PAUSE);

        doNothing().when(service).pauseAudio();

        service.onStartCommand(intent, 0, 0);

        verify(service, times(1)).pauseAudio();
    }

    @Test
    void testStopActionCallsStopAudio() {
        Intent intent = new Intent();
        intent.setAction(MediaService.STOP);

        doNothing().when(service).stopAudio();

        service.onStartCommand(intent, 0, 0);

        verify(service, times(1)).stopAudio();
    }
}
 */

 package com.example.musicplayer;

import android.app.Notification;
import android.content.Intent;
import android.media.MediaPlayer;

import androidx.core.app.NotificationCompat;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.musicplayer.Services.AudioPlayer;
import com.example.musicplayer.Services.MediaService;
import com.example.musicplayer.notification.MediaNotificationManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Spy
    @InjectMocks
    private MediaService service;

    @Mock
    private MediaNotificationManager mockNotificationManager;

    @Mock
    private MediaPlayer mockMediaPlayer;

    @Mock
    private AudioPlayer mockAudioPlayer;

    @BeforeEach
    void setUp() throws Exception {
        // Inject our mocks into the private fields of MediaService
        setPrivateField(service, "notificationManager", mockNotificationManager);

//        service.setNotificationManager(mock(MediaNotificationManager.class));

//        setPrivateField(service, "audioPlayer", mockMediaPlayer);
        setPrivateField(service, "audioPlayer", mockAudioPlayer);

//        setPrivateField(service, "audioPlayer", mock(AudioPlayer.class));
    }

    // -- onStartCommand tests ------------------------------------------------

    @Test
    void onStartCommand_withPlayAction_invokesPlayAudio() {
        // spy the service so we can verify playAudio(...) is called
        MediaService spy = Mockito.spy(service);

        Intent intent = new Intent();
        intent.setAction(MediaService.PLAY);
        intent.putExtra("path", 42);

        spy.onStartCommand(intent, /*flags=*/0, /*startId=*/0);

        verify(spy, times(1)).playAudio(42);
    }

    @Test
    void onStartCommand_withPauseAction_invokesPauseAudio() {
        MediaService spy = Mockito.spy(service);

        Intent intent = new Intent();
        intent.setAction(MediaService.PAUSE);

        spy.onStartCommand(intent, 0, 0);

        verify(spy, times(1)).pauseAudio();
    }

    @Test
    void onStartCommand_withStopAction_invokesStopAudio() {
        MediaService spy = Mockito.spy(service);

        Intent intent = new Intent();
        intent.setAction(MediaService.STOP);

        spy.onStartCommand(intent, 0, 0);

        verify(spy, times(1)).stopAudio();
    }

    // -- pauseAudio() tests ---------------------------------------------------

    @Test
    void pauseAudio_whenMediaPlayerPlaying_pausesAndUpdatesNotification() throws Exception {
        // Arrange
        when(mockAudioPlayer.isPlaying()).thenReturn(true);
        // service.isPlaying defaults to false, but notification update reads it
        setPrivateField(service, "isPlaying", true);

        // Act
        service.pauseAudio();

        // Assert
        verify(mockAudioPlayer).pause();
        // isPlaying should now be false
        boolean newState = (boolean) getPrivateField(service, "isPlaying");
        assertEquals(false, newState);
        verify(mockNotificationManager).updateNotification(false, 
            (Integer) getPrivateField(service, "currentSongId"));
    }

    @Test
    void pauseAudio_whenMediaPlayerNotPlaying_doesNothing() {
        when(mockAudioPlayer.isPlaying()).thenReturn(false);

        service.pauseAudio();

        verify(mockAudioPlayer, never()).pause();
        verify(mockNotificationManager, never()).updateNotification(anyBoolean(), anyInt());
    }

    // -- stopAudio() tests ----------------------------------------------------

    @Test
    void stopAudio_always_stopsPlayerAndService() throws Exception {
        // Arrange: mediaPlayer non-null
        // Act
        service.stopAudio();

        // Assert
        verify(mockAudioPlayer).stop();

        // Because stopForeground() and stopSelf() are final on Service, spy them
        MediaService spy = Mockito.spy(service);
        setPrivateField(spy, "mediaPlayer", mockAudioPlayer);

        spy.stopAudio();
        verify(spy).stopForeground(true);
        verify(spy).stopSelf();

        // isPlaying reset
        assertEquals(false, getPrivateField(service, "isPlaying"));
    }

    // ====================
    // Reflection utilities
    // ====================

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static Object getPrivateField(Object target, String fieldName) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(target);
    }
}
