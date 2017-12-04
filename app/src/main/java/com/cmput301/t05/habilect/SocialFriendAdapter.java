package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This adapter allows for the app users friend's profiles to be displayed
 * in a ListView
 * @author rarog
 * @see UserAccount
 * @see SocialFriendFragment
 */

public class SocialFriendAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<UserProfile> friendList = new ArrayList<>();
    private Context context;
    private Context mContext;
    private ImageView profileImageView;
    private TextView profileNameTextView;
    private Button selectButton;

    SocialFriendAdapter(ArrayList<UserProfile> friendList, Context context, Context mContext) {
        this.friendList = friendList;
        this.context = context;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int i) {
        return friendList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // inflates the view
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.social_friend_row, null);
        }
        final UserProfile userProfile = new UserProfile(mContext);

        UserProfile friend = friendList.get(i);

        profileImageView = view.findViewById(R.id.socialFriendProfileImage);
        profileNameTextView = view.findViewById(R.id.socialFriendUserName);
        selectButton = view.findViewById(R.id.socialFriendSelectButton);

        Bitmap profileImage = friend.getProfilePicture();
        if(profileImage == null) {
            profileImage = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.no_profile_image);
        }
        profileImageView.setImageBitmap(scaleDownBitmap(profileImage));

        profileNameTextView.setText(friend.getDisplayName());

        // if a user selects a profile, launch an activity showing their details
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = sendHabitInfoToView(friend);
                Intent intent = new Intent(view.getContext(), ViewFriendActivity.class);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });

        return view;
    }

    /**
     * Using the events information, makes a bundle so the view event activity can be properly filled
     * @param friend the friend that you want to view
     * @return a bundle that can be sent off to the activity
     */
    private Bundle sendHabitInfoToView(UserProfile friend) {
        Bundle bundle = new Bundle();
        bundle.putString("User Name", friend.getDisplayName());
        bundle.putParcelable("Image", friend.getProfilePicture());
        return bundle;
    }

    /**
     * The image must be scaled down to fit in a 24dpx24dp box, this function
     * scales down a given bitmap to that size
     * @param bitmap the bitmap that needs to be scaled down
     * @return a bitmap with the size of 24dpx24dp
     */
    private Bitmap scaleDownBitmap(Bitmap bitmap) {
        int width = (int) dipToPixels(context, (float) 24);
        int height = (int) dipToPixels(context, (float) 24);
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    /**
     * Converts dp to pixels. Taken from https://stackoverflow.com/questions/8399184/convert-dip-to-px-in-android
     * @param context the context of the activity
     * @param dipValue the dp value you want to convert
     * @return the converted pixel value
     */
    private static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
