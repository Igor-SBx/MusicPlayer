package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class PagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;
    private ArrayList<String> tabNames;

    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);



        Fragment songsFragment = new SongsFragment();
        String songsFragmentTitle = "Músicas";

        Fragment equalizerFragment = new EqualizerFragment();
        String equalizerFragmentTitle = "Equalizador";

        this.fragments = getList(songsFragment, equalizerFragment);
        this.tabNames = getList(songsFragmentTitle, equalizerFragmentTitle);
    }

    @NonNull
    private static<T> ArrayList<T> getList(T songsFragment, T equalizerFragment) {
        return new ArrayList<>(Arrays.asList(songsFragment, equalizerFragment));
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames.get(position);
    }
}