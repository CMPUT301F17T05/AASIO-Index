package com.cmput301.t05.habilect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Andrea on 12-11-17.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserProfile userIntialization = new UserProfile(getApplicationContext());

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}