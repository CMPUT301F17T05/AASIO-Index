package com.cmput301.t05.habilect;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

/**
 * @author ioltuszy
 */

public class ProfileActivity extends AppCompatActivity {
    Context context = ProfileActivity.this;

    Camera camera;

    /**
     * Sets up the camera to begin updating the texture surface once it is available
     */
    TextureView.SurfaceTextureListener cameraPreviewSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            camera.setup(getApplication(), width, height);
            camera.open(context);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    ConstraintLayout mainConstraint;
    ConstraintLayout secondaryConstraint;

    TextureView cameraTextureView;
    EditText displayNameEditText;
    TextView displayNameWarning;
    ImageView backgroundImageView;
    ImageView profileImageView;
    ImageView bandImageView;
    ImageView treeGrowthImageView;
    Button saveChangesButton;
    Button captureButton;

    /**
     * Animates the view when the user captures a still image and enables the save button
     */
    boolean profileImageViewDebounce = false;
    CameraCaptureSession.CaptureCallback cameraCaptureSessionCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, @NonNull CaptureRequest request, TotalCaptureResult result) {
            switch (camera.getCameraState()) {
                case Camera.STATE_PREVIEWING:
                    break;
                case Camera.STATE_CAPTURING:
                    camera.retrieveImage(context.getApplicationContext());
                    if (!profileImageViewDebounce) {
                        profileImageViewDebounce = true;
                        Handler responseHandler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message params) {
                                if (params.obj == null) {
                                    profileImageViewDebounce = false;
                                    captureButton.setEnabled(false);
                                    captureButton.setClickable(false);
                                    captureButton.setFocusable(false);
                                    secondaryConstraint.setEnabled(false);
                                    secondaryConstraint.setClickable(false);
                                    secondaryConstraint.setFocusable(false);
                                    saveChangesButton.setEnabled(true);
                                } else {
                                    cameraTextureView.setAlpha(1f - 0.8f * MathUtility.EasingOut(System.currentTimeMillis() - ((long[]) params.obj)[0], ((long[]) params.obj)[1], 3));
                                    mainConstraint.setAlpha(0f + 1f * MathUtility.EasingOut(System.currentTimeMillis() - ((long[]) params.obj)[0], ((long[]) params.obj)[1], 3));
                                    secondaryConstraint.setAlpha(1f - 1f * MathUtility.EasingOut(System.currentTimeMillis() - ((long[]) params.obj)[0], ((long[]) params.obj)[1], 3));
                                }
                            }
                        };
                        MathUtility.Animate(100, 500, responseHandler);
                    }
            }
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, @NonNull CaptureRequest request, CaptureFailure failure) {

        }
    };

    /**
     * Constrains the length of the user's display name
     */
    private void checkDisplayNameLength() {
        String commentField = displayNameEditText.getText().toString();
        if (commentField.length() <= 48) {
            saveChangesButton.setEnabled(true);
            displayNameWarning.setVisibility(View.INVISIBLE);

        } else {
            saveChangesButton.setEnabled(false);
            displayNameWarning.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("Habilect - Profile");

        Navigation.setup(findViewById(android.R.id.content));

        mainConstraint = (ConstraintLayout) findViewById(R.id.mainConstraint);
        secondaryConstraint = (ConstraintLayout) findViewById(R.id.secondaryConstraint);

        cameraTextureView = (TextureView) findViewById(R.id.cameraPreviewTextureView);
        displayNameEditText = (EditText) findViewById(R.id.displayNameEditText);
        displayNameWarning = (TextView) findViewById(R.id.displayNameWarning);
        backgroundImageView = (ImageView) findViewById(R.id.backgroundImageView);
        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        treeGrowthImageView = (ImageView) findViewById(R.id.treeGrowthImageView);
        saveChangesButton = (Button) findViewById(R.id.saveChangesButton);
        captureButton = (Button) findViewById(R.id.captureButton);
        captureButton.setEnabled(false);
        captureButton.setClickable(false);
        captureButton.setFocusable(false);
        secondaryConstraint.setEnabled(false);
        secondaryConstraint.setClickable(false);
        secondaryConstraint.setFocusable(false);

        /**
         * Retrieves user information and updates the views accordingly
         */
        final UserProfile profile = new UserProfile(getApplicationContext());
        displayNameEditText.setText(profile.getDisplayName());
        if (profile.getProfilePicture() != null) {
            profileImageView.setImageBitmap(profile.getProfilePicture());
        }

        /**
         * Sets the user's tree growth image based on acquired level
         */
        setTreeGrowthImageView();

        /**
         * Subscribes the display name's EditText view to a listener that validates its input
         */
        displayNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkDisplayNameLength();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**
         * Subscribes the save button to a listener that stores display name and profile image information locally
         */
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChangesButton.setEnabled(false);

                SharedPreferences sharedPreferences = context.getSharedPreferences(UserProfile.HABILECT_USER_INFO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                profile.setDisplayName(displayNameEditText.getText().toString());
                editor.putString(UserProfile.HABILECT_USER_DISPLAY_NAME, displayNameEditText.getText().toString());
                editor.commit();

                Bitmap bitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, blob);
                String encodedBitmapString = new String(Base64.encode(blob.toByteArray(), Base64.DEFAULT));
                profile.setProfilePicture(encodedBitmapString);
                editor.putString(UserProfile.HABILECT_USER_PICTURE, encodedBitmapString);
                editor.commit();

                WebService.UpdateUserProfileTask updateUserProfileTask = new WebService.UpdateUserProfileTask();
                updateUserProfileTask.execute(profile);
            }
        });

        camera = new Camera(cameraTextureView, cameraCaptureSessionCallback, profileImageView);

        Typeface openSansFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Montserrat-Bold.ttf");
        displayNameEditText.setTypeface(openSansFont);

        /**
         * Animates and enables the views that facilitate the user with a way to capture an image for their profile pic
         */
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!profileImageViewDebounce) {
                    profileImageViewDebounce = true;
                    Handler responseHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message params) {
                            if (params.obj == null) {
                                profileImageViewDebounce = false;
                                captureButton.setEnabled(true);
                                captureButton.setClickable(true);
                                captureButton.setFocusable(true);
                                secondaryConstraint.setEnabled(true);
                                secondaryConstraint.setClickable(true);
                                secondaryConstraint.setFocusable(true);
                            } else {
                                cameraTextureView.setAlpha(0.2f + 0.8f * MathUtility.EasingOut(System.currentTimeMillis() - ((long[]) params.obj)[0], ((long[]) params.obj)[1], 3));
                                mainConstraint.setAlpha(1f - 1f * MathUtility.EasingOut(System.currentTimeMillis() - ((long[]) params.obj)[0], ((long[]) params.obj)[1], 3));
                                secondaryConstraint.setAlpha(0f + 1f * MathUtility.EasingOut(System.currentTimeMillis() - ((long[]) params.obj)[0], ((long[]) params.obj)[1], 3));
                            }
                        }
                    };
                    MathUtility.Animate(100, 500, responseHandler);
                }
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: set camera's auto-focus lock
                camera.takePhoto();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!cameraTextureView.isAvailable()) {
            cameraTextureView.setSurfaceTextureListener(cameraPreviewSurfaceTextureListener);
        } else {
            camera.setup(this, cameraTextureView.getWidth(), cameraTextureView.getHeight());
            camera.open((Activity) ProfileActivity.this);
        }

        setTreeGrowthImageView();

    }

    @Override
    protected void onPause() {
        camera.close();
        super.onPause();
    }

    private void setTreeGrowthImageView(){

        final UserProfile profile = new UserProfile(getApplicationContext());

        TreeGrowth profileTreeGrowth = profile.treeGrowth;

        int nutrientLevel = profileTreeGrowth.getNutrientLevel();

        //tmp drawables until have actual images
        if (nutrientLevel < 25) {
            treeGrowthImageView.setImageResource(R.drawable.home);
        } else if (nutrientLevel >= 25 && nutrientLevel < 50) {
            treeGrowthImageView.setImageResource(R.drawable.profile);
        } else if (nutrientLevel >= 50 && nutrientLevel < 75) {
            treeGrowthImageView.setImageResource(R.drawable.history);
        }
    }
}
