package com.cmput301.t05.habilect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

/**
 * This activity allows the user to see details about one of their friends
 * @see SocialFollowingFragment
 * @see UserAccount
 * @author rarog
 */
public class ViewFriendActivity extends AppCompatActivity {
    private Bundle bundle;
    private TextView userName;
    private ImageView profileImage;
    private Button deleteButton;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);

        bundle = getIntent().getExtras();

        userName = findViewById(R.id.viewFriendUserName);
        profileImage = findViewById(R.id.viewFriendProfileImage);
        deleteButton = findViewById(R.id.stopFollowingButton);
        backButton = findViewById(R.id.viewFriendBackButton);

        Bitmap imageBitmap = null;
        UserAccount user = getUserFromBundle();
        if (user!=null)
        {
            userName.setText(user.getDisplayName());
            imageBitmap = user.getProfilePicture();
        } else {
            userName.setText("Error finding user");
        }
        if (imageBitmap == null) {
            imageBitmap = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.add_image);
        }
        profileImage.setImageBitmap(imageBitmap);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAccount localUser = new UserAccount();
                localUser.load(getApplicationContext());
                List<UserAccount> following = localUser.getFollowees();
                for (UserAccount followee : following) {
                    if (followee.getId().toString().equals(user.getId().toString())) {
                        localUser.removeFollowee(followee.getId());
                        localUser.save(getApplicationContext());
                        localUser.sync(getApplicationContext());
                        finish();
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     *
     * @return a UserAccount representing the friend if there is one
     */
    private UserAccount getUserFromBundle() {
        try {
            return UserAccount.fromId(UUID.fromString(bundle.getString("ID")));
        }
        catch (Exception e) {
            return null;
        }
    }

}
