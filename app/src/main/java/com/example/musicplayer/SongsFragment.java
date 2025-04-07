package com.example.musicplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

public class SongsFragment extends Fragment {

    RecyclerView songListView;
    SongAdapter songAdapter;

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
        songListView.setAdapter(songAdapter);

        return view;
    }
}