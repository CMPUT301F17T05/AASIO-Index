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
 * Created by Oliver on 02/12/2017.
 */

public class SocialFeedAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<FeedEvent> feedList = new ArrayList<>();
    private Context context;
    private Context mContext;
    private ImageView profileImageView;
    private TextView profileNameTextView;
    private TextView eventTextView;
    private TextView eventDateTextView;
    private Button selectButton;

    SocialFeedAdapter(ArrayList<FeedEvent> feedList, Context context, Context mContext) {
        this.feedList = feedList;
        this.context = context;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return feedList.size();
    }

    @Override
    public Object getItem(int i) {
        return feedList.get(i);
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
            view = inflater.inflate(R.layout.social_feed_row, null);
        }
        final UserProfile userProfile = new UserProfile(mContext);
        TreeGrowth profileTreeGrowth = userProfile.treeGrowth;

        FeedEvent feedEvent = feedList.get(i);

        profileImageView = view.findViewById(R.id.socialFeedRowProfileImage);
        profileNameTextView = view.findViewById(R.id.socialFeedRowProfileName);
        eventTextView = view.findViewById(R.id.socialFeedRowEvent);
        eventDateTextView = view.findViewById(R.id.socialFeedDateText);
        selectButton = view.findViewById(R.id.socialFeedRowSelectButton);

        profileNameTextView.setText(feedEvent.getProfile().getDisplayName());
        eventTextView.setText(feedEvent.getEvent().getHabitType());
        eventDateTextView.setText(feedEvent.getEvent().getCompletionDateString());


        Bitmap profileImage = feedEvent.getProfile().getProfilePicture();
        if(profileImage == null) {
            profileImage = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.no_profile_image);
        }

        profileImageView.setImageBitmap(scaleDownBitmap(profileImage));

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = sendHabitInfoToView(feedEvent);
                Intent intent = new Intent(view.getContext(), ViewFeedEventActivity.class);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });

        return view;
    }

    /**
     * Using the events information, makes a bundle so the view event activity can be properly filled
     * @param event the feed event that you want to view
     * @return a bundle that can be sent off to the activity
     */
    private Bundle sendHabitInfoToView(FeedEvent event) {
        Bundle bundle = new Bundle();
        bundle.putString("User Name", event.getProfile().getDisplayName());
        bundle.putString("Habit Type", event.getEvent().getHabitType());
        String comment = event.getEvent().getComment();
        if(comment.equals("")) {
            comment = "[no comment]";
        }
        bundle.putString("Date", event.getEvent().getCompletionDateString());
        bundle.putString("Comment", comment);
        bundle.putParcelable("Image", event.getEvent().getEventPicture());
        return bundle;
    }

    private Bitmap scaleDownBitmap(Bitmap bitmap) {
        int width = (int) dipToPixels(context, (float) 24);
        int height = (int) dipToPixels(context, (float) 24);
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

}
