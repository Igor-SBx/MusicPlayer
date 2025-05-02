package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.musicplayer.MediaService;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupPager();
        setupService();
    }

    private void setupPager() {
        ViewPager pager = findViewById(R.id.pager);
        TabLayout navbar = findViewById(R.id.navbar);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        navbar.setupWithViewPager(pager);
    }

    private void setupService(){
        Intent mediaServiceIntent = new Intent(this, MediaService.class);
        mediaServiceIntent.setAction(MediaService.PLAY);
        //mediaServiceIntent.putExtra("path", R.raw.song_3);

        startService(mediaServiceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }

    private void stopService(){
        Intent mediaService = new Intent(this, MediaService.class);
        stopService(mediaService);
    }
}