package com.example.musicplayer.Services;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.util.LogPrinter;

import com.example.musicplayer.EqualizerSystem.AudioEqualizer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class AudioPlayer {
    private static final String TAG = "AudioPlayer";

    private MediaExtractor extractor;
    private MediaCodec decoder;
    private AudioTrack audioTrack;

    private boolean isPlaying = false;
    private boolean isPaused = false; // Flag de pausa
    private AudioEqualizer audioEqualizer;
    public AudioPlayer(){
        audioEqualizer = AudioEqualizer.getInstance();
    }
    public boolean isPlaying(){
        return isPlaying;
    }
    public boolean isPaused() {
        return isPaused;
    }
    public void play(String filePath) {
        isPlaying = true;
        new Thread(() -> {
            try {
                extractor = new MediaExtractor();
                extractor.setDataSource(filePath);

                MediaFormat format = null;
                int trackIndex = -1;

                for (int i = 0; i < extractor.getTrackCount(); i++) {
                    MediaFormat f = extractor.getTrackFormat(i);
                    String mime = f.getString(MediaFormat.KEY_MIME);
                    if (mime.startsWith("audio/")) {
                        format = f;
                        trackIndex = i;
                        break;
                    }
                }

                if (format == null) {
                    Log.e(TAG, "Nenhuma faixa de áudio encontrada");
                    return;
                }

                extractor.selectTrack(trackIndex);
                String mime = format.getString(MediaFormat.KEY_MIME);
                decoder = MediaCodec.createDecoderByType(mime);
                decoder.configure(format, null, null, 0);
                decoder.start();

                int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                int channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                int channelConfig = channelCount == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;

                int bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
                audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        sampleRate,
                        channelConfig,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize,
                        AudioTrack.MODE_STREAM
                );
                audioTrack.play();

                audioEqualizer.init(audioTrack.getAudioSessionId(), sampleRate, 5);
//                Log.e("AudioPlayer", audioEqualizer)

                boolean isEOS = false;
                long timeoutUs = 10000;

                while (isPlaying) {
                    if (!isEOS) {
                        int inputBufferId = decoder.dequeueInputBuffer(timeoutUs);
                        if (inputBufferId >= 0) {
                            ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferId);
                            int sampleSize = extractor.readSampleData(inputBuffer, 0);
                            if (sampleSize < 0) {
                                decoder.queueInputBuffer(inputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                isEOS = true;
                            } else {
                                long presentationTimeUs = extractor.getSampleTime();
                                decoder.queueInputBuffer(inputBufferId, 0, sampleSize, presentationTimeUs, 0);
                                extractor.advance();
                            }
                        }
                    }

                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                    int outputBufferId = decoder.dequeueOutputBuffer(bufferInfo, timeoutUs);
                    if (outputBufferId >= 0) {
                        ByteBuffer outputBuffer = decoder.getOutputBuffer(outputBufferId);
                        if (outputBuffer != null) {
                            byte[] pcmData = new byte[bufferInfo.size];
                            outputBuffer.get(pcmData);
                            outputBuffer.clear();

                            // Conversão para short[] para aplicar o equalizador
                            short[] audioData = new short[pcmData.length / 2];
                            for (int i = 0; i < audioData.length; i++) {
                                audioData[i] = (short) ((pcmData[i * 2] & 0xFF) | (pcmData[i * 2 + 1] << 8));
                            }

                            // ➕ Aplicar equalização
                            int[] gains = AudioEqualizer.getInstance().getGainsForNative();
                            Log.d("EqualizerSync", "Gains enviados para native: " + Arrays.toString(gains));
                            Log.d("EqualizerSync", System.currentTimeMillis() + " - Gains: " + Arrays.toString(gains));
                            AudioEqualizer.getInstance().applyEqualization(audioData, gains);

                            // Converte de volta para byte[]
                            for (int i = 0; i < audioData.length; i++) {
                                pcmData[i * 2] = (byte) (audioData[i] & 0xFF);
                                pcmData[i * 2 + 1] = (byte) ((audioData[i] >> 8) & 0xFF);
                            }

                            audioTrack.write(pcmData, 0, pcmData.length);
                        }

                        decoder.releaseOutputBuffer(outputBufferId, false);
                    } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        Log.d(TAG, "Formato de saída alterado");
                    }
                }

                stop();

            } catch (IOException e) {
                Log.e(TAG, "Erro ao reproduzir áudio", e);
            }
        }).start();
    }

    public void stop() {
        isPlaying = false;

        if (audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            try {
                audioTrack.stop();
                audioTrack.release();
            } catch (IllegalStateException e) {
                Log.e(TAG, "AudioTrack não inicializado corretamente", e);
            }
            audioTrack = null;
        }

        if (decoder != null) {
            decoder.stop();
            decoder.release();
            decoder = null;
        }

        if (extractor != null) {
            extractor.release();
            extractor = null;
        }
    }
    public void pause() {
        if (isPlaying && !isPaused) {
            isPaused = true;
            if (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.pause();
                Log.d(TAG, "Reprodução pausada");
            }
        }
    }

    public void resume() {
        if (isPlaying && isPaused) {
            isPaused = false;
            if (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                audioTrack.play();
                Log.d(TAG, "Reprodução retomada");
            }
        }
    }


}