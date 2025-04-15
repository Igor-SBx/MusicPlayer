//
// Created by root on 11/04/2025.
//

// equalizer.cpp

#include <jni.h>
#include <vector>
#include <android/log.h>

#define LOG_TAG "equalizer"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

struct Equalizer {
    bool enabled = true;
    std::vector<float> bandGains;
};
static Equalizer* eq = nullptr;

extern "C" JNIEXPORT void JNICALL

Java_com_example_musicplayer_EqualizerSystem_AudioEqualizer_init(
        JNIEnv* env,
        jobject thiz,
        jint audioSessionId,
        jint sampleRate,
        jint numBands) {

    if (eq != nullptr) {
        delete eq;
    }

    eq = new Equalizer();
    eq->bandGains.resize(numBands, 1.0f);

    LOGD("Equalizer initialized with %d bands @ %d Hz", numBands, sampleRate);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_musicplayer_EqualizerSystem_AudioEqualizer_setBandGain(
        JNIEnv* env,
        jobject thiz,
        jint band,
        jfloat gain) {

    if (eq && band >= 0 && band < eq->bandGains.size()) {
        eq->bandGains[band] = gain;
    }
}

extern "C" JNIEXPORT jint JNICALL Java_com_example_musicplayer_EqualizerSystem_AudioEqualizer_applyEqualization(JNIEnv *env, jobject thiz, jshortArray audioData, jintArray gains){


    //Recuperando os dados

    jshort* audioDataPtr = env->GetShortArrayElements(audioData, 0);

    jint* gainsPtr = env->GetIntArrayElements(gains, 0);



    int numSamples = env->GetArrayLength(audioData);

    int numBands = env->GetArrayLength(gains);



    //Aplicando a equalização (Exemplo simples de ganho)

    for (int i = 0; i < numSamples; i++) {

        for (int j = 0; j < numBands; j++) {

            audioDataPtr[i] *= (double)(gainsPtr[j] / 1000.0); //Ajuste o ganho

        }

    }



    env->ReleaseShortArrayElements(audioData, audioDataPtr, 0);

    env->ReleaseIntArrayElements(gains, gainsPtr, 0);



    return numSamples;

}

