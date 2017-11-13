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
 */

public class HistoryFilterFragment extends Fragment {
    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_history_filter, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();

        ListView eventList = rootView.findViewById(R.id.historyFilterFragmentListView);

        HabitEvent e1 = new HabitEvent("Event 1", null, null, new Date(), "Event 1");
        HabitEvent e2 = new HabitEvent("Event 2", null, null, new Date(), "Event 2");
        HabitEvent e3 = new HabitEvent("Event 3", null, null, new Date(), "Event 3");
        HabitEvent e4 = new HabitEvent("Event 4", null, null, new Date(), "Event 4");
        ArrayList<HabitEvent> eList = new ArrayList<>();
        eList.add(e1);
        eList.add(e2);
        eList.add(e3);
        eList.add(e4);

        HabitEventListAdapter eventListAdapter = new HabitEventListAdapter(eList, rootView.getContext());
        eventList.setAdapter(eventListAdapter);


        return rootView;
    }
}
