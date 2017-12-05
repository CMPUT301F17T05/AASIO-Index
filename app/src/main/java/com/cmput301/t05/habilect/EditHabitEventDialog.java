package com.cmput301.t05.habilect;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
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
// TODO: need to pass the date to this dialog, and not make a new date when finished editing
public class EditHabitEventDialog extends DialogFragment {
    private OnEditHabitEventListener onEditHabitEventListener;
    private Bundle resultBundle;

    private String dateString;
    private Date eventDate;

    // layout views
    private TextureView cameraTextureView;
    private ImageButton eventImage;
    private Bitmap eventBitmap;
    Context context;
    EditText commentText;
    TextView commentWarning;
    Button createButton;
    Spinner spinner;
    CheckBox checkBox;

    private static final String TAG = "Edit event dialog";

    private boolean cameraPermission;
    private boolean locationPermission;

    Camera camera;
    boolean editEventImageViewDebounce = false;

    // location controller
    private FusedLocationProviderClient fusedLocationClient;
    protected Location lastLocation;

    public void setOnEditHabitEventListener(OnEditHabitEventListener onEditHabitEventListener) {
        this.onEditHabitEventListener = onEditHabitEventListener;
    }

    //region camera controller
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
    }; //endregion

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        cameraPermission = checkCameraPermissions();
        locationPermission = checkLocationPermissions();
        if(locationPermission) {
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

    /**
     * Checks if the inputted comment confers to the correct size for HabitEvent
     */
    private void checkCommentLength() {
        String commentField = commentText.getText().toString();
        if(commentField.length() <= HabitEvent.MAX_COMMENT_LENGTH) {
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
        View view = inflater.inflate(R.layout.dialog_edit_habit_event, null);
        dialog.setContentView(view);

        context = getContext();

        dateString = getDateFromBundle();

        TextView eventTitle = view.findViewById(R.id.editHabitEventDialogTitle);
        spinner = view.findViewById(R.id.editHabitEventSpinner);
        eventImage = view.findViewById(R.id.editHabitEventImageButton);
        cameraTextureView = view.findViewById(R.id.editEventCameraPreviewTextureView);
        final ImageButton captureButton = view.findViewById(R.id.editEventCaptureButton);
        checkBox = view.findViewById(R.id.editHabitEventCheckBox);
        commentText = view.findViewById(R.id.editEventCommentText);
        commentText.setText(getCommentFromBundle());
        commentWarning = view.findViewById(R.id.editEventCommentWarning);
        Button cancelButton = view.findViewById(R.id.editEventCancelButton);
        createButton = view.findViewById(R.id.editEventCreateButton);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        camera = new Camera(cameraTextureView, cameraCaptureSessionCallback, eventImage);

        // when you click on add image, open the camera and capture button
        eventImage.setImageBitmap(getEventBitmapFromBundle());
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
                            if (params.obj == null) {
                                editEventImageViewDebounce = false;
                            } else {
                                cameraTextureView.setAlpha(0.2f + 0.8f * MathUtility.EasingOut(System.currentTimeMillis() - ((long[]) params.obj)[0], ((long[]) params.obj)[1], 3));
                            }
                        }
                    };
                    MathUtility.Animate(100, 500, responseHandler);
                }
            }
        });

        // hit the capture button, take a picture, close camera and remove capture button
        captureButton.setVisibility(ImageButton.INVISIBLE);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set camera's auto-focus lock
                camera.takePhoto();
                eventBitmap = ((BitmapDrawable) eventImage.getDrawable()).getBitmap();
                eventImage.setVisibility(ImageButton.VISIBLE);
                captureButton.setVisibility(ImageButton.INVISIBLE);
            }
        });

        // add the text listener to display warning when comment is too long
        commentWarning.setVisibility(View.INVISIBLE);
        commentText.addTextChangedListener(commentTextWatcher);

        // gets the passed information from calling activity
        String title = getTitleFromBundle();
        ArrayList<String> habits = getHabitTypesFromBundle();

        // sets list adapter for the spinner
        ListAdapter listAdapter = new ArrayAdapter<>(context,
                R.layout.habit_type_spinner_layout,
                R.id.habitTypeSpinnerTextView, habits);
        spinner.setAdapter((SpinnerAdapter) listAdapter);

        eventTitle.setText("Edit " + title + " event");

        // when we create, gather the relevant information, inform calling activity, dismiss dialog
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultBundle = createHabitEventBundle();
                onEditHabitEventListener.OnAdded();
                dialog.dismiss();
            }
        });

        // when you want to cancel, simply exit the dialog
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
        if(cameraPermission) {
            camera.close();
        }
        super.onPause();
    }

    /**
     *
     * @return returns a bundle with all of the information that the user specified
     * in creating the event
     */
    public Bundle getResultBundle() {
        return resultBundle;
    }

    /**
     *
     * @return Creates a resultBundle, which holds all of the information the user inputted
     * in creating the event
     */
    private Bundle createHabitEventBundle() {
        Bundle bundle= new Bundle();
        String latitude;
        String longitude;
        String habitType;
        String comment;
        String date;

        // gets location
        if (checkBox.isChecked() && locationPermission && lastLocation != null) {
            latitude = String.valueOf(lastLocation.getLatitude());
            longitude = String.valueOf(lastLocation.getLongitude());
        } else {
            latitude = null;
            longitude = null;
        }
        // gets comment and makes new date
        comment = commentText.getText().toString();
        date = dateString;

        // gets the selected title
        if(spinner.getSelectedItem() != null) {
            habitType = spinner.getSelectedItem().toString();
        } else {
            habitType = "";
        }
        eventBitmap = ((BitmapDrawable) eventImage.getDrawable()).getBitmap();
        // put all information in bundle
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        eventBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedString = Base64.encodeToString(byteArray, Base64.URL_SAFE | Base64.NO_WRAP);
        bundle.putString("Image", encodedString);
        bundle.putString("comment", comment);
        bundle.putString("date", date);
        bundle.putString("latitude", latitude);
        bundle.putString("longitude", longitude);
        bundle.putString("habitType", habitType);

        return bundle;
    }

    /**
     *
     * @return a String representing the habit event title if there is one
     */
    private String getTitleFromBundle() {
        try {
            return getArguments().getString("Title");
        }
        catch (Exception e) {
            return "";
        }
    }

    private String getCommentFromBundle() {
        try {
            return getArguments().getString("Comment");
        }
        catch (Exception e) {
            return "";
        }
    }
    /**
     *
     * @return an ArrayList with all of the passed habit types. If calling from main activity,
     * will be list of all of the users created habit types
     */
    private ArrayList<String> getHabitTypesFromBundle() {
        ArrayList<String> habits;
        try {
            return (ArrayList<String>) getArguments().get("Habit Type");
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String getDateFromBundle() {
        try {
            return getArguments().getString("Date");
        }
        catch (Exception e) {
            return "";
        }
    }

    private Bitmap getEventBitmapFromBundle() {
        try {
            String imageString = getArguments().getString("Image");
            byte[] decodedByteArray = Base64.decode(imageString, Base64.URL_SAFE | Base64.NO_WRAP);
            Bitmap image = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            return image;
        }
        catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener((Activity) context, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastLocation = task.getResult();
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    /**
     *
     * @return a boolean representing if we have permission to access user location
     */
    private boolean checkLocationPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     *
     * @return a boolean representing if we have permission to access the camera
     */
    private boolean checkCameraPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

}
