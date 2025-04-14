package com.example.musicplayer;

public class Music {

    private String title;
    private int rawResourceId;

    public Music(String title, int rawResourceId) {
        this.title = title;
        this.rawResourceId = rawResourceId;
    }

    public String getTitle() { return title; }
    public int getRawResourceId() { return rawResourceId; }
}
