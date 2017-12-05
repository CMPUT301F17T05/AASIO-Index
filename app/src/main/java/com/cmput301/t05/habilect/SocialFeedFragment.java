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
import java.util.List;
import java.util.UUID;

/**
 * This fragment shows the user their social feed
 * @author rarog
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

        final UserAccount user = new UserAccount();
        user.load(mContext);

        feedListView = rootView.findViewById(R.id.socialActivityFeedListView);

        feedEventList = new ArrayList<>();
        List<UserAccount> followees = user.getFollowees();
        for (UserAccount followee : followees) {
            if (followee!=null) {
                List<UserAccount> followers = followee.getFollowers();
                List<UUID> followerIds = new ArrayList<UUID>();
                for (UserAccount follower : followers) {
                    if (follower!=null) {
                        followerIds.add(follower.getId());
                    }
                }
                if (followerIds.contains(user.getId())) {
                    List<HabitType> habits = followee.getHabits();
                    for (HabitType habit : habits) {
                        if (habit.getShared()) {
                            List<HabitEvent> events = habit.getHabitEvents();
                            for (HabitEvent event : events) {
                                if (event.getCompletionDate()!=null & event.getHabitType()!=null) {
                                    HabitEvent feedHabitEvent = new HabitEvent(event.getComment(), event.getEventPicture(), event.getLocation(), event.getCompletionDate(), event.getHabitType(), user.getId().toString());
                                    FeedEvent feedEvent = new FeedEvent(followee, feedHabitEvent);
                                    feedEventList.add(feedEvent);
                                }
                            }
                        }
                    }
                }
            }
        }
        SocialFeedAdapter feedAdapter = new SocialFeedAdapter(feedEventList, context, mContext);
        feedListView.setAdapter(feedAdapter);

        return rootView;

    }

    /**
     * When the user is currently on this fragment, set the title
     * @param isVisibleToUser
     */
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
