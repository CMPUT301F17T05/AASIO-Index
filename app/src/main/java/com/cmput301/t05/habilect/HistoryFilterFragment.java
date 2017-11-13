package com.cmput301.t05.habilect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author ioltuszy
 * @author rarog
 * The filter fragment displays all of the stored habit events and allows the user to filter
 * by various parameters
 */

public class HistoryFilterFragment extends Fragment {
    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_history_filter, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();

        ListView fragmentHabitTypeOptionsListView = rootView.findViewById(R.id.historyFilterFragmentListView);

        ArrayList<HabitEvent> eList = GSONController.GSON_CONTROLLER.loadHabitEventFromFile();

        HabitEventListAdapter eventListAdapter = new HabitEventListAdapter(eList, rootView.getContext());
        fragmentHabitTypeOptionsListView.setAdapter(eventListAdapter);


        return rootView;
    }
}
