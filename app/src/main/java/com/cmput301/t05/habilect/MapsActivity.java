package com.cmput301.t05.habilect;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author alexisseniuk
 *         MapsActivity for Visual Representation of Habits
 *         Currently something wrong with API key.....
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    protected Location lastLocation;
    private UserAccount userAccount;
    private Context context;
    private GoogleMap mMap;
    private List<HabitEvent> originalEventList;
    private List<HabitEvent> allHabitEvents;
    private Button show5kmButton;
    private Button resetButton;
    private Button backButton;
    private FusedLocationProviderClient fusedLocationClient;
    private int ZOOM_LEVEL = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = this;
        userAccount = new UserAccount().load(context);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        allHabitEvents = getEventsFromBundle();
        originalEventList = new ArrayList<>(allHabitEvents);

        if (allHabitEvents.size() < 1) {
            ZOOM_LEVEL = 1; // if we have nothing to show, display the whole world
        }

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
                mMap.clear();
                setCurrentLocationMarker();
                allHabitEvents = filterBy5km(allHabitEvents);
                ZOOM_LEVEL = 15;
                setUserMarkers(allHabitEvents);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                setCurrentLocationMarker();
                ZOOM_LEVEL = 15;
                setUserMarkers(originalEventList);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        getLastLocation();
        setCurrentLocationMarker();
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

    private HabitEvent findHabitEvent(String title) {
        for (HabitEvent event : allHabitEvents) {
            if (event.getHabitType().equals(title)) {
                return event;
            }
        }
        return null;
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
        for (HabitEvent e : eventList) {
            //final Lat Lng position = new LatLng(habit_type.getHabitEvents().get)
            Location location = e.getLocation();
            //LatLng loc = e.getLocation();
            if (location != null) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(e.getHabitType())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        }
        if (latLng.latitude != 0 && latLng.longitude != 0) {
            goToLocationZoom(latLng.latitude, latLng.longitude);
        }
    }

    private void setCurrentLocationMarker() {
        if (lastLocation != null) {
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Current location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    /**
     * From a list of events, allows user to filter by showing event only 5km away or less
     *
     * @param eventList the list of events you want to filer
     * @return returns the filtered list
     */
    private List<HabitEvent> filterBy5km(List<HabitEvent> eventList) {
        Iterator<HabitEvent> iterator = eventList.iterator();
        getLastLocation();
        while (iterator.hasNext()) {
            HabitEvent event = iterator.next();
            if (eventNear(event)) {
                continue;
            }
            iterator.remove();
        }
        return eventList;
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener((Activity) context, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastLocation = task.getResult();
                            setCurrentLocationMarker();
                        } else {
                            lastLocation = null;
                        }
                    }
                });
    }

    /**
     * Calculates if a given event is 5km or less from your current location
     *
     * @param event the event you want to check
     * @return boolean representing if it is nearby
     */
    private boolean eventNear(HabitEvent event) {
        // Adapted from https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
        if (lastLocation == null || event.getLocation() == null) {
            return false;
        }

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(event.getLocation().getLatitude() - lastLocation.getLatitude());
        double dLng = Math.toRadians(event.getLocation().getLongitude() - lastLocation.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lastLocation.getLatitude())) * Math.cos(Math.toRadians(event.getLocation().getLatitude())) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        if (dist <= 5000) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Using the events information, makes a bundle so the view event activity can be properly filled
     *
     * @param event the habit event that you want to view
     * @return a bundle that can be sent off to the activity
     */
    private Bundle sendHabitInfoToView(HabitEvent event) {
        Bundle bundle = new Bundle();
        bundle.putString("Title", event.getHabitType());
        String dateString = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).format(event.getCompletionDate());
        bundle.putString("Date", event.getCompletionDateString());
        bundle.putString("Comment", event.getComment());
        bundle.putString("File Path", event.getHabitType().replace(" ", "_") + "_" + dateString);

        return bundle;
    }
}
