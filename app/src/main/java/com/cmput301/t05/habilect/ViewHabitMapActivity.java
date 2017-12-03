/*
package com.cmput301.t05.habilect;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.google.android.gms.maps.model.LatLng;
import android.widget.Toast;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

*/
/**
 * @author alexisseniuk
 * 1st MapActivity Map Version
 * Combine into MapsActivity.java when working.
 *//*


public class ViewHabitMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public ViewHabitMapActivity(HabitType habit_type) {
        this.habit_type = habit_type;
    }

    GoogleMap mgoogleMap;

    private HabitType habit_type;

    private boolean center_on_user;
    private boolean highlight_near;

    private HashMap<LatLng, Marker> myMarkers = new HashMap<>();
    private UserProfile user;

    protected LatLng mLastLocation = new LatLng(53.534172, -113.488460);
    private LatLng mDefaultLocation = new LatLng(53.534172, -113.488460);

    // Bundle Keys
    public static final String USER_LOC_KEY = "user location";
    public static final String CENTER_USER_LOC_KEY = "if centering on user location";
    public static final String HIGHLIGHT_NEAR_KEY = "highlight nearby locations";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit_map);

        setTitle("H a b i l e c t - Map");

        mLastLocation = (mLastLocation) getIntent().getExtras().getParcelable(USER_LOC_KEY);
        if (mLastLocation != null){
            Log.d("    MAPS    ", "Last Location:" + mLastLocation.toString());
        }
        center_on_user = getIntent().getExtras().getBoolean(CENTER_USER_LOC_KEY, false);
        highlight_near = getIntent().getExtras().getBoolean(HIGHLIGHT_NEAR_KEY, false);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

*/
/*         A : BUTTON FOR : showing myhabitevents on the map
        final Button myhabiteventsbutton = (Button) findViewById(R.id.myhabiteventsbutton);
        myhabiteventsbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentActivity, FriendActivity.class);
                startActivity(intent);
            }
        });

         B : BUTTON FOR : showing 5km of my habit events on the map
        final Button show5kmbutton = (Button) findViewById(R.id.show5kmbutton);
        show5kmbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentActivity, FriendActivity.class);
                startActivity(intent);
            }
        });

         C : BUTTON FOR : showing friendhabitevents on the map
        final Button friendhabiteventsbutton = (Button) findViewById(R.id.friendhabiteventsbutton);
        friendhabiteventsbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentActivity, FriendActivity.class);
                startActivity(intent);
            }
        });

         D : BUTTON FOR : resetting/clearing the map to default location/zoom
        final Button resetMapButton = (Button) findViewById(R.id.resetMapButton);
        resetMapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentActivity, FriendActivity.class);
                startActivity(intent);
            }
        });*//*


        if (googleServicesAvailable()) {
            Toast.makeText(this, "GoogleServices Available!", Toast.LENGTH_LONG).show();
           // setContentView(R.layout.activity_view_habit_map);
           // initMap();
        }else {
            Toast.makeText(this, "No Google Maps Layout!", Toast.LENGTH_LONG).show();

        }
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS){
            return true;
        } else if (api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //sets camera scope to default location of downtown edmonton
        mgoogleMap = googleMap;
        setMarkers();
        goToLocationZoom(53.534172,-113.488460, 10);

        //SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");

        //add all of the markers the user profile has for their HabitEvents

        //for (HabitType habitType : userProfile.getHabits().getArrayListofHabits()){
        //    if (HabitEvent.getLocation() != null) {
        //        mgoogleMap.addMarker(new MarkerOptions().position(HabitEvent.getLocation()).title(getTitle()).snippet(sourceFormat.format(HabitEvent.completion_date())));

        //    }
    }

*/
/*    private boolean eventNear(HabitEvent e){
        // Adapted from https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
        if (mLastLocation == null || !highlight_near){
            return false;
        }

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(e.getLocation().latitude-mLastLocation.getLatitude());
        double dLng = Math.toRadians(e.getLocation().longitude-mLastLocation.getLongitude());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(mLastLocation.getLatitude())) * Math.cos(Math.toRadians(e.getLocation().latitude)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        if (dist <= 5000){
            return true;
        } else {
            return false;
        }
    }*//*




    //gotoLocation without a zoomed in focus
    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mgoogleMap.moveCamera(update);
    }
    //gotoLocation with a zoomed in focus - focus downtown Edmonton
    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mgoogleMap.moveCamera(update);
    }

    //set user markers
    private void setMarkers(<LatLng, Marker> markerMap){
        List<HabitEvent> events = habit_type.getHabitEvents();


        SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
        Float bitMapColor;
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        bitMapColor = BitmapDescriptorFactory.HUE_AZURE;
        // TODO: add in friend portion (following/social part)
//        if (isFriend) {
//            //friend is green
//            bitMapColor = BitmapDescriptorFactory.HUE_BLUE;
//        } else {
//            // blue for userProfile
//            bitMapColor = BitmapDescriptorFactory.HUE_AZURE;
//        }


        // set all the markers a user has on their events

        //for (HabitEvent habit : userProfile.getHabits().getArrayList()) {
        for (HabitEvent e: events){
            //LatLng location = e.getLocation();
            //final Lat Lng position = new LatLng(list.get(i).getCurrent_lat(), list.get(i).getCurrent_lng());
            // to ensure we start at the last place in the list in case this call is for a friend
                if (HabitEvent.getLocation() != null) {
                    Marker marker = mgoogleMap.addMarker(new MarkerOptions().position(HabitEvent.getLocation())
                            .draggable(false)
                            .title(e.getHabitType())
                            //.visible(isFriend)
                            .icon(BitmapDescriptorFactory.defaultMarker(bitMapColor));
                    markerMap.put(HabitEvent.getLocation(), marker);
                }
            }
        }
    }



//    private void setVisibleMarkers(List<HabitTypeActivity.HabitTypeEventsFragment> eventsFragmentList, Boolean withDistance) {
//        for (HabitEvent event : eventList)  {
//            LatLng location = HabitEvent.getLocation();
//            if (myMarkers.get(location) != null) {
//                float[] dist = {0f,0f,0f};
//                Location.distanceBetween(mDefaultLocation.latitude, mDefaultLocation.longitude,
//                        location.latitude, location.longitude, dist);
//                if (withDistance && (dist[0] <= DISTANCE)) {
//                    myMarkers.get(location).setVisible(true);
//                } else {
//                    myMarkers.get(location).setVisible(true);
//                }
//            }
//        }
//
//    }

}

*/











