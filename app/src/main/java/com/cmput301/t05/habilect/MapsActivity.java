package com.cmput301.t05.habilect;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author alexisseniuk
 * MapsActivity for Visual Representation of Habits
 * Currently something wrong with API key.....
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private UserAccount userAccount;
    private Context context;
    private GoogleMap mMap;
    private HabitType habit_type;
    private Bundle bundle;
    private TextView userName;
    private Location location;
    private List<HabitType> allHabitTypes;
    private List<HabitEvent> allHabitEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = this;
        userAccount = new UserAccount().load(context);
        allHabitTypes = userAccount.getHabits();
        loadAllHabitEvents();


        setTitle("H a b i l e c t - Map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //googlesevices good to go
        if (googleServicesAvailable()) {
            Toast.makeText(this, "GoogleMaps Services Available!", Toast.LENGTH_LONG).show();

            //no googleservices
        }else {
            Toast.makeText(this, "No GoogleMap Services Available...", Toast.LENGTH_LONG).show();

        }

        //TODO: get friend info and plot from setFriendsMarkers (if working)
        //get friend info
/*        bundle = getIntent().getExtras();
        userName = findViewById(R.id.viewFriendUserName);
        location = findViewById(R.id.viewFriendLocation);

        userName.setText(getUserNameFromBundle());
        location.setLocation(Location);*/


    }

    private boolean googleServicesAvailable() {
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



       //edmonton ---  goToLocationZoom(53.534172,-113.488460, 10);
        LatLng Edmonton = new LatLng(53.534172,-113.488460);
        mMap.addMarker(new MarkerOptions().position(Edmonton).title("Marker in Edmonton"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Edmonton));

        //Default setting:
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        //INSERT BUTTON IF WORKS...
        setUsermarkers();


    }

    //gotoLocation with a zoomed in focus - focus downtown Edmonton
    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }

    private int totalEvents() {
        List<HabitEvent> events = habit_type.getHabitEvents();
        return events.size();
    }

    //set markers for habit events that have locations
    public void setUsermarkers() {
        //add all User events Markers (azure)
        for (HabitEvent e: allHabitEvents) {
            //final Lat Lng position = new LatLng(habit_type.getHabitEvents().get)
            Location location = e.getLocation();
            //LatLng loc = e.getLocation();
            if (location != null){
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(e.getHabitType())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }


        }
    }

    private void loadAllHabitEvents() {
        Iterator<HabitType> iterator = allHabitTypes.iterator();
        allHabitEvents = new ArrayList<>();
        while(iterator.hasNext()) {
            HabitType habit = iterator.next();
            ArrayList<HabitEvent> eventList = habit.getHabitEvents();
            allHabitEvents.addAll(eventList);
        }
    }
//set markers for user's friend's habits
/*    public void setFriendMarkers() {
        //List<FriendHabitEvent> events = habit_type.getHabitEvents();

        //add Friends markers (magenta)
        for (FriendHabitEvents friende: events){
            LatLng location = friende.getLocation();
            if (location != null){
                mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(friende.getHabitType())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            }
        }
    }*/

    //use if using bundles for habitfriend list
    /**
     *
     * @return a String representing the habit event title if there is one
     */
/*    private String getUserNameFromBundle() {
        try {
            return bundle.getString("User Name");
        }
        catch (Exception e) {
            return "";
        }
    }

    private Location getLocationFromBundle() {
        try {
            return bundle.getParcelable("Location");
        }
        catch (Exception e) {
            return null;
        }
    }*/

    //for calulating within 5km distance 
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
    }*/
}
