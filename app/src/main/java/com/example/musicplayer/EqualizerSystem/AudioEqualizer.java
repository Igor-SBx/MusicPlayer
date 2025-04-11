package com.example.musicplayer.EqualizerSystem;

public class AudioEqualizer {

    static {

        System.loadLibrary("Equalizer"); // Nome da biblioteca nativa

    }



    public native int applyEqualization(short[] audioData, int[] gains);
}
