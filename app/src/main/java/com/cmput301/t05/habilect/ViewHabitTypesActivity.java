package com.cmput301.t05.habilect;

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

public class ViewHabitTypesActivity extends AppCompatActivity {

    private ListView habitTypeList;
    private ArrayList<HabitType> habit_types;

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

        setTitle("Habilect - Habit Types");

        Navigation.setup(findViewById(android.R.id.content));

        habitTypeList = (ListView) findViewById(R.id.habitTypeListView);

        habitTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ViewHabitTypesActivity.this, HabitTypeActivity.class);
                intent.putExtra("ClickedHabitType", habit_types.get(i));
                startActivity(intent);
            }
        });

    }

    /**
     * gets all of the user's habit types locally or from elasticsearch (to be implemented) and
     * puts them in the ListView
     *
     * @see HabitType
     */
    @Override
    protected void onStart() {
        super.onStart();

        /*ElasticsearchHabitTypeController.GetHabitTypeTask getHabitTypeTask =
          new ElasticsearchHabitTypeController.GetHabitTypeTask();
          habit_types = getHabitTypeTask.execute(null);*/
        //boolean[] plan1 = {true, true, true, true, false, false, false};
        //boolean[] plan2 = {false, false, false, false, false, false, true};
        //HabitType habit1 = new HabitType("clean", "keep the house nice", new Date(), plan1);
        //HabitType habit2 = new HabitType("plan meals", "to save time", new Date(), plan2);

        habit_types = new ArrayList<>();
        habit_types = GSONController.GSON_CONTROLLER.loadHabitTypeFromFile();
        ArrayAdapter<HabitType> adapter = new ArrayAdapter<>(this, R.layout.habit_type_list_item, habit_types);
        habitTypeList.setAdapter(adapter);

        //Log.d("Debugging", "habit types list:" + habit_types.get(0).toString());
        adapter.notifyDataSetChanged();
    }
}
