package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author ioltuszy
 */

public class ProfileActivity extends AppCompatActivity {
    Camera camera;

    TextureView.SurfaceTextureListener cameraPreviewSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            camera.setup(getApplication() ,width, height);
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

    CameraCaptureSession.CaptureCallback cameraCaptureSessionCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            if (camera.getSessionStartTime()==0) {
                camera.setSessionStartTime(timestamp);
            }
            camera.getCameraTextureView().setAlpha(MathUtility.EasingOut(timestamp-camera.getSessionStartTime(), 1000000000, 3));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        cameraTextureView = (TextureView) findViewById(R.id.cameraPreviewTextureView);
        camera = new Camera(cameraTextureView, cameraCaptureSessionCallback);

        displayNameTextView = (TextView) findViewById(R.id.displayNameTextView);
        Typeface openSansFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Montserrat-Bold.ttf");
        displayNameTextView.setTypeface(openSansFont);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!cameraTextureView.isAvailable()) {
            cameraTextureView.setSurfaceTextureListener(cameraPreviewSurfaceTextureListener);
        }
    }
}
