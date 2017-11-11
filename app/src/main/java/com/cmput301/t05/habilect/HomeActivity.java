package com.cmput301.t05.habilect;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author ioltuszy
 */

public class HomeActivity extends AppCompatActivity {
    Activity currentActivity = this;

    FragmentManager fragmentManager = getSupportFragmentManager();
    ViewPagerHomeAdapter homeViewPagerAdapter;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeViewPagerAdapter = new ViewPagerHomeAdapter(fragmentManager);
        ViewPager mHomeViewPager = (ViewPager) findViewById(R.id.homeViewPager);
        mHomeViewPager.setAdapter(homeViewPagerAdapter);

        final Button navigateHistoryButton = (Button) findViewById(R.id.navigateHistory);
        navigateHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentActivity, HistoryActivity.class);
                startActivity(intent);
            }
        });
        final Button navigateSocialButton = (Button) findViewById(R.id.navigateSocial);
        navigateSocialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentActivity, SocialActivity.class);
                startActivity(intent);
            }
        });
        final Button navigateProfileButton = (Button) findViewById(R.id.navigateProfile);
        navigateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentActivity, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

}