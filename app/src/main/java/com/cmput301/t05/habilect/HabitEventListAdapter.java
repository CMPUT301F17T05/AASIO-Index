package com.cmput301.t05.habilect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Oliver on 12/11/2017.
 */

public class HabitEventListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<HabitEvent> eventList = new ArrayList<>();
    private Context context;

    HabitEventListAdapter(ArrayList<HabitEvent> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int i) {
        return eventList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // inflates the view
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.habit_event_row, null);
        }
        // gets the counter object that we want to display
        final HabitEvent event = eventList.get(i);

        TextView habitTitle = view.findViewById(R.id.habitEventRowTitle);
        TextView habitDate = view.findViewById(R.id.habitEventRowDate);

        habitTitle.setText(event.getHabitType());
        habitDate.setText(event.getCompletionDate().toString());

        return view;
    }
}
