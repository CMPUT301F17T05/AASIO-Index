package com.cmput301.t05.habilect;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author alexisseniuk
 * MapsActivity for Visual Representation of Habits
 * Currently something wrong with API key.....
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private UserAccount userAccount;
    private Context context;
    private GoogleMap mMap;
    private List<HabitType> allHabitTypes;
    private List<HabitEvent> allHabitEvents;
    private Button show5kmButton;
    private Button resetButton;
    private Button backButton;

    private int ZOOM_LEVEL = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = this;
        userAccount = new UserAccount().load(context);

        allHabitTypes = userAccount.getHabits();
        allHabitEvents = getEventsFromBundle();

        Navigation.setup(findViewById(android.R.id.content));

        setTitle("Habilect - Map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        show5kmButton = findViewById(R.id.mapActivityShow5kmButton);
        resetButton = findViewById(R.id.mapActivityResetMapButton);
        backButton = findViewById(R.id.mapActivityBackButton);

        show5kmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allHabitTypes = userAccount.getHabits();
                loadAllUserHabitEvents();
                ZOOM_LEVEL = 10;
                setUserMarkers(allHabitEvents);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allHabitEvents.clear();
                ZOOM_LEVEL = 1;
                setUserMarkers(allHabitEvents);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private List<HabitEvent> getEventsFromBundle() {
        try {
            return (List<HabitEvent>) getIntent().getExtras().getSerializable("Events");
        } catch (Exception e) {
            return new ArrayList<>();
        }
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
        setUserMarkers(allHabitEvents);
    }

    //gotoLocation with a zoomed in focus - focus downtown Edmonton
    private void goToLocationZoom(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL);
        mMap.moveCamera(update);
    }

    //set markers for habit events that have locations
    public void setUserMarkers(List<HabitEvent> eventList) {
        //add all User events Markers (azure)
        LatLng latLng = new LatLng(0, 0);
        for (HabitEvent e: eventList) {
            //final Lat Lng position = new LatLng(habit_type.getHabitEvents().get)
            Location location = e.getLocation();
            //LatLng loc = e.getLocation();
            if (location != null){
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(e.getHabitType())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        }
        if(latLng != null) {
            goToLocationZoom(latLng.latitude, latLng.longitude);
        }
    }

    private void loadAllUserHabitEvents() {
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
