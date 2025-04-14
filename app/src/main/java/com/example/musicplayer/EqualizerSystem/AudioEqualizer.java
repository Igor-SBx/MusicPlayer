package com.example.musicplayer.EqualizerSystem;

public class AudioEqualizer {

    static {

        System.loadLibrary("musicplayer"); // Nome da biblioteca nativa

    }


    // Inicializa o equalizador com configurações básicas
    public native void init(int audioSessionId, int sampleRate, int numBands);
    public native int applyEqualization(short[] audioData, int[] gains);

}
