package com.example.musicplayer.EqualizerSystem;

public class AudioEqualizer {

    static {

        System.loadLibrary("equalizer"); // Nome da biblioteca nativa

    }


    // Inicializa o equalizador com configurações básicas
    public native void init(int audioSessionId, int sampleRate, int numBands);
    // Define o ganho para uma banda específica (0 a numBands-1)
    public native void setBandGain(int band, float gain);
    public native int applyEqualization(short[] audioData, int[] gains);

}
