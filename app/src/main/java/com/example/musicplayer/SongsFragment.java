package com.example.musicplayer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class SongsFragment extends Fragment implements SongAdapter.OnItemClickListener{

    RecyclerView songListView;
    SongAdapter songAdapter;
    private MediaService mediaService;
    public SongsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        songListView = view.findViewById(R.id.recycler_view);
        songListView.setHasFixedSize(true);
        mediaService = new MediaService();
        ArrayList<String> songList = new ArrayList<>(
                Arrays.asList(
                        "Bright is the Ring of Words - Ron Meixsell and Wahneta Meixsell",
                        "I Feel Great - Jeremy Korpas",
                        "Strong Self Esteem - Jeremy Korpas"
                        )
        );
        songAdapter = new SongAdapter(getContext(), songList);
        songAdapter.setOnItemClickListener(this);
        songListView.setAdapter(songAdapter);

        return view;
    }

    @Override
    public void onItemClick(String songName, int position){
        
        Intent serviceIntent = new Intent(getActivity(), MediaService.class);
        serviceIntent.setAction(MediaService.PLAY);
        //        mediaService.onStartCommand();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent);
        } else {
            requireActivity().startService(serviceIntent);
        }
    }


}