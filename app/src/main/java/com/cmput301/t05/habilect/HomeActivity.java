package com.cmput301.t05.habilect;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * The main activity of the app, displays the users habit types that still need to be done
 * options to add types and events and shows their progress tree
 *
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

//        if (googleServicesAvailable()){
//            Toast.makeText(this, "Google services available!", Toast.LENGTH_LONG).show();
//        }
        homeViewPagerAdapter = new ViewPagerHomeAdapter(fragmentManager);
        ViewPager mHomeViewPager = (ViewPager) findViewById(R.id.homeViewPager);
        mHomeViewPager.setAdapter(homeViewPagerAdapter);
        mHomeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("POSITION: ", "" + position);
                mHomeViewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Navigation.setup(findViewById(android.R.id.content));
    }

    /**
     * Return the current state of the permissions needed.
     *
     * @return returns true if we have access to the users location
     */
    private boolean checkLocationPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Return the current state of the permissions needed.
     *
     * @return returns true if we have access to the users camera
     */
    private boolean checkCameraPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Depending on which permissions may be need, ask the user to enable them
     */
    private void startPermissionRequest() {
        if (cameraPermission && !locationPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS__LOCATION_REQUEST_CODE);
        } else if (!cameraPermission && locationPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSIONS__CAMERA_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS__MULTIPLE_REQUEST_CODE);
        }
    }

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
            }
        }
    }
}