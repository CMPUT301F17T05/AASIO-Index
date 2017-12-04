package com.cmput301.t05.habilect;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Oliver on 03/12/2017.
 */

public class SocialFriendFragment extends Fragment {
    FragmentManager fragmentManager;
    private Context mContext;
    private Context context;
    private ArrayList<UserProfile> friendList = new ArrayList<>();
    ListView friendListView;
    SocialFriendAdapter friendAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_social_friend, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();

        mContext = getActivity().getApplicationContext();

        context = getActivity();

        final UserProfile profile = new UserProfile(mContext);

        friendListView = rootView.findViewById(R.id.socialFriendListView);

        friendList.add(profile);
        friendAdapter = new SocialFriendAdapter(friendList, context, mContext);
        friendListView.setAdapter(friendAdapter);


        return rootView;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(getActivity() != null) {
                getActivity().setTitle("Friend list");
            }
    }
    }
}
