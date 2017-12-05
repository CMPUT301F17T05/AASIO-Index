package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
    ImageButton mapButton;

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

        // this array adapter shows the user the options that they can filter by
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_options, R.layout.history_filter_spinner_item);
        filterAdapter.setDropDownViewResource(R.layout.history_filter_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);

        // loads all of the users habit events from their account and sets an adapter to display in
        // a list view
        allHabitTypes = userAccount.getHabits();
        allHabitEvents = new ArrayList<>();
        loadAllHabitEvents();

        HabitEventListAdapter eventListAdapter = new HabitEventListAdapter(allHabitEvents, rootView.getContext());
        fragmentHabitTypeOptionsListView.setAdapter(eventListAdapter);

        // when the text in the search with is changed or submitted, the event list array
        // is filtered by either to event comments or their habit type (base on user selection)
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

        mapButton = rootView.findViewById(R.id.historyFilterMapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Events", eventListAdapter.getAllEvents());
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });

        return rootView;
    }

    /**
     * loads all of the users habit events from file and sets a the global field
     */
    private void loadAllHabitEvents() {
        Iterator<HabitType> iterator = allHabitTypes.iterator();
        while(iterator.hasNext()) {
            HabitType habit = iterator.next();
            ArrayList<HabitEvent> eventList = habit.getHabitEvents();
            allHabitEvents.addAll(eventList);
        }
    }
}
