package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
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
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author ioltuszy
 */

class Camera {
    final static int STATE_PREVIEWING = 0;
    final static int STATE_CAPTURING = 1;

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    private CameraDevice mCameraDevice;
    private String mCameraId;
    private Size mCameraSize;
    private int mCameraState;

    private CameraCaptureSession mCameraSession;
    private long mCameraSessionStartTime;
    private CameraCaptureSession.CaptureCallback mCameraCaptureSessionCaptureCallback;

    private CaptureRequest.Builder mCaptureRequestBuilder;

    private Bitmap mCameraImage;
    private ImageReader mCameraImageReader;
    private ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            Image latestImage = imageReader.acquireNextImage();
            ByteBuffer buffer = latestImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            mCameraImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            if (mImageView!=null) {
                Matrix matrix = new Matrix();
                matrix.postRotate(270); // TODO: set according to camera's orientation
                matrix.postScale(-1, 1, mImageView.getWidth()/2, mImageView.getHeight()/2);
                mCameraImage = Bitmap.createScaledBitmap(mCameraImage, mImageView.getWidth(), mImageView.getHeight(), true);
                mCameraImage = Bitmap.createBitmap(mCameraImage, 0, 0, mCameraImage.getWidth(), mCameraImage.getHeight(), matrix, true);
                mImageView.setImageBitmap(mCameraImage);
            }
            latestImage.close();
        }
    };

    private TextureView mTextureView;
    private ImageView mImageView;

    /**
     * Establishes the preview TextureView onto which the camera will project its image and the
     * ImageView which will update upon still image capture.
     *
     * @param textureView           A surface onto which the preview will be rendered
     * @param captureCallback      Callback that overrides behavior during preview capture and still image capture
     * @param imageView             An image view which will be updated according
     */
    Camera(TextureView textureView, CameraCaptureSession.CaptureCallback captureCallback, ImageView imageView) {
        mTextureView = textureView;
        mCameraCaptureSessionCaptureCallback = captureCallback;
        mImageView = imageView;
    }

    /**
     * Determines the front facing camera, the ideal capture resolution, and sets the callback that
     * updates the ImageView supplied to the constructor when a new image is captured
     *
     * @param context               The context through which to obtain system services
     * @param width                 The width of preview surface
     * @param height                The height of preview surface
     */
    void setup(Context context, int width, int height) {
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (cameraManager != null) {
                for (String cameraId : cameraManager.getCameraIdList()) {
                    CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                    Integer cameraLensFacingCharacteristic = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                    if (cameraLensFacingCharacteristic != null) {
                        if (cameraLensFacingCharacteristic == CameraCharacteristics.LENS_FACING_FRONT) {
                            this.mCameraId = cameraId;

                            mCameraImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
                            mCameraImageReader.setOnImageAvailableListener(onImageAvailableListener, mBackgroundHandler);

                            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                            if (streamConfigurationMap != null) {
                                mCameraSize = getAppropriateSize(streamConfigurationMap.getOutputSizes(SurfaceTexture.class), width, height);
                            }
                            break;
                        }
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a background thread that can be used to handle some of the more intensive camera-related
     * operations
     */
    private void openBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraThread");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    /**
     * Closes the background thread
     */
    private void closeBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback methods for when a camera is opened using its ID - so as to create the camera device
     */
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            createCameraSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    /**
     * Checks for camera service permissions and opens the front facing camera, whose ID is
     * obtained during setup
     *
     * @param context               The context through which to obtain system services
     */
    void open(Context context) {
        if (mCameraId != null && mCameraImageReader != null && mCameraSize != null) {
            openBackgroundThread();
            if (mBackgroundThread != null && mBackgroundHandler != null) {
                CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                try {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        if (cameraManager != null) {
                            cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
                        }
                    } else {
                        ((AppCompatActivity) context).requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates a capture session that continuously notifies the CaptureCallback supplied to the
     * constructor and updates the TextureView.
     */
    private void createCameraSession() {
        try {
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mCameraSize.getWidth(), mCameraSize.getHeight());
            Surface cameraSurface = new Surface(surfaceTexture);
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(cameraSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(cameraSurface, mCameraImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (mCameraDevice == null) {
                        return;
                    }
                    try {
                        CaptureRequest cameraCaptureRequest = mCaptureRequestBuilder.build();
                        mCameraSession = cameraCaptureSession;
                        mCameraSessionStartTime = 0;
                        cameraCaptureSession.setRepeatingRequest(
                                cameraCaptureRequest,
                                mCameraCaptureSessionCaptureCallback,
                                mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Releases resources used by the camera
     */
    void close() {
        if (mCameraSession != null) {
            mCameraSession.close();
            mCameraSession = null;
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (mCameraImage != null) {
            mCameraImage = null;
        }
        if (mCameraImageReader != null) {
            mCameraImageReader.close();
            mCameraImageReader = null;
        }
        closeBackgroundThread();
    }

    private boolean isCapturing = false;

    /**
     * Temporarily suspends the repeating capture session to capture a still image
     *
     * @param context                The context through which to obtain system services
     */
    void retrieveImage(final Context context) {
        try {
            if (!isCapturing) {
                isCapturing = true;
                CaptureRequest.Builder cameraStillCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                cameraStillCaptureRequestBuilder.addTarget(mCameraImageReader.getSurface());
                mCameraSession.stopRepeating();
                mCameraSession.capture(cameraStillCaptureRequestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        unlockFocus();
                    }
                }, mBackgroundHandler);
            }
        } catch (CameraAccessException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Sets the camera state which should be used in the CaptureCallback supplied to the constructor
     */
    void takePhoto() {
        mCameraState = STATE_CAPTURING;
    }

    /**
     * Sets the camera state which should be used in the CaptureCallback supplied to the constructor
     * and also restarts the repeating capture session
     */
    private void unlockFocus() {
        mCameraState = STATE_PREVIEWING;
        try {
            isCapturing = false;
            mCameraSession.setRepeatingRequest(mCaptureRequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the smallest camera-supported resolution that is larger than both the width and height
     * of the TextureView supplied to the constructor
     */
    private static Size getAppropriateSize(Size[] sizes, int width, int height) {
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

    long getCameraSessionStartTime() {
        return mCameraSessionStartTime;
    }

    void setCameraSessionStartTime(long mCameraSessionStartTime) {
        this.mCameraSessionStartTime = mCameraSessionStartTime;
    }

    TextureView getTextureView() {
        return this.mTextureView;
    }

    int getCameraState() {
        return mCameraState;
    }
}