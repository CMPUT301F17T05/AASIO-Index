package com.cmput301.t05.habilect;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
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

import static com.cmput301.t05.habilect.UserProfile.HABILECT_USER_INFO;

/**
 * This dialog facilitates the creation of habit events. If creating an event outside of a habit type
 * you must pass an ArrayList of String with the titles of all possible habit types in a bundle
 * with the tag 'Habit Type'. If you are creating an event from a specific Habit Type, you must still
 * pass the Habit Title in an ArrayList of String. When the user presses create the following tags are
 * passed back to the calling Activity, they are all Strings.
 * In order to make location functionality, used this sample - https://github.com/googlesamples/android-play-location/blob/master/LocationUpdatesForegroundService/app/src/main/java/com/google/android/gms/location/sample/locationupdatesforegroundservice/MainActivity.java
 * <p>
 * comment - the comment the user left with the event. Always less than 20 characters
 * date -  the date the event was created in form yyyy_mm_dd
 * latitude - String of the latitude of the user location, if enable otherwise null
 * longitude - String of the longitude of the user location, if enable otherwise null
 * fileName - the name of the file where the image bitmap is stored
 * habitType - the associated HabitType title of the event
 *
 * @author rarog
 */

public class AddHabitEventDialog extends DialogFragment {
    private OnAddHabitEventListener onAddHabitEventListener;
    private Bundle resultBundle;

    // layout views
    private TextureView cameraTextureView;
    private ImageButton eventImage;
    private Bitmap eventBitmap;
    Context context;
    TextView commentText;
    TextView commentWarning;
    Button createButton;
    Spinner spinner;
    CheckBox checkBox;

    private static final String TAG = "Add event dialog";
    private static Context mContext;

    private boolean cameraPermission;
    private boolean locationPermission;

    Camera camera;
    boolean addEventImageViewDebounce = false;

    // location controller
    private FusedLocationProviderClient fusedLocationClient;
    protected Location lastLocation;

    //region camera controller
    TextureView.SurfaceTextureListener cameraPreviewSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            if (cameraPermission) {
                camera.setup(context, width, height);
                camera.open(context);
            }
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
            if (cameraPermission) {
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
                                    if (params.obj == null) {
                                        addEventImageViewDebounce = false;
                                    } else {
                                        cameraTextureView.setAlpha(1f - 0.8f * MathUtility.EasingOut(System.currentTimeMillis() - ((long[]) params.obj)[0], ((long[]) params.obj)[1], 3));
                                    }
                                }
                            };
                            MathUtility.Animate(100, 500, responseHandler);
                        }
                }
            }
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, @NonNull CaptureRequest request, CaptureFailure failure) {

        }
    }; //endregion

    /**
     * @param onAddHabitEventListener A HabitEvent Listener which is used to detect when the
     *                                create / delete buttons are pushed
     */
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
        cameraPermission = checkCameraPermissions();
        locationPermission = checkLocationPermissions();
        if (locationPermission) {
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
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

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
        if (commentField.length() <= HabitEvent.MAX_COMMENT_LENGTH) {
            createButton.setEnabled(true);
            commentWarning.setVisibility(View.INVISIBLE);

        } else {
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
        mContext = getActivity().getApplicationContext();
        final UserProfile profile = new UserProfile(mContext);
        TreeGrowth profileTreeGrowth = profile.treeGrowth;

        // creates all of the necessary view controllers
        TextView eventTitle = view.findViewById(R.id.addHabitEventDialogTitle);
        spinner = view.findViewById(R.id.addHabitEventSpinner);
        eventImage = view.findViewById(R.id.addHabitEventImageButton);
        cameraTextureView = view.findViewById(R.id.addEventCameraPreviewTextureView);
        final ImageButton captureButton = view.findViewById(R.id.addEventCaptureButton);
        checkBox = view.findViewById(R.id.addHabitEventCheckBox);
        commentText = view.findViewById(R.id.addEventCommentText);
        commentWarning = view.findViewById(R.id.addEventCommentWarning);
        Button cancelButton = view.findViewById(R.id.addEventCancelButton);
        createButton = view.findViewById(R.id.addEventCreateButton);

        // makes the location can camera handlers
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        camera = new Camera(cameraTextureView, cameraCaptureSessionCallback, eventImage);

        // when you click on add image, open the camera and capture button
        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnCaptureButton(captureButton);
                eventImage.setVisibility(ImageButton.INVISIBLE);
                if (!addEventImageViewDebounce) {
                    addEventImageViewDebounce = true;
                    Handler responseHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message params) {
                            if (params.obj == null) {
                                addEventImageViewDebounce = false;
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
        turnOffCaptureButton(captureButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set camera's auto-focus lock
                camera.takePhoto();
                eventImage.setVisibility(ImageButton.VISIBLE);
                turnOffCaptureButton(captureButton);
            }
        });

        // add the text listener to display warning when comment is too long
        commentWarning.setVisibility(View.INVISIBLE);
        commentText.addTextChangedListener(commentTextWatcher);

        // gets the passed information from calling activity
        String title = getTitleFromBundle();
        ArrayList<String> habits = getHabitTypesFromBundle();
        if(habits.size() <= 1) {
            spinner.setFocusable(false);
            spinner.setVisibility(View.INVISIBLE);
            spinner.setClickable(false);
            TextView titleText = view.findViewById(R.id.addEventHabitTypeTextView);
            titleText.setText(habits.get(0));
        }

        // sets list adapter for the spinner
        ListAdapter listAdapter = new ArrayAdapter<>(context,
                R.layout.habit_type_spinner_layout,
                R.id.habitTypeSpinnerTextView, habits);
        spinner.setAdapter((SpinnerAdapter) listAdapter);

        eventTitle.setText("Add " + title + " event");

        // when we create, gather the relevant information, inform calling activity, dismiss dialog
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultBundle = createHabitEventBundle();
                onAddHabitEventListener.OnAdded();
                dialog.dismiss();

                SharedPreferences sharedPreferences = context.getSharedPreferences(HABILECT_USER_INFO, Context.MODE_PRIVATE);
                String preference = sharedPreferences.getString(UserProfile.HABILECT_USER_TREE_GROWTH, null);

                int nutrientLevel = Integer.parseInt(preference);

                //test numbers
                nutrientLevel += 30;

                SharedPreferences.Editor editor = sharedPreferences.edit();
                profile.setTreeGrowth(Integer.toString(nutrientLevel));
                editor.putString(UserProfile.HABILECT_USER_TREE_GROWTH, Integer.toString(nutrientLevel));
                editor.commit();
                Log.i("NUTRIENTLEVEL: ", "" + profileTreeGrowth.getNutrientLevel());

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
        if (cameraPermission) {
            camera.close();
        }
        super.onPause();
    }

    /**
     * Disable the capture button
     */
    private void turnOffCaptureButton(ImageButton captureButton) {
        captureButton.setVisibility(View.INVISIBLE);
        captureButton.setEnabled(false);
        captureButton.setClickable(false);
        captureButton.setFocusable(false);
    }

    /**
     * Enables the capture button
     */
    private void turnOnCaptureButton(ImageButton captureButton) {
        captureButton.setVisibility(View.VISIBLE);
        captureButton.setEnabled(true);
        captureButton.setClickable(true);
        captureButton.setFocusable(true);
    }

    /**
     * @return returns a bundle with all of the information that the user specified
     * in creating the event
     */
    public Bundle getResultBundle() {
        return resultBundle;
    }


    /**
     * @return Creates a resultBundle, which holds all of the information the user inputted
     * in creating the event
     */
    private Bundle createHabitEventBundle() {
        Bundle bundle = new Bundle();
        String latitude;
        String longitude;
        String habitType;
        String comment;
        String date;

        // gets location
        if (checkBox.isChecked() && locationPermission) {
            latitude = String.valueOf(lastLocation.getLatitude());
            longitude = String.valueOf(lastLocation.getLongitude());
        } else {
            latitude = null;
            longitude = null;
        }
        // gets comment and makes new date
        comment = commentText.getText().toString();
        date = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).format(new Date());

        // gets the selected title
        if (spinner.getSelectedItem() != null) {
            habitType = spinner.getSelectedItem().toString();
        } else {
            habitType = "";
        }
        // makes the file name, in form habitTitle_yyyy_mm_dd
        String fileName = habitType.replace(" ", "_") + "_" + date;

        // saves the image in file, save the directory and file name
        String directory = saveImageInFile(fileName);
        bundle.putString("fileName", fileName);
        bundle.putString("directory", directory);

        // put all information in bundle
        bundle.putString("comment", comment);
        bundle.putString("date", date);
        bundle.putString("latitude", latitude);
        bundle.putString("longitude", longitude);
        bundle.putString("habitType", habitType);

        return bundle;
    }

    // https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    private String saveImageInFile(String fileName) {
        eventBitmap = ((BitmapDrawable) eventImage.getDrawable()).getBitmap();
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("eventImages", Context.MODE_PRIVATE);
        File myPath = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            eventBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();// TODO: Don't want to crash, just don't have image
            }
        }
        return directory.toString();
    }

    /**
     * @return a String representing the habit event title if there is one
     */
    private String getTitleFromBundle() {
        try {
            String title = getArguments().getString("Title");
            return title == null ? "" : title;
        } catch (Exception e) {
            return "";
        }
    }

    /**
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


    // TODO: seems to be a bug where it can't get location unless you open an app like google maps
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

    // TODO: Maybe have all of the permission handling / asking in a separate class

    /**
     * @return a boolean representing if we have permission to access user location
     */
    private boolean checkLocationPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @return a boolean representing if we have permission to access the camera
     */
    private boolean checkCameraPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
}