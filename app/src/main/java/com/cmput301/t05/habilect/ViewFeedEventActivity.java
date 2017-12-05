package com.cmput301.t05.habilect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This activity shows the details of a particular feed event
 * @see FeedEvent
 * @see SocialFeedFragment
 * @author rarog
 */
public class ViewFeedEventActivity extends AppCompatActivity {
    private TextView userNameTextView;
    private TextView habitTypeTextView;
    private TextView commentTextView;
    private TextView completionDateTextView;
    private ImageView eventImageView;
    private Button backButton;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feed_event);

        bundle = getIntent().getExtras();

        userNameTextView = findViewById(R.id.viewFeedEventUserName);
        habitTypeTextView = findViewById(R.id.viewFeedEventTitle);
        commentTextView = findViewById(R.id.viewFeedEventComment);
        completionDateTextView = findViewById(R.id.viewFeedEventCompletionDate);
        eventImageView = findViewById(R.id.viewFeedEventImage);
        backButton = findViewById(R.id.viewFeedEventBackButton);
        Bitmap imageBitmap = getEventImageFromBundle();
        if (imageBitmap == null) {
            imageBitmap = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.add_image);
        }
        userNameTextView.setText(getUserNameFromBundle());
        habitTypeTextView.setText(getHabitTypeFromBundle());
        commentTextView.setText(getCommentFromBundle());
        completionDateTextView.setText(getDateFromBundle());
        eventImageView.setImageBitmap(imageBitmap);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    /**
     *
     * @return a String representing the habit event title if there is one
     */
    private String getHabitTypeFromBundle() {
        try {
            return bundle.getString("Habit Type");
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     *
     * @return a String representing the habit event title if there is one
     */
    private String getUserNameFromBundle() {
        try {
            return bundle.getString("User Name");
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     *
     * @return a Bitmap representing the habit event image
     */
    private Bitmap getEventImageFromBundle() {
        try {
            String encodedImage = bundle.getString("Image");
            if (encodedImage!=null) {
                byte[] decodedByteArray = Base64.decode(encodedImage, Base64.URL_SAFE | Base64.NO_WRAP);
                return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @return a String representing the habit event comment if there is one
     */
    private String getCommentFromBundle() {
        try {
            return bundle.getString("Comment");
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     *
     * @return a String representing the habit event date if there is one
     */
    private String getDateFromBundle() {
        try {
            return bundle.getString("Date");
        }
        catch (Exception e) {
            return "";
        }
    }
}
