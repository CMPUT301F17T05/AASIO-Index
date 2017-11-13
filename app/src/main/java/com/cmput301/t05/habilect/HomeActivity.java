package com.cmput301.t05.habilect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

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

        Navigation.setup(findViewById(android.R.id.content));
    }
}