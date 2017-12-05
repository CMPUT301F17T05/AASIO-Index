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
 * This list adapter allows habit types to be displayed in a list view
 * @author rarog
 */

public class HabitTypeListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<HabitType> typeList = new ArrayList<>();
    private Context context;

    HabitTypeListAdapter(ArrayList<HabitType> typeList, Context context) {
        this.typeList = typeList;
        this.context = context;
    }

    /**
     * Returns an integer representing the size of the habit type list
     */
    @Override
    public int getCount() {
        return typeList.size();
    }

    /**
     * Returns an Object item at a particular specified index
     */
    @Override
    public Object getItem(int i) {
        return typeList.get(i);
    }

    /**
     * Returns a Long at a particular specified index
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // inflates the view
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.habit_type_list_item, null);
        }

        // gets the counter object that we want to display
        final HabitType habitType = typeList.get(i);

        TextView title = view.findViewById(R.id.habitTypeListTitle);
        TextView plan = view.findViewById(R.id.habitTypeListPlan);

        title.setText(habitType.getTitle());
        plan.setText(habitType.getWeeklyPlanString());

        return view;
    }
}
