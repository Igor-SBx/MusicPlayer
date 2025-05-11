package com.example.musicplayer.EqualizerSystem;

import android.util.Log;

import java.util.Arrays;

public class AudioEqualizer {

    static {

        System.loadLibrary("equalizer"); // Nome da biblioteca nativa

    }

    private static AudioEqualizer instance;
    private float[] bandGains;

    private AudioEqualizer() {
        bandGains = new float[5]; // Use o número de bandas apropriado
        for (int i = 0; i < bandGains.length; i++) {
            bandGains[i] = 1.0f;
        }
    } // Impede instanciação externa

    public static synchronized AudioEqualizer getInstance() {
        if (instance == null) {
            instance = new AudioEqualizer();
        }
        return instance;
    }

    // Inicializa o equalizador com configurações básicas
    public native void init(int audioSessionId, int sampleRate, int numBands);
    // Define o ganho para uma banda específica (0 a numBands-1)
    public native void nativeSetBandGain(int band, float gain);

    // Novo método Java para manter os ganhos sincronizados
    public void setBandGain(int band, float gain) {
        if (band >= 0 && band < bandGains.length) {
            bandGains[band] = gain;
            nativeSetBandGain(band, gain);
//            Log.d("EqualizerSync", "setBandGain: banda " + band + " => " + gain);
            Log.d("EqualizerSync", "setBandGain: banda " + band + " => " + gain + " [" + this + "]");
        }
    }

    public int[] getGainsForNative() {
        Log.d("EqualizerSync", "getGainsForNative() chamada [" + this + "]");
        int[] gainsInt = new int[bandGains.length];
        for (int i = 0; i < bandGains.length; i++) {
            gainsInt[i] = (int)(bandGains[i] * 1000);
        }
        Log.d("EqualizerSync", "getGainsForNative retornando: " + Arrays.toString(gainsInt));
        return gainsInt;
    }
    public native int applyEqualization(short[] audioData, int[] gains);

}
