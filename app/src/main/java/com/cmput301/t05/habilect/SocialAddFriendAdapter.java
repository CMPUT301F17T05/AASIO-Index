package com.cmput301.t05.habilect;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * This adapter shows a user, their name and profile image. When the user is clicked,
 * it sends a follow request
 *
 * @author rarog
 * @author ioltuszy
 * @see UserAccount
 */
public class SocialAddFriendAdapter extends BaseAdapter implements ListAdapter {
    private Dialog dialog;
    private ArrayList<UserAccount> accountList = new ArrayList<>();
    private Context context;
    private ImageView profileImageView;
    private TextView profileNameTextView;
    private Button selectButton;

    SocialAddFriendAdapter(Dialog dialog, ArrayList<UserAccount> accountList, Context context) {
        this.dialog = dialog;
        this.accountList = accountList;
        this.context = context;
    }

    /**
     * Converts dp to pixels. Taken from https://stackoverflow.com/questions/8399184/convert-dip-to-px-in-android
     *
     * @param context  the context of the activity
     * @param dipValue the dp value you want to convert
     * @return the converted pixel value
     */
    private static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    @Override
    public int getCount() {
        return accountList.size();
    }

    @Override
    public Object getItem(int i) {
        return accountList.get(i);
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
            view = inflater.inflate(R.layout.social_add_friend_row, null);
        }
        UserAccount userAccount = accountList.get(i);

        profileImageView = view.findViewById(R.id.socialAddFriendImage);
        profileNameTextView = view.findViewById(R.id.socialAddFriendName);
        selectButton = view.findViewById(R.id.socialAddFriendSelectButton);

        profileNameTextView.setText(userAccount.getDisplayName());

        Bitmap profileImage = userAccount.getProfilePicture();
        if (profileImage == null) {
            profileImage = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.no_profile_image);
        }

        profileImageView.setImageBitmap(scaleDownBitmap(profileImage));

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAccount localUser = new UserAccount();
                localUser.load(context);
                localUser.addFollowee(userAccount.getId());
                localUser.save(context);
                localUser.sync(context);
                userAccount.addPendingFollower(localUser.getId());
                userAccount.sync(context);
                dialog.dismiss();
            }
        });

        return view;
    }

    /**
     * The image must be scaled down to fit in a 24dpx24dp box, this function
     * scales down a given bitmap to that size
     *
     * @param bitmap the bitmap that needs to be scaled down
     * @return a bitmap with the size of 24dpx24dp
     */
    private Bitmap scaleDownBitmap(Bitmap bitmap) {
        int width = (int) dipToPixels(context, (float) 24);
        int height = (int) dipToPixels(context, (float) 24);
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

}
