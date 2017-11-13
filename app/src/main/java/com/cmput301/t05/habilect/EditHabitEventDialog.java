package com.cmput301.t05.habilect;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.Date;

/**
 * This dialog facilitates the editing of habit events. When editing you must pass all
 * the relevant HabitEvent information to the dialog in a bundle. That is the comment, date,
 * location, latitude, longitude, habitType and file name of the image. When the user presses
 * create the following tags are passed back to the calling Activity, they are all Strings.
 *
 * comment - the comment the user left with the event. Always less than 20 characters
 * date -  the date the event was created in form yyyy_mm_dd
 * latitude - String of the latitude of the user location, if enable otherwise null
 * longitude - String of the longitude of the user location, if enable otherwise null
 * filePath - the name of the file where the image bitmap is stored
 * habitType - the associated HabitType title of the event
 * @author rarog
 */

public class EditHabitEventDialog extends DialogFragment {
    private OnEditHabitEventListener onEditHabitEventListener;
    private ImageButton eventImage;
    private Bitmap eventBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "Add event dialog";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    Context context;
    TextView commentText;
    TextView commentWarning;
    Button createButton;

    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;


    public void setOnEditHabitEventListener(OnEditHabitEventListener onEditHabitEventListener) {
        this.onEditHabitEventListener = onEditHabitEventListener;
    }

    Camera camera;
    boolean editEventImageViewDebounce = false;
    private TextureView cameraTextureView;

    TextureView.SurfaceTextureListener cameraPreviewSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            camera.setup(context, width, height);
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

    CameraCaptureSession.CaptureCallback cameraCaptureSessionCallback = new CameraCaptureSession.CaptureCallback() {
        /*@Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            Handler responseHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message params) {
                    cameraTextureView.setAlpha(0f+0.2f*MathUtility.EasingOut(System.currentTimeMillis() - ((long[])params.obj)[0], ((long[])params.obj)[1], 3));
                }
            };
            MathUtility.Animate(100, 500, responseHandler);
        }*/

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, @NonNull CaptureRequest request, TotalCaptureResult result) {
            switch (camera.getCameraState()) {
                case Camera.STATE_PREVIEWING:
                    break;
                case Camera.STATE_CAPTURING:
                    camera.retrieveImage(context);
                    if (!editEventImageViewDebounce) {
                        editEventImageViewDebounce = true;
                        Handler responseHandler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message params) {
                                if (params.obj==null)
                                {
                                    editEventImageViewDebounce = false;
                                }
                                else {
                                    cameraTextureView.setAlpha(1f-0.8f*MathUtility.EasingOut(System.currentTimeMillis() - ((long[])params.obj)[0], ((long[])params.obj)[1], 3));
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
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if(checkPermissions()) {
            getLastLocation();
        }
    }

    /* idea to disable button until required editText fields filled
    taken from
    https://stackoverflow.com/questions/20682865/disable-button-when-edit-text-fields-empty
    */
    /**
     * This TextWatcher implementation calls method to check required fields whenever a
     * required field has its text edited, used to disable create button until all required
     * fields are filled
     */
    private TextWatcher commentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkCommentLength();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void checkCommentLength() {
        String commentField = commentText.getText().toString();
        if(commentField.length() <= 20) {
            createButton.setEnabled(true);
            commentWarning.setVisibility(View.INVISIBLE);

        }
        else {
            createButton.setEnabled(false);
            commentWarning.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_habit_event, null);
        dialog.setContentView(view);

        context = getContext();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        eventImage = view.findViewById(R.id.editHabitEventImageButton);

        cameraTextureView = view.findViewById(R.id.editEventCameraPreviewTextureView);
        camera = new Camera(cameraTextureView, cameraCaptureSessionCallback, eventImage);

        final ImageButton captureButton = view.findViewById(R.id.editEventCaptureButton);
        captureButton.setVisibility(ImageButton.INVISIBLE);


        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureButton.setVisibility(ImageButton.VISIBLE);
                eventImage.setVisibility(ImageButton.INVISIBLE);
                if (!editEventImageViewDebounce) {
                    editEventImageViewDebounce = true;
                    Handler responseHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message params) {
                            if (params.obj==null) {
                                editEventImageViewDebounce = false;
                            }
                            else {
                                cameraTextureView.setAlpha(0.2f+0.8f*MathUtility.EasingOut(System.currentTimeMillis() - ((long[])params.obj)[0], ((long[])params.obj)[1], 3));
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
                eventImage.setVisibility(ImageButton.VISIBLE);
                captureButton.setVisibility(ImageButton.INVISIBLE);
            }
        });

        commentText = view.findViewById(R.id.editEventCommentText);
        commentWarning = view.findViewById(R.id.editEventCommentWarning);
        commentWarning.setVisibility(View.INVISIBLE);
        commentText.addTextChangedListener(commentTextWatcher);

        String title = getTitleFromBundle();
        ArrayList<String> habits = getHabitTypesFromBundle();

        ListAdapter listAdapter = new ArrayAdapter<>(context,
                R.layout.habit_type_spinner_layout,
                R.id.habitTypeSpinnerTextView, habits);

        final Spinner spinner = view.findViewById(R.id.editHabitEventSpinner);
        spinner.setAdapter((SpinnerAdapter) listAdapter);

        TextView eventTitle = view.findViewById(R.id.editHabitEventDialogTitle);
        eventTitle.setText("Add " + title + " event");

        createButton = view.findViewById(R.id.editEventCreateButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String habitType = spinner.getSelectedItem().toString();
                HabitEvent habitEvent = new HabitEvent(commentText.getText().toString(), eventBitmap, mLastLocation, new Date(), habitType);
                dialog.dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.editEventCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    private String getTitleFromBundle() {
        try {
            return getArguments().getString("Title");
        }
        catch (Exception e) {
            return "";
        }
    }


    // TODO: Right now for simplicity, habit events only know the title of habit types, might want to change that
    private ArrayList<String> getHabitTypesFromBundle() {
        ArrayList<String> habits;
        try {
            return (ArrayList<String>) getArguments().get("Habit Type");
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // TODO: seems to be a bug where it can't get location unless you open an app like google maps
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener((Activity) context, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
}
