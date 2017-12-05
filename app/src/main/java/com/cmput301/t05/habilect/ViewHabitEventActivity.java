package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * This activity launches when a user wishes to see more information about a particular
 * habit event. This activity must be passed information about the event in a bundle. The tags are as follow
 *
 * Title - the habit type title of the event
 * Date - the date of completion of the event
 * Comment - the user inputted comment to the event
 * File Path - the location where the event image is stored
 * @author rarog
 */
public class ViewHabitEventActivity extends AppCompatActivity {
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit_event);

        bundle =  getIntent().getExtras();

        TextView title = findViewById(R.id.viewHabitEventTitle);
        ImageView image = findViewById(R.id.viewHabitEventImageView);
        TextView comment = findViewById(R.id.viewHabitEventComment);
        TextView date = findViewById(R.id.viewHabitEventDate);
        Button backButton = findViewById(R.id.viewEventBackButton);

        title.setText(getTitleFromBundle());
        comment.setText(getCommentFromBundle());
        date.setText(getDateFromBundle());

        String fileName = getFilePathFromBundle();
        ContextWrapper cw = new ContextWrapper(this.getApplicationContext());
        //File directory = cw.getDir("eventImages", Context.MODE_PRIVATE);
        //File myPath = new File(directory, fileName);
        image.setImageBitmap(getEventImageFromBundle());

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
    private String getTitleFromBundle() {
        try {
            return bundle.getString("Title");
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     *
     * @return a String representing the habit date title if there is one
     */
    private String getDateFromBundle() {
        try {
            return bundle.getString("Date");
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     *
     * @return a String representing the habit comment title if there is one
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
     * @return a String representing the habit event image file path
     */
    private String getFilePathFromBundle() {
        try {
            return bundle.getString("File Path");
        }
        catch (Exception e) {
            return "";
        }
    }
}
