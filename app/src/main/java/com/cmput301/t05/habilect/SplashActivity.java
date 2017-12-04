package com.cmput301.t05.habilect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * This shows the splash screen of the app while data is being loaded
 * @author amwhitta
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, InitActivity.class);
        startActivity(intent);
        finish();
    }
}