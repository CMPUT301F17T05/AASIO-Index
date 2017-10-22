package com.cmput301.t05.habilect;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {
    FragmentManager fragmentManager = getSupportFragmentManager();
    ViewPagerHistoryAdapter historyViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setTitle("Habilect - History");

        historyViewPagerAdapter = new ViewPagerHistoryAdapter(fragmentManager);
        ViewPager mHistoryViewPager = (ViewPager) findViewById(R.id.historyViewPager);
        mHistoryViewPager.setAdapter(historyViewPagerAdapter);

    }
}
