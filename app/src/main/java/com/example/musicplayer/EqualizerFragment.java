package com.example.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.musicplayer.EqualizerSystem.AudioEqualizer;
import com.example.musicplayer.databinding.FragmentEqualizerBinding;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EqualizerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */



public class EqualizerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MediaPlayer mediaPlayer;
    private SeekBar[] seekBars;
    private final int numBands = 5;
    private FragmentEqualizerBinding binding;
    public EqualizerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EqualizerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EqualizerFragment newInstance(String param1, String param2) {
        EqualizerFragment fragment = new EqualizerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Fragment Equalizer", "Criado");
        mediaPlayer = MediaPlayer.create(this.getContext(), R.raw.song_1);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_equalizer, container, false);

        // Infla o layout usando View Binding
       binding = FragmentEqualizerBinding.inflate(inflater, container, false);

        seekBars = new SeekBar[] {
                binding.seekBar, binding.seekBar1, binding.seekBar2,
                binding.seekBar3, binding.seekBar4
        };

        //____________________________________
        for (int i = 0; i < numBands; i++) {
            final int bandIndex = i;
            seekBars[i].setMax(100);
            seekBars[i].setProgress(50); // valor neutro (0.0f)

            seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        float gain = (progress / 100f) * 2f - 1f; // -1.0f a +1.0f
                        AudioEqualizer.getInstance().setBandGain(bandIndex, gain);
                        Log.d("Equalização", "SeekBar" + bandIndex);
                        Log.d("Equalização JNI", "Banda " + bandIndex + " ajustada para ganho: " + gain);
                        Log.d("EqualizerFragment", "SeekBar banda " + bandIndex + " ajustada para: " + progress);
                    }
                }

                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
        //____________________________________


//        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int level, boolean b) {
//                if(b) {
//                    float normalized = level / 100f; // Converte para 0.0 - 1.0
//                    float gain = normalized * 2f - 1f;  // -1.0 a +1.0
//
//                    AudioEqualizer eq = AudioEqualizer.getInstance();
//                    eq.setBandGain(0, gain); // Exemplo: altera a banda 0
//                    Log.d("Equalização", "SeekBar");
////                    mediaPlayer.setVolume(volume, volume);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

       return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}