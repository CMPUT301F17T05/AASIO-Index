package com.cmput301.t05.habilect;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author ioltuszy
 */

public class ViewPagerFriendAdapter extends FragmentPagerAdapter {
    static final int NUM_PAGES = 2;

    public ViewPagerFriendAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new FriendHabitEventsFragment();
            case 1: return new FriendMapFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}

