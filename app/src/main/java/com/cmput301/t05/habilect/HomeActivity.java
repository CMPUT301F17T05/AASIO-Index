package com.cmput301.t05.habilect;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends FragmentActivity {
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Button addHabitButton = (Button) findViewById(R.id.addHabitButton);
        addHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AddHabitDialog addHabitDialog = new AddHabitDialog();
                addHabitDialog.setOnAddHabitListener(new OnAddHabitListener() {
                    @Override
                    public void OnAdded() {
                        // TODO: implement OnAdded
                    }

                    @Override
                    public void OnCancelled() {
                        // TODO: implement OnCancelled
                    }
                });
                addHabitDialog.show(fragmentManager, "addHabitDialog");
            }
        });

        final Button viewHabitButton = (Button) findViewById(R.id.viewHabitButton);
        viewHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ViewHabitDialog viewHabitDialog = new ViewHabitDialog();
                viewHabitDialog.setOnViewHabitListener(new OnViewHabitListener() {
                    @Override
                    public void OnDeleted() {
                        // TODO: implement OnDeleted
                    }

                    @Override
                    public void OnSaved() {
                        // TODO: implement OnSaved
                    }

                    @Override
                    public void OnFollowed() {
                        // TODO: implement OnFollowed
                    }

                    @Override
                    public void OnCancelled() {
                        // TODO: implement OnCancelled
                    }
                });
                viewHabitDialog.show(fragmentManager, "viewHabitDialog");
            }
        });
    }
}
