package com.cmput301.t05.habilect;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * This dialog facilitates the creation of habit events. If creating an event outside of a habit type
 * you must pass an ArrayList of String with the titles of all possible habit types in a bundle
 * with the tag 'Habit Type'. If you are creating an event from a specific Habit Type, you must still
 * pass the Habit Title in an ArrayList of String. When the user presses create the following tags are
 * passed back to the calling Activity, they are all Strings.
 *
 * comment - the comment the user left with the event. Always less than 20 characters
 * date -  the date the event was created in form yyyy_mm_dd
 * latitude - String of the latitude of the user location, if enable otherwise null
 * longitude - String of the longitude of the user location, if enable otherwise null
 * filePath - the name of the file where the image bitmap is stored
 * habitType - the associated HabitType title of the event
 * @author rarog
 */

public class AddHabitEventDialog extends DialogFragment {
    private OnAddHabitEventListener onAddHabitEventListener;
    private ImageButton eventImage;
    private Bitmap eventBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "Add event dialog";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    Context context;
    TextView commentText;
    TextView commentWarning;
    Button createButton;
    Spinner spinner;
    CheckBox checkBox;

    Camera camera;
    boolean addEventImageViewDebounce = false;
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
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, @NonNull CaptureRequest request, TotalCaptureResult result) {
            switch (camera.getCameraState()) {
                case Camera.STATE_PREVIEWING:
                    break;
                case Camera.STATE_CAPTURING:
                    camera.retrieveImage(context);
                    if (!addEventImageViewDebounce) {
                        addEventImageViewDebounce = true;
                        Handler responseHandler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message params) {
                                if (params.obj==null)
                                {
                                    addEventImageViewDebounce = false;
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

    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;



    public void setOnAddHabitEventListener(OnAddHabitEventListener onAddHabitEventListener) {
        this.onAddHabitEventListener = onAddHabitEventListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // if user does not have location permissions set, will ask to enable them
        if (!checkPermissions()) {
            requestPermissions();
        } else {
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
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_habit_event, null);
        dialog.setContentView(view);

        context = getContext();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        eventImage = view.findViewById(R.id.addHabitEventImageButton);

        checkBox = view.findViewById(R.id.addHabitEventCheckBox);

        cameraTextureView = view.findViewById(R.id.addEventCameraPreviewTextureView);
        camera = new Camera(cameraTextureView, cameraCaptureSessionCallback, eventImage);

        final ImageButton captureButton = view.findViewById(R.id.addEventCaptureButton);
        captureButton.setVisibility(ImageButton.INVISIBLE);

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureButton.setVisibility(ImageButton.VISIBLE);
                eventImage.setVisibility(ImageButton.INVISIBLE);
                if (!addEventImageViewDebounce) {
                    addEventImageViewDebounce = true;
                    Handler responseHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message params) {
                            if (params.obj==null) {
                                addEventImageViewDebounce = false;
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
                    eventBitmap = ((BitmapDrawable)eventImage.getDrawable()).getBitmap();
                    eventImage.setVisibility(ImageButton.VISIBLE);
                    captureButton.setVisibility(ImageButton.INVISIBLE);
                }
        });

        commentText = view.findViewById(R.id.addEventCommentText);
        commentWarning = view.findViewById(R.id.addEventCommentWarning);
        commentWarning.setVisibility(View.INVISIBLE);
        commentText.addTextChangedListener(commentTextWatcher);

        String title = getTitleFromBundle();
        ArrayList<String> habits = getHabitTypesFromBundle();

        ListAdapter listAdapter = new ArrayAdapter<>(context,
                R.layout.habit_type_spinner_layout,
                R.id.habitTypeSpinnerTextView, habits);

        spinner = view.findViewById(R.id.addHabitEventSpinner);
        spinner.setAdapter((SpinnerAdapter) listAdapter);

        TextView eventTitle = view.findViewById(R.id.addHabitEventDialogTitle);
        eventTitle.setText("Add " + title + " event");

        createButton = view.findViewById(R.id.addEventCreateButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = createHabitEventIntent();
                dialog.dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.addEventCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!cameraTextureView.isAvailable()) {
            cameraTextureView.setSurfaceTextureListener(cameraPreviewSurfaceTextureListener);
        } else {
            camera.setup(context, cameraTextureView.getWidth(), cameraTextureView.getHeight());
            camera.open(context);
        }
    }

    @Override
    public void onPause() {
        camera.close();
        super.onPause();
    }

    // TODO: Probably want some error checking...
    private Intent createHabitEventIntent() {
        Intent intent = new Intent();
        String latitude;
        String longitude;
        if(checkBox.isChecked()) {
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
        }
        else {
            latitude = null;
            longitude = null;
        }
        String comment = commentText.getText().toString();
        String date = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).format(new Date());
        String habitType = spinner.getSelectedItem().toString();
        String filePath = habitType.replace(" ", "_") + "_" + date;

        if(eventBitmap != null) {
            saveImageInFile(filePath);
            intent.putExtra("filePath", filePath);
        }

        intent.putExtra("comment", comment);
        intent.putExtra("date", date);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("habitType", habitType);

        return intent;
    }
    // https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    private void saveImageInFile(String filePath) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("eventImages", Context.MODE_PRIVATE);
        File mypath=new File(directory, filePath);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            eventBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getTitleFromBundle() {
        return getArguments().getString("Title") == null
                ? "" : getArguments().getString("Title");
    }


    // TODO: Right now for simplicity, habit events only know the title of habit types, might want to change that
    private ArrayList<String> getHabitTypesFromBundle() {
        ArrayList<String> habits = (ArrayList<String>) getArguments().get("Habit Type");
        if(habits != null) {
            return habits;
        }
        return new ArrayList<>();
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


    //TODO: onRequestPermissionsResult not currently called if app requests permission because this is DialogFragment, either fix bug or ask for permission on main activity
    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.
                // TODO: Can't test this right now due to bug mentioned above
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar snackbar = Snackbar.make(getView(), "Test", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }
}
