package com.cmput301.t05.habilect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
    ImageButton mapButton;

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
        mapButton = rootView.findViewById(R.id.socialMapButton);

        feedEventList = new ArrayList<>();
        List<HabitEvent> mostRecentEvents = new ArrayList<HabitEvent>();
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
                        //if (habit.getShared()) {
                            List<HabitEvent> events = habit.getHabitEvents();
                            HabitEvent mostRecent=null;
                            for (HabitEvent event : events) {
                                if (event.getCompletionDate()!=null & event.getHabitType()!=null) {
                                    HabitEvent feedHabitEvent = new HabitEvent(event.getComment(), event.getEventPicture(), event.getLocation(), event.getCompletionDate(), event.getHabitType(),user.getId().toString());
                                    if (mostRecent==null) {
                                        mostRecent = feedHabitEvent;
                                    }else if (mostRecent.getCompletionDate().before(feedHabitEvent.getCompletionDate())) {
                                        mostRecent = feedHabitEvent;
                                    }
                                }
                            }
                            if (mostRecent!=null) {
                                FeedEvent feedEvent = new FeedEvent(followee, mostRecent);
                                mostRecentEvents.add(mostRecent);
                                feedEventList.add(feedEvent);
                            }
                        //}
                    }
                }
            }
        }
        Collections.sort(feedEventList, new Comparator<FeedEvent>() {
            @Override
            public int compare(FeedEvent o1, FeedEvent o2) {
                if (o1.getEvent().getCompletionDate().after(o2.getEvent().getCompletionDate())) {
                    return 1;
                }
                else if (o1.getEvent().getCompletionDate().before(o2.getEvent().getCompletionDate())) {
                    return -1;
                }
                return 0;
            }
        });
        SocialFeedAdapter feedAdapter = new SocialFeedAdapter(feedEventList, context, mContext);
        feedListView.setAdapter(feedAdapter);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Events", (ArrayList<HabitEvent>)mostRecentEvents);
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });

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
                getActivity().setTitle("Social Feed");
            }
        }
    }
}
