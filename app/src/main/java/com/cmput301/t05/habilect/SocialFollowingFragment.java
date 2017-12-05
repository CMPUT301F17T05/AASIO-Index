package com.cmput301.t05.habilect;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This fragment show the user a list of their friends and allows them to find new friends
 *
 * @author rarog
 */

public class SocialFollowingFragment extends Fragment {
    FragmentManager fragmentManager;
    ListView friendListView;
    Button socialFriendFindFriendButton;
    SocialFollowingAdapter friendAdapter;
    private Context mContext;
    private Context context;
    private ArrayList<UserAccount> friendList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_social_followee, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();

        mContext = getActivity().getApplicationContext();

        context = getActivity();

        final UserAccount profile = new UserAccount();
        profile.load(mContext);

        friendListView = rootView.findViewById(R.id.socialFriendListView);

        socialFriendFindFriendButton = rootView.findViewById(R.id.socialFriendFindFriendButton);

        socialFriendFindFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                ShowUserDialog dialog = new ShowUserDialog();
                dialog.show(fragmentManager, "showUserDialog");
            }
        });

        ArrayList<UserAccount> userAccounts = (ArrayList<UserAccount>) profile.getFollowees();
        ArrayList<UserAccount> validAccounts = new ArrayList<>();
        for (UserAccount followee : userAccounts) {
            if (followee != null) {
                List<UserAccount> followers = followee.getFollowers();
                List<UUID> followerIds = new ArrayList<UUID>();
                for (UserAccount follower : followers) {
                    if (follower != null) {
                        followerIds.add(follower.getId());
                    }
                }
                if (followerIds.contains(profile.getId())) {
                    validAccounts.add(followee);
                }
            }
        }
        friendAdapter = new SocialFollowingAdapter(validAccounts, context, mContext);
        friendListView.setAdapter(friendAdapter);
        return rootView;
    }

    /**
     * When the user is currently on this fragment, set the title
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getActivity() != null) {
                getActivity().setTitle("Following");
            }
        }
    }
}
