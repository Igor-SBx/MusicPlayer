package com.example.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
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

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int level, boolean b) {
                if(b) {
                    float volume = level / 100f; // Converte para 0.0 - 1.0
                    mediaPlayer.setVolume(volume, volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

       return binding.getRoot();
    }
}