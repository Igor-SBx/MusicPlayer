package com.example.musicplayer.audio.decoder;

import android.media.*;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioDecoder {
    private static final String TAG = "AudioDecoder";

    public interface Callback {
        void onAudioDecoded(short[] pcmSamples, int sampleRate);
    }

    public void decode(String audioPath, Callback callback) {
        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(audioPath);
        } catch (IOException e) {
            Log.e(TAG, "Erro ao abrir o áudio", e);
            return;
        }

        int audioTrackIndex = -1;
        MediaFormat format = null;

        for (int i = 0; i < extractor.getTrackCount(); i++) {
            format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                audioTrackIndex = i;
                break;
            }
        }

        if (audioTrackIndex == -1) {
            Log.e(TAG, "Nenhuma faixa de áudio encontrada.");
            return;
        }

        extractor.selectTrack(audioTrackIndex);
        MediaCodec codec = null;
        try {
            codec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
        } catch (IOException e) {
            Log.e(TAG, "Erro ao criar MediaCodec", e);
            return;
        }

        codec.configure(format, null, null, 0);
        codec.start();

        ByteBuffer[] inputBuffers = codec.getInputBuffers();
        ByteBuffer[] outputBuffers = codec.getOutputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        boolean isEOS = false;
        while (!isEOS) {
            int inIndex = codec.dequeueInputBuffer(10000);
            if (inIndex >= 0) {
                ByteBuffer buffer = inputBuffers[inIndex];
                int sampleSize = extractor.readSampleData(buffer, 0);

                if (sampleSize < 0) {
                    codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    isEOS = true;
                } else {
                    long presentationTimeUs = extractor.getSampleTime();
                    codec.queueInputBuffer(inIndex, 0, sampleSize, presentationTimeUs, 0);
                    extractor.advance();
                }
            }

            int outIndex = codec.dequeueOutputBuffer(info, 10000);
            while (outIndex >= 0) {
                ByteBuffer outBuffer = outputBuffers[outIndex];
                byte[] pcmBytes = new byte[info.size];
                outBuffer.get(pcmBytes);
                outBuffer.clear();

                short[] samples = new short[pcmBytes.length / 2];
                for (int i = 0; i < samples.length; i++) {
                    samples[i] = (short) ((pcmBytes[i * 2] & 0xFF) | (pcmBytes[i * 2 + 1] << 8));
                }

                callback.onAudioDecoded(samples, format.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                codec.releaseOutputBuffer(outIndex, false);
                outIndex = codec.dequeueOutputBuffer(info, 0);
            }
        }

        codec.stop();
        codec.release();
        extractor.release();
    }
}
