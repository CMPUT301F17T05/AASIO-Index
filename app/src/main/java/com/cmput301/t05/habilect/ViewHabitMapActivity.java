package com.cmput301.t05.habilect;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;

/**
 * @author alexisseniuk
 */

public class ViewHabitMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mgoogleMap;
    private UserProfile userProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit_map);

        setTitle("H a b i l e c t - Map");


        // A : BUTTON FOR : showing myhabitevents on the map
 //       final Button myhabiteventsbutton = (Button) findViewById(R.id.myhabiteventsbutton);
        //myhabiteventsbutton.setOnClickListener(new View.OnClickListener() {

//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(currentActivity, FriendActivity.class);
//                startActivity(intent);
//            }
//        });

        // B : BUTTON FOR : showing 5km of my habit events on the map
//        final Button show5kmbutton = (Button) findViewById(R.id.show5kmbutton);
        //show5kmbutton.setOnClickListener(new View.OnClickListener() {

//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(currentActivity, FriendActivity.class);
//                startActivity(intent);
//            }
//        });

        // C : BUTTON FOR : showing friendhabitevents on the map
//        final Button friendhabiteventsbutton = (Button) findViewById(R.id.friendhabiteventsbutton);
        //friendhabiteventsbutton.setOnClickListener(new View.OnClickListener() {

//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(currentActivity, FriendActivity.class);
//                startActivity(intent);
//            }
//        });


        if (googleServicesAvailable()) {
            Toast.makeText(this, "GoogleMaps Working!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_view_habit_map);
            initMap();
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
        goToLocationZoom(53.534172,-113.488460, 10);

        //SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");

        //add all of the markers the user profile has for their HabitEvents
        //TODO: need to fix getLocation
        //TODO: need to fix completion_date

        //for (HabitType habitType : userProfile.getHabits().getArrayListofHabits()){
        //    if (HabitEvent.getLocation() != null) {
        //        mgoogleMap.addMarker(new MarkerOptions().position(HabitEvent.getLocation()).title(getTitle()).snippet(sourceFormat.format(HabitEvent.completion_date())));

        //    }
    }

    //TO DO:

    //gotoLocation without a zoomed in focus
    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mgoogleMap.moveCamera(update);
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mgoogleMap.moveCamera(update);
    }


}








