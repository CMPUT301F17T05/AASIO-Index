package com.cmput301.t05.habilect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.cmput301.t05.habilect.UserProfile.HABILECT_USER_INFO;
import static com.cmput301.t05.habilect.UserProfile.HABILECT_USER_PREVIOUS_NUTRIENT_LEVEL_TIER_RANK_UP;

/**
 * @author ioltuszy
 */

public class ProfileActivity extends AppCompatActivity {
    Context context = ProfileActivity.this;

    Camera camera;

    private Map<Integer, Integer> tierThresholds = new HashMap<Integer, Integer>() {{
        put(1, 10);
        put(2, 21);
        put(3, 33);
        put(4, 46);
        put(5, 66);
        put(6, 87);
        put(7, 109);
        put(8, 132);
        put(9, 162);
        put(10, 193);
        put(11, 225);
        put(12, 258);
        put(13, 298);
        put(14, 348);
        put(15, 408);
        put(16, 478);
    }};

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
    TextView nutrientLevelTextView;
    ProgressBar nutrientLevelProgressBar;
    TextView nutrientLevelToNextTierTextView;
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
        nutrientLevelTextView = (TextView) findViewById(R.id.nutrientLevelTextView);
        nutrientLevelProgressBar = (ProgressBar) findViewById(R.id.nutrientLevelProgressBar);
        nutrientLevelToNextTierTextView = (TextView) findViewById(R.id.nutrientLevelToNextTierTextView);
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

    private void setTreeGrowthImageView() {

        final UserProfile profile = new UserProfile(getApplicationContext());

        TreeGrowth profileTreeGrowth = profile.treeGrowth;

        int nutrientLevel = profileTreeGrowth.getNutrientLevel();

        Log.i("NUTRIENTLEVEL: ", "" + nutrientLevel);

        nutrientLevelTextView.setText("Nutrient Level: " + nutrientLevel);

        //Base Tier 0
        if (nutrientLevel < tierThresholds.get(1)) {
            nutrientLevelProgressBar.setMax(tierThresholds.get(1));
            nutrientLevelProgressBar.setProgress(nutrientLevel);
            nutrientLevelToNextTierTextView.setText(nutrientLevel + "/" + tierThresholds.get(1));
            treeGrowthImageView.setImageResource(R.drawable.level_01);
        } else if (nutrientLevel >= tierThresholds.get(1) && nutrientLevel < tierThresholds.get(2)) {
            checkAndAdjustTierProperties(nutrientLevel, 1);
        } else if (nutrientLevel >= tierThresholds.get(2) && nutrientLevel < tierThresholds.get(3)) {
            checkAndAdjustTierProperties(nutrientLevel, 2);
        } else if (nutrientLevel >= tierThresholds.get(3) && nutrientLevel < tierThresholds.get(4)) {
            checkAndAdjustTierProperties(nutrientLevel, 3);
        } else if (nutrientLevel >= tierThresholds.get(4) && nutrientLevel < tierThresholds.get(5)) {
            checkAndAdjustTierProperties(nutrientLevel, 4);
        } else if (nutrientLevel >= tierThresholds.get(5) && nutrientLevel < tierThresholds.get(6)) {
            checkAndAdjustTierProperties(nutrientLevel, 5);
        } else if (nutrientLevel >= tierThresholds.get(6) && nutrientLevel < tierThresholds.get(7)) {
            checkAndAdjustTierProperties(nutrientLevel, 6);
        } else if (nutrientLevel >= tierThresholds.get(7) && nutrientLevel < tierThresholds.get(8)) {
            checkAndAdjustTierProperties(nutrientLevel, 7);
        } else if (nutrientLevel >= tierThresholds.get(8) && nutrientLevel < tierThresholds.get(9)) {
            checkAndAdjustTierProperties(nutrientLevel, 8);
        } else if (nutrientLevel >= tierThresholds.get(9) && nutrientLevel < tierThresholds.get(10)) {
            checkAndAdjustTierProperties(nutrientLevel, 9);
        } else if (nutrientLevel >= tierThresholds.get(10) && nutrientLevel < tierThresholds.get(11)) {
            checkAndAdjustTierProperties(nutrientLevel, 10);
        } else if (nutrientLevel >= tierThresholds.get(11) && nutrientLevel < tierThresholds.get(12)) {
            checkAndAdjustTierProperties(nutrientLevel, 11);
        } else if (nutrientLevel >= tierThresholds.get(12) && nutrientLevel < tierThresholds.get(13)) {
            checkAndAdjustTierProperties(nutrientLevel, 12);
        } else if (nutrientLevel >= tierThresholds.get(13) && nutrientLevel < tierThresholds.get(14)) {
            checkAndAdjustTierProperties(nutrientLevel, 13);
        } else if (nutrientLevel >= tierThresholds.get(14) && nutrientLevel < tierThresholds.get(15)) {
            checkAndAdjustTierProperties(nutrientLevel, 14);
        } else if (nutrientLevel >= tierThresholds.get(15) && nutrientLevel < tierThresholds.get(16)) {
            checkAndAdjustTierProperties(nutrientLevel, 15);
        } else if (nutrientLevel >= tierThresholds.get(16)) {
            checkAndAdjustTierProperties(nutrientLevel, 16);
        }
    }

    private void buildRankUpDialog(int tier) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rank Up!");
        builder.setMessage("Congratulations! You have reached tier " + tier + ".");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if(tier == 4){
            ImageView completedTreeImage = new ImageView(context);
            completedTreeImage.setImageResource(R.drawable.green_3);
            builder.setView(completedTreeImage);
        } else if(tier == 8){
            ImageView completedTreeImage = new ImageView(context);
            completedTreeImage.setImageResource(R.drawable.orange_3);
            builder.setView(completedTreeImage);
        }else if(tier == 12){
            ImageView completedTreeImage = new ImageView(context);
            completedTreeImage.setImageResource(R.drawable.purple_3);
            builder.setView(completedTreeImage);
        }else if(tier == 16){
            ImageView completedTreeImage = new ImageView(context);
            completedTreeImage.setImageResource(R.drawable.rainbow_3);
            builder.setView(completedTreeImage);
        }
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    private void checkAndAdjustTierProperties(int nutrientLevel, int tier) {
        Log.i("INFORMATION", "nutlevel: " + nutrientLevel + " tier: " + tier);

        SharedPreferences sharedPreferences = context.getSharedPreferences(HABILECT_USER_INFO, Context.MODE_PRIVATE);
        String preference = sharedPreferences.getString(HABILECT_USER_PREVIOUS_NUTRIENT_LEVEL_TIER_RANK_UP, null);
        //if true, trigger rank up popup
        if (Integer.parseInt(preference) < tierThresholds.get(tier)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(UserProfile.HABILECT_USER_PREVIOUS_NUTRIENT_LEVEL_TIER_RANK_UP, Integer.toString(nutrientLevel));
            editor.commit();
            buildRankUpDialog(tier);
        }
        if(tier == 16){
            nutrientLevelProgressBar.setMax(tierThresholds.get(tier));
            nutrientLevelProgressBar.setProgress(nutrientLevel);
            nutrientLevelToNextTierTextView.setText("MAXED");
            treeGrowthImageView.setImageResource(R.drawable.all_trees_completed);
        }else{
            nutrientLevelProgressBar.setMax(tierThresholds.get(tier + 1));
            nutrientLevelProgressBar.setProgress(nutrientLevel);
            nutrientLevelToNextTierTextView.setText(nutrientLevel + "/" + tierThresholds.get(tier + 1));

            switch(tier){
                case 1:
                    treeGrowthImageView.setImageResource(R.drawable.level_02);
                    break;
                case 2:
                    treeGrowthImageView.setImageResource(R.drawable.level_03);
                    break;
                case 3:
                    treeGrowthImageView.setImageResource(R.drawable.level_04);
                    break;
                case 4:
                    treeGrowthImageView.setImageResource(R.drawable.level_06);
                    break;
                case 5:
                    treeGrowthImageView.setImageResource(R.drawable.level_07);
                    break;
                case 6:
                    treeGrowthImageView.setImageResource(R.drawable.level_08);
                    break;
                case 7:
                    treeGrowthImageView.setImageResource(R.drawable.level_09);
                    break;
                case 8:
                    treeGrowthImageView.setImageResource(R.drawable.level_11);
                    break;
                case 9:
                    treeGrowthImageView.setImageResource(R.drawable.level_12);
                    break;
                case 10:
                    treeGrowthImageView.setImageResource(R.drawable.level_13);
                    break;
                case 11:
                    treeGrowthImageView.setImageResource(R.drawable.level_14);
                    break;
                case 12:
                    treeGrowthImageView.setImageResource(R.drawable.level_16);
                    break;
                case 13:
                    treeGrowthImageView.setImageResource(R.drawable.level_17);
                    break;
                case 14:
                    treeGrowthImageView.setImageResource(R.drawable.level_18);
                    break;
                case 15:
                    treeGrowthImageView.setImageResource(R.drawable.level_19);
                    break;
            }
        }
    }
}
