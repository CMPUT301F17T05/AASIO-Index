package com.cmput301.t05.habilect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewFriendActivity extends AppCompatActivity {
    private Bundle bundle;
    private TextView userName;
    private ImageView profileImage;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);

        bundle = getIntent().getExtras();

        userName = findViewById(R.id.viewFriendUserName);
        profileImage = findViewById(R.id.viewFriendProfileImage);
        backButton = findViewById(R.id.viewFriendBackButton);

        userName.setText(getUserNameFromBundle());
        Bitmap imageBitmap = getImageFromBundle();
        if (imageBitmap == null) {
            imageBitmap = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.add_image);
        }
        profileImage.setImageBitmap(imageBitmap);

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
    private String getUserNameFromBundle() {
        try {
            return bundle.getString("User Name");
        }
        catch (Exception e) {
            return "";
        }
    }

    private Bitmap getImageFromBundle() {
        try {
            return bundle.getParcelable("Image");
        }
        catch (Exception e) {
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.add_image);
            return icon;
        }
    }
}
