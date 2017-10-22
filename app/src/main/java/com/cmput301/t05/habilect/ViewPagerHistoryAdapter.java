package com.cmput301.t05.habilect;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Ian on 2017-10-22.
 */

public class ViewPagerHistoryAdapter extends FragmentPagerAdapter {
    static final int NUM_PAGES = 2;

    public ViewPagerHistoryAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new HistoryFilterFragment();
            case 1: return new HistoryMapFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}

