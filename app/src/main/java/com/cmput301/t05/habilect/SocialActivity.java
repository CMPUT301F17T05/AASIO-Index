package com.cmput301.t05.habilect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * @author ioltuszy
 */

public class SocialActivity extends AppCompatActivity {
    Activity currentActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        setTitle("Habilect - Social");

        final Button viewFriendButton = (Button) findViewById(R.id.viewFriendButton);
        viewFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentActivity, FriendActivity.class);
                startActivity(intent);
            }
        });
    }
}
