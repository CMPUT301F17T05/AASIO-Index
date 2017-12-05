package com.cmput301.t05.habilect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
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
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.UUID;

/**
 * @author ioltuszy
 */

public class InitActivity extends AppCompatActivity {
    Context context = InitActivity.this;

    Camera camera;

    ConstraintLayout mainConstraint;
    ConstraintLayout secondaryConstraint;

    TextureView cameraTextureView;
    ImageView backgroundImageView;
    ImageView profileImageView;
    Button captureButton;

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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a new user account, with a unique ID
        UserAccount user = new UserAccount();
        /*ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("userData", Context.MODE_PRIVATE);
        File path = new File(directory, "LocalUserAccount.sav");
        path.delete();*/
        user.load(context);
        //user.Exists = false;
        if (user.Exists) {
            //user.removeFollowee(UUID.fromString("a47f3482-c537-4cdd-a6cd-1244a2424151"));
            //user.addFollowee(UUID.fromString("f925b981-39f9-4a2c-aa70-98e3ca56a5a7"));
            //remote.removeFollower(UUID.fromString("e06459d4-27f1-49de-b8e8-85ab5b3469ef"));
            //UserAccount remote = UserAccount.fromId(UUID.fromString("f925b981-39f9-4a2c-aa70-98e3ca56a5a7"));
            //remote.addFollower(UUID.fromString("4d67c2ad-b5fd-4c6f-adc9-f2278bedbfea"));
            //remote.getHabits().get(1).setShared(true);
            //user.save(context);
            //remote.sync(context);
            //remote.getHabits().get(0).setShared(true);
            //remote.sync(context);
            //user.save(context);
            user.sync(context);

            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            user.init();

            setContentView(R.layout.activity_init);
            EditText displayNameEditText = (EditText) findViewById(R.id.displayNameInput);
            Button confirmButton = (Button) findViewById(R.id.confirmButton);

            displayNameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    user.setDisplayName(displayNameEditText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setProfilePicture(((BitmapDrawable)profileImageView.getDrawable()).getBitmap());

                    user.save(context);
                    user.sync(context);

                    Intent intent = new Intent(context, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            mainConstraint = (ConstraintLayout) findViewById(R.id.mainConstraint);
            secondaryConstraint = (ConstraintLayout) findViewById(R.id.secondaryConstraint);

            cameraTextureView = (TextureView) findViewById(R.id.cameraPreviewTextureView);
            backgroundImageView = (ImageView) findViewById(R.id.backgroundImageView);
            profileImageView = (ImageView) findViewById(R.id.profileImageView);
            captureButton = (Button) findViewById(R.id.captureButton);

            camera = new Camera(cameraTextureView, cameraCaptureSessionCallback, profileImageView);

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
                    camera.takePhoto();

                }
            });
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!cameraTextureView.isAvailable()) {
            cameraTextureView.setSurfaceTextureListener(cameraPreviewSurfaceTextureListener);
        } else {
            camera.setup(this, cameraTextureView.getWidth(), cameraTextureView.getHeight());
            camera.open((Activity)InitActivity.this);
        }
    }

    @Override
    protected void onPause() {
        camera.close();
        super.onPause();
    }
}