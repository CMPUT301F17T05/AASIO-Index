package com.cmput301.t05.habilect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Ian on 2017-10-22.
 */

public class HistoryFilterFragment extends Fragment {
    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_history_filter, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();

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

        return rootView;
    }
}
