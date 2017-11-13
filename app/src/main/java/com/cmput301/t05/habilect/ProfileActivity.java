package com.cmput301.t05.habilect;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
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
import android.widget.TextView;

/**
 * @author ioltuszy
 */

public class ProfileActivity extends AppCompatActivity {
    Context context = ProfileActivity.this;

    Camera camera;

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
    TextView displayNameEditText;
    ImageView backgroundImageView;
    ImageView profileImageView;
    ImageView bandImageView;
    Button saveChangesButton;
    Button captureButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("Habilect - Profile");

        UserProfile profile = new UserProfile(getApplicationContext());

        Navigation.setup(findViewById(android.R.id.content));

        mainConstraint = (ConstraintLayout) findViewById(R.id.mainConstraint);
        secondaryConstraint = (ConstraintLayout) findViewById(R.id.secondaryConstraint);

        cameraTextureView = (TextureView) findViewById(R.id.cameraPreviewTextureView);
        displayNameEditText = (EditText) findViewById(R.id.displayNameEditText);
        backgroundImageView = (ImageView) findViewById(R.id.backgroundImageView);
        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        saveChangesButton = (Button) findViewById(R.id.saveChangesButton);
        captureButton = (Button) findViewById(R.id.captureButton);
        captureButton.setEnabled(false);
        captureButton.setClickable(false);
        captureButton.setFocusable(false);
        secondaryConstraint.setEnabled(false);
        secondaryConstraint.setClickable(false);
        secondaryConstraint.setFocusable(false);

        displayNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChangesButton.setEnabled(false);
            }
        });

        camera = new Camera(cameraTextureView, cameraCaptureSessionCallback, profileImageView);

        Typeface openSansFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Montserrat-Bold.ttf");
        displayNameEditText.setTypeface(openSansFont);

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
                // set camera's auto-focus lock
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
            camera.open((Activity)ProfileActivity.this);
        }
    }

    @Override
    protected void onPause() {
        camera.close();
        super.onPause();
    }
}
