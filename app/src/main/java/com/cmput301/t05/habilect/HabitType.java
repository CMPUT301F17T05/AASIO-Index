package com.cmput301.t05.habilect;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ioltuszy on 10/19/17.
 */

public class HabitType {

    private String title;
    private String purpose;
    private Date startDate;
    private Boolean shared;
    private List<UserProfile> followers;

    public String getTitle() {
        return this.title;
    }
    public String getPurpose() {
        return purpose;
    }
    public Date getStartDate() {
        return this.startDate;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    public void setStartDate(Date date) {
        /* when current time greater than startDate, then the result should be negative
        if (date.before(Calendar.getInstance().getTime())) {
            try {
                throw new IllegalArgumentException();
            }
            catch (IllegalArgumentException e) {
                Log.e("dateError", "Supplied start date has already passed");
            }
        }*/
        this.startDate = date;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public HabitType(String title, String reason, Date startDate) {
        this.setTitle(title);
        this.setPurpose(reason);
        this.setStartDate(startDate);
    }
}
