package com.example.musicplayer;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicplayer.Services.MediaService;

import java.util.ArrayList;
import java.util.Arrays;

public class SongsFragment extends Fragment implements SongAdapter.OnItemClickListener{

    RecyclerView songListView;
    SongAdapter songAdapter;
    private MediaPlayer mediaPlayer;

    public SongsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        songListView = view.findViewById(R.id.recycler_view);
        songListView.setHasFixedSize(true);

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

        int[] songResources = {R.raw.song_1, R.raw.song_2, R.raw.song_3};
        Intent serviceIntent = new Intent(getActivity(), MediaService.class);
        serviceIntent.setAction(MediaService.PLAY);

        serviceIntent.putExtra("songId", songResources[position]);
//        PendingIntent playPendingIntent = PendingIntent.getService(getActivity(), 2, serviceIntent, PendingIntent.FLAG_IMMUTABLE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent);
            Toast.makeText(getContext(), "debbug IF: "+ position, Toast.LENGTH_SHORT).show();
        } else {
            requireActivity().startService(serviceIntent);
            Toast.makeText(getContext(), "debbug ELSE", Toast.LENGTH_SHORT).show();

        }
    }


}