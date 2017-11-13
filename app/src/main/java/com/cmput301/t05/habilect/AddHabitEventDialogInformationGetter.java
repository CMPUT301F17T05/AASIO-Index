package com.cmput301.t05.habilect;

import android.location.Location;
import android.os.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is used to facilitate getting information from an addHabitEventDialog.
 * It should be passed the resultBundle from the dialog when making the class. Then
 * simply call the methods you want to get the information you need.
 * @author rarog
 */

public class AddHabitEventDialogInformationGetter {
    Bundle bundle;

    AddHabitEventDialogInformationGetter(Bundle bundle) {
        this.bundle = bundle;
    }

    public String getTitle() {
        String title = bundle.getString("habitType");
        return title == null ? "" : title ;
    }

    public String getComment() {
        String comment = bundle.getString("comment");
        return comment == null ? "" : comment ;
    }


    public Date getDate() {
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy_mm_dd");
        Date date = new Date();
        try {
            date = simpleDate.parse(bundle.getString("date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Location getLocation() {
        String latitude = bundle.getString("latitude");
        String longitude = bundle.getString("longitude");

        Location location = new Location("");
        if(latitude != null || longitude != null) {
            location.setLatitude(Double.parseDouble(latitude));
            location.setLongitude(Double.parseDouble(longitude));
            return location;
        }
        return null;
    }

    public String getFileName() {
        String fileName = bundle.getString("fileName");
        return fileName == null ? "" : fileName ;
    }

    public String getDirectory() {
        String directory = bundle.getString("directory");
        return directory == null ? "" : directory ;
    }
}
