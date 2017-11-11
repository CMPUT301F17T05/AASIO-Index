package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ian on 2017-11-08.
 */

public class Camera {
    public final static int STATE_PREVIEW = 0;
    public final static int STATE_LOCKED = 1;

    private String cameraId;
    private Size cameraSize;
    private CameraDevice cameraDevice;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private CameraCaptureSession cameraSession;
    private CaptureRequest.Builder cameraPreviewCaptureRequestBuilder;
    private Image cameraImage;
    private ImageReader cameraImageReader;
    private ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            cameraImage = imageReader.acquireLatestImage();
        }
    };

    private int state;
    private CameraCaptureSession.CaptureCallback cameraCaptureSessionCallback;

    private TextureView cameraTextureView;

    private long sessionStartTime;

    public Camera(TextureView cameraTextureView, CameraCaptureSession.CaptureCallback captureCallback) {
        this.cameraTextureView = cameraTextureView;
        this.cameraCaptureSessionCallback = captureCallback;
    }

    public void open(Context context) {
        openBackgroundThread();
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    cameraManager.openCamera(cameraId, cameraDeviceStateCallback, backgroundHandler);
                }
                else {
                    if (((AppCompatActivity)context).shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
                        Toast.makeText(context,"No Permission to use the Camera services", Toast.LENGTH_SHORT).show();
                    }
                    ((AppCompatActivity)context).requestPermissions(new String[] {android.Manifest.permission.CAMERA},1);
                }
            }
            else {
                cameraManager.openCamera(cameraId, cameraDeviceStateCallback, backgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (cameraSession != null) {
            cameraSession.close();
            cameraSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        closeBackgroundThread();
    }

    CameraDevice.StateCallback cameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            createCameraCaptureSession();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    };

    private void openBackgroundThread() {
        backgroundThread = new HandlerThread("CameraThread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void closeBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    boolean isCapturing = false;
    public void captureStill(final Context context) {
        try {
            if (!isCapturing) {
                isCapturing = true;
                CaptureRequest.Builder cameraStillCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                cameraStillCaptureRequestBuilder.addTarget(cameraImageReader.getSurface());
                cameraSession.stopRepeating();
                cameraSession.capture(cameraStillCaptureRequestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        Toast.makeText(context, "Picture taken", Toast.LENGTH_SHORT).show();
                        unlockFocus();
                    }
                }, backgroundHandler);
            }
        } catch (CameraAccessException e1) {
            e1.printStackTrace();
        }
    }

    public void lockFocus() {
        state = STATE_LOCKED;
        cameraPreviewCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
        try {
            cameraSession.capture(cameraPreviewCaptureRequestBuilder.build(), cameraCaptureSessionCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void unlockFocus() {
        state = STATE_PREVIEW;
        CaptureRequest cameraCaptureRequest = cameraPreviewCaptureRequestBuilder.build();
        try {
            cameraSession.setRepeatingRequest(
                    cameraCaptureRequest,
                    cameraCaptureSessionCallback,
                    backgroundHandler);
            isCapturing = false;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        cameraPreviewCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
        try {
            cameraSession.capture(cameraPreviewCaptureRequestBuilder.build(), cameraCaptureSessionCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraCaptureSession() {
        try {
            if (cameraTextureView==null) {
                throw new NullPointerException("Missing camera texture view");
            }
            SurfaceTexture surfaceTexture = cameraTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
            Surface cameraPreviewSurface = new Surface(surfaceTexture);
            cameraPreviewCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            cameraPreviewCaptureRequestBuilder.addTarget(cameraPreviewSurface);
            cameraDevice.createCaptureSession(Arrays.asList(cameraPreviewSurface, cameraImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (cameraDevice == null) {
                        return;
                    }
                    try {
                        CaptureRequest cameraCaptureRequest = cameraPreviewCaptureRequestBuilder.build();
                        cameraSession = cameraCaptureSession;
                        sessionStartTime = 0;
                        cameraCaptureSession.setRepeatingRequest(
                                cameraCaptureRequest,
                                cameraCaptureSessionCallback,
                                backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    // camera session failed to be created
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void setup(Context context, int width, int height) {
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    cameraImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
                    cameraImageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);
                    this.cameraSize = this.getApproprateResolution(streamConfigurationMap.getOutputSizes(SurfaceTexture.class), width, height);
                    this.cameraId = cameraId;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public static Size getApproprateResolution(Size[] sizes, int width, int height) {
        List<Size> viableSizes = new ArrayList<>();
        for (Size size : sizes) {
            if (height > width) {
                if (size.getWidth() > width && size.getHeight() > height) {
                    viableSizes.add(size);
                } else if (size.getWidth() > height && size.getHeight() > width) {
                    viableSizes.add(size);
                }
            }
        }
        if (viableSizes.size() > 0) {
            return Collections.min(viableSizes, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        return sizes[0];
    }

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(long sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public TextureView getCameraTextureView() {
        return cameraTextureView;
    }

    public void setCameraTextureView(TextureView cameraTextureViw) {
        this.cameraTextureView = cameraTextureViw;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Image getCameraImage() {
        return cameraImage;
    }
}
