package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This activity shows the user a list of all of their habit types
 */
public class ViewHabitTypesActivity extends AppCompatActivity {

    private ListView habitTypeList;
    private ArrayList<HabitType> habit_types;
    private Context context;
    private UserProfile user_profile;

    /**
     * Passes a habit type to the HabitTypeActivity if it is clicked on in the ListView
     *
     * @param savedInstanceState
     * @see HabitType
     * @see HabitTypeActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit_types);

        context = getApplicationContext();
        user_profile = new UserProfile(context);

        setTitle("Habilect - Habit Types");
        Navigation.setup(findViewById(android.R.id.content));
        habitTypeList = (ListView) findViewById(R.id.habitTypeListView);

        habitTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ViewHabitTypesActivity.this, HabitTypeActivity.class);
                intent.putExtra("ClickedHabitType", habit_types.get(i).getTitle());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserAccount localUser = new UserAccount().load(context);
        habit_types = new ArrayList<>(localUser.getHabits());
        HabitTypeListAdapter adapter = new HabitTypeListAdapter(habit_types, this);
        habitTypeList.setAdapter(adapter);
    }
}
