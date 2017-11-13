package com.cmput301.t05.habilect;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * @author ioltuszy
 */

public class FriendActivity extends AppCompatActivity {
    FragmentManager fragmentManager = getSupportFragmentManager();
    ViewPagerFriendAdapter friendViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        setTitle("Habilect - Friend");

        Navigation.setup(findViewById(android.R.id.content));

        friendViewPagerAdapter = new ViewPagerFriendAdapter(fragmentManager);
        ViewPager mFriendViewPager = (ViewPager) findViewById(R.id.friendViewPager);
        mFriendViewPager.setAdapter(friendViewPagerAdapter);

    }
}
