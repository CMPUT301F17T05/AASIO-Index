package com.cmput301.t05.habilect;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Oliver on 03/12/2017.
 */

public class ViewPagerSocialAdapter extends FragmentPagerAdapter {
    static final int NUM_PAGES = 3;

    public ViewPagerSocialAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new SocialFeedFragment();
            case 1: return new SocialFollowingFragment();
            case 2: return new SocialFollowerFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
