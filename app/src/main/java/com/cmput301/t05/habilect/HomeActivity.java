package com.cmput301.t05.habilect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author ioltuszy
 */

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "Add event dialog";
    private static final int REQUEST_PERMISSIONS__LOCATION_REQUEST_CODE = 10;
    private static final int REQUEST_PERMISSIONS__CAMERA_REQUEST_CODE = 20;
    private static final int REQUEST_PERMISSIONS__MULTIPLE_REQUEST_CODE = 20;
    private boolean cameraPermission;
    private boolean locationPermission;

    Activity currentActivity = this;

    FragmentManager fragmentManager = getSupportFragmentManager();
    ViewPagerHomeAdapter homeViewPagerAdapter;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onStart() {
        super.onStart();
        // if user does not have location or camera permissions set, will ask to enable them
        cameraPermission = checkCameraPermissions();
        locationPermission = checkLocationPermissions();
        startPermissionRequest();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeViewPagerAdapter = new ViewPagerHomeAdapter(fragmentManager);
        ViewPager mHomeViewPager = (ViewPager) findViewById(R.id.homeViewPager);
        mHomeViewPager.setAdapter(homeViewPagerAdapter);

        Navigation.setup(findViewById(android.R.id.content));
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkLocationPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkCameraPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startPermissionRequest() {
        if(cameraPermission && !locationPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS__LOCATION_REQUEST_CODE);
        }
        else if (!cameraPermission && locationPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSIONS__CAMERA_REQUEST_CODE);
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS__MULTIPLE_REQUEST_CODE);
        }
    }

    // TODO: if permission is denied, inform user parts of app wont work
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS__LOCATION_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
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
                Snackbar snackbar = Snackbar.make(this.getCurrentFocus(), "Test", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }
}