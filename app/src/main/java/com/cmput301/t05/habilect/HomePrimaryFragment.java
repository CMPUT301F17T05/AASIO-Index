package com.cmput301.t05.habilect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.cmput301.t05.habilect.AddHabitEventDialog.REQUEST_IMAGE_CAPTURE;

/**
 * @author ioltuszy
 */

public class HomePrimaryFragment extends Fragment {
    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_home_primary, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();

        final Button addHabitButton = (Button) rootView.findViewById(R.id.addHabitButton);
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

        final Button viewHabitButton = (Button) rootView.findViewById(R.id.viewHabitButton);
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

        final Button addHabitEventButton = rootView.findViewById(R.id.addHabitEvent);
        addHabitEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AddHabitEventDialog addHabitEventDialog = new AddHabitEventDialog();
                addHabitEventDialog.setOnAddHabitEventListener(new OnAddHabitEventListener() {
                    @Override
                    public void OnAdded() {
                        // TODO: implement OnAdded
                    }

                    @Override
                    public void OnCancelled() {
                        // TODO: implement OnCancelled
                    }
                });
                addHabitEventDialog.show(fragmentManager, "addHabitEventDialog");
                Bundle eventTitle = new Bundle();
                HabitType h1 = new HabitType("Title 1", "Reason 1", new Date());
                HabitType h2 = new HabitType("Title 2", "Reason 2", new Date());
                HabitType h3 = new HabitType("Title 3", "Reason 3", new Date());
                ArrayList<String> hList = new ArrayList<>();
                hList.add(h1.getTitle());
                hList.add(h2.getTitle());
                hList.add(h3.getTitle());
                eventTitle.putStringArrayList("Habit Type", hList);
                eventTitle.putString("Title", "TEST");
                addHabitEventDialog.setArguments(eventTitle);
            }
        });





        return rootView;
    }

}
