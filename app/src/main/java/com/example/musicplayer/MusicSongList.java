package com.example.musicplayer;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class MusicSongList {
    public static List<Music> getPredefinedList(Context context) {
        List<Music> songs = new ArrayList<>();
        songs.add(new Music("Música 1", R.raw.song_1));
        songs.add(new Music("Música 2", R.raw.song_2));
        songs.add(new Music("Música 3", R.raw.song_3));
        return songs;
    }
}
