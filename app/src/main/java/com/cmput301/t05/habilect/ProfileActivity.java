package com.cmput301.t05.habilect;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

/**
 * @author ioltuszy
 */

public class ProfileActivity extends AppCompatActivity {
    Context context = this;

    Camera camera;

    TextureView.SurfaceTextureListener cameraPreviewSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            camera.setup(getApplication(), width, height);
            camera.open(getApplication());
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

    TextureView cameraTextureView;
    TextView displayNameTextView;
    ImageView backgroundImageView;
    ImageView profileImageView;
    ImageView bandImageView;
    Button captureButton;

    boolean profileImageViewDebounce = false;

    CameraCaptureSession.CaptureCallback cameraCaptureSessionCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            if (camera.getSessionStartTime() == 0) {
                camera.setSessionStartTime(timestamp);
            }
            camera.getCameraTextureView().setAlpha(MathUtility.EasingOut(timestamp - camera.getSessionStartTime(), 1000000000, 3));
        }
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, @NonNull CaptureRequest request, TotalCaptureResult result) {
                switch (camera.getState()) {
                    case Camera.STATE_PREVIEW:
                        break;
                    case Camera.STATE_LOCKED:
                        camera.captureStill(context);
                        camera.getCameraImage().close();
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

        cameraTextureView = (TextureView) findViewById(R.id.cameraPreviewTextureView);
        camera = new Camera(cameraTextureView, cameraCaptureSessionCallback);

        displayNameTextView = (TextView) findViewById(R.id.displayNameTextView);
        backgroundImageView = (ImageView) findViewById(R.id.backgroundImageView);
        bandImageView = (ImageView) findViewById(R.id.bandImageView);
        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        captureButton = (Button) findViewById(R.id.captureButton);

        Typeface openSansFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Montserrat-Bold.ttf");
        displayNameTextView.setTypeface(openSansFont);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!profileImageViewDebounce) {
                    profileImageViewDebounce = true;
                    Handler responseHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message params) {
                            if (params.obj==null)
                            {
                                profileImageViewDebounce = false;
                            }
                            else {
                                displayNameTextView.setAlpha(1f-1f*MathUtility.EasingOut(System.currentTimeMillis() - ((long[])params.obj)[0], ((long[])params.obj)[1], 3));
                                backgroundImageView.setAlpha(0.9f-0.9f*MathUtility.EasingOut(System.currentTimeMillis() - ((long[])params.obj)[0], ((long[])params.obj)[1], 3));
                                bandImageView.setAlpha(0.5f-0.5f*MathUtility.EasingOut(System.currentTimeMillis() - ((long[])params.obj)[0], ((long[])params.obj)[1], 3));
                                profileImageView.setAlpha(1f-1f*MathUtility.EasingOut(System.currentTimeMillis() - ((long[])params.obj)[0], ((long[])params.obj)[1], 3));
                                captureButton.setAlpha(0f+0.75f*MathUtility.EasingOut(System.currentTimeMillis() - ((long[])params.obj)[0], ((long[])params.obj)[1], 3));
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
                camera.lockFocus();
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
            camera.open(this);
        }
    }

    @Override
    protected void onPause() {
        camera.close();
        super.onPause();
    }
}
