package com.cmput301.t05.habilect;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

/**
 * @author ioltuszy
 * @author rarog
 * The filter fragment displays all of the stored habit events and allows the user to filter
 * by various parameters
 */

public class HistoryFilterFragment extends Fragment {
    FragmentManager fragmentManager;
    ArrayList<HabitEvent> allHabitEvents;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_history_filter, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();

        ListView fragmentHabitTypeOptionsListView = rootView.findViewById(R.id.historyFilterFragmentListView);
        searchView = rootView.findViewById(R.id.historyFilterFragmentSearchView);

        allHabitEvents = GSONController.GSON_CONTROLLER.loadHabitEventFromFile();

        HabitEventListAdapter eventListAdapter = new HabitEventListAdapter(allHabitEvents, rootView.getContext());
        fragmentHabitTypeOptionsListView.setAdapter(eventListAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                eventListAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                eventListAdapter.getFilter().filter(s);
                return false;
            }
        });

        return rootView;
    }
}
