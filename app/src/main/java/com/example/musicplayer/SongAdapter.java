package com.example.musicplayer;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> songs;
    private OnItemClickListener listener;

    // Interface para o clique
    public interface OnItemClickListener {
        void onItemClick(String songName, int position);
    }

    public SongAdapter(Context context, ArrayList<String> songs) {
        this.songs = songs;
        this.context = context;
    }

    // Metodo para setar o listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        Toast.makeText(context, "Musica", Toast.LENGTH_SHORT);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.songName.setText(songs.get(position));
        String song = songs.get(position);
        holder.bind(song, position, listener);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songName;
        ImageView imgPlaceholder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            songName = itemView.findViewById(R.id.song_name);
            imgPlaceholder = itemView.findViewById(R.id.song_icon);
        }

        public void bind(String music, int position, OnItemClickListener listener) {
            itemView.setOnClickListener(v->{
                if (listener != null) {
                    listener.onItemClick(music, position);
                }
            });
        }
    }

}
