package com.cmput301.t05.habilect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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

        final Button navigateHabitTypes = (Button) findViewById(R.id.navigateHabitTypes);
        navigateHabitTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentActivity, ViewHabitTypesActivity.class);
                startActivity(intent);
            }
        });
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