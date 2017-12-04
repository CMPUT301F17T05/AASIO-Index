package com.cmput301.t05.habilect;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author ioltuszy
 */

public class SocialFeedFragment extends Fragment {
    FragmentManager fragmentManager;
    private Context mContext;
    private Context context;
    private ArrayList<FeedEvent> feedEventList;
    ListView feedListView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                                     ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_social_feed, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();

        mContext = getActivity().getApplicationContext();

        context = getActivity();

        getActivity().setTitle("Social Feed");

        final UserProfile profile = new UserProfile(mContext);

        feedListView = rootView.findViewById(R.id.socialActivityFeedListView);


        feedEventList = new ArrayList<>();
        HabitEvent event = new HabitEvent("", null, null, new Date(), "Dummy event");
        FeedEvent feedEvent = new FeedEvent(profile, event);
        feedEventList.add(feedEvent);

        SocialFeedAdapter feedAdapter = new SocialFeedAdapter(feedEventList, context, mContext);
        feedListView.setAdapter(feedAdapter);

        return rootView;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(getActivity() != null) {
                getActivity().setTitle("Social feed");
            }
        }
    }
}
