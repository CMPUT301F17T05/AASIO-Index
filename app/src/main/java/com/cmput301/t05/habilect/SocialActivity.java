package com.cmput301.t05.habilect;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * This activity handles the social fragments
 *
 * @author rarog
 */
public class SocialActivity extends AppCompatActivity {
    FragmentManager fragmentManager = getSupportFragmentManager();
    ViewPagerSocialAdapter socialPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        setTitle("Habilect - Social");

        Navigation.setup(findViewById(android.R.id.content));

        socialPageAdapter = new ViewPagerSocialAdapter(fragmentManager);
        ViewPager mSocialViewPager = (ViewPager) findViewById(R.id.socialViewPager);
        mSocialViewPager.setAdapter(socialPageAdapter);

    }
}
