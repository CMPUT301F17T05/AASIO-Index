package com.cmput301.t05.habilect;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ioltuszy
 * @author rarog
 * The filter fragment displays all of the stored habit events and allows the user to filter
 * by various parameters
 */

public class HistoryFilterFragment extends Fragment {
    private UserAccount userAccount;
    private Context context;
    FragmentManager fragmentManager;
    List<HabitType> allHabitTypes;
    ArrayList<HabitEvent> allHabitEvents;
    SearchView searchView;
    Spinner filterSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_history_filter, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();

        context = getContext();
        userAccount = new UserAccount().load(context);

        ListView fragmentHabitTypeOptionsListView = rootView.findViewById(R.id.historyFilterFragmentListView);
        searchView = rootView.findViewById(R.id.historyFilterFragmentSearchView);
        filterSpinner = rootView.findViewById(R.id.historyFilterFragmentSpinner);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_options, R.layout.history_filter_spinner_item);
        filterAdapter.setDropDownViewResource(R.layout.history_filter_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);

        allHabitTypes = userAccount.getHabits();
        allHabitEvents = new ArrayList<>();
        loadAllHabitEvents();

        HabitEventListAdapter eventListAdapter = new HabitEventListAdapter(allHabitEvents, rootView.getContext());
        fragmentHabitTypeOptionsListView.setAdapter(eventListAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String spinnerOption = (String) filterSpinner.getSelectedItem();
                switch (spinnerOption) {
                    case "Habit Comment":
                        eventListAdapter.option = "Comment";
                        eventListAdapter.getFilter().filter(s);
                        break;
                    case "Habit Type":
                        eventListAdapter.option = "Type";
                        eventListAdapter.getFilter().filter(s);
                        break;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String spinnerOption = (String) filterSpinner.getSelectedItem();
                switch (spinnerOption) {
                    case "Habit Comment":
                        eventListAdapter.option = "Comment";
                        eventListAdapter.getFilter().filter(s);
                        break;
                    case "Habit Type":
                        eventListAdapter.option = "Type";
                        eventListAdapter.getFilter().filter(s);
                        break;
                }
                return false;
            }
        });

        return rootView;
    }

    private void loadAllHabitEvents() {
        Iterator<HabitType> iterator = allHabitTypes.iterator();
        while(iterator.hasNext()) {
            HabitType habit = iterator.next();
            ArrayList<HabitEvent> eventList = habit.getHabitEvents();
            allHabitEvents.addAll(eventList);
        }
    }
}
