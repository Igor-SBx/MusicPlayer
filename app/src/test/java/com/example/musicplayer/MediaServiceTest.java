package com.example.musicplayer;

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
