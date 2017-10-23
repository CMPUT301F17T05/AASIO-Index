package com.cmput301.t05.habilect;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author ioltuszy
 * @author amwhitta
 */

public class HabitType {

    private String title;
    private String reason;
    private Date start_date;
    private boolean shared;
    private List<UserProfile> followers;

    // Constructor
    public HabitType(String title, String reason, Date startDate) {
        this.setTitle(title);
        this.setReason(reason);
        this.setStartDate(startDate);
        this.setShared(false);
        this.followers = null;
    }

    // Getters
    public String getTitle() {
        return this.title;
    }
    public String getReason() {
        return reason;
    }
    public Date getStartDate() {
        return this.start_date;
    }
    public boolean getShared() {
        return this.shared;
    }
    public List<UserProfile> getFollowers() {
        return this.followers;
    }

    // Setters
    public void setTitle(String title) {
        if (title.length() == 0 || title.length() > 20) {
            throw new IllegalArgumentException();
        } else {
            this.title = title;
        }
    }
    public void setReason(String reason) {
        if (reason.length() == 0 || reason.length() > 30) {
            throw new IllegalArgumentException();
        } else {
            this.reason = reason;
        }
    }
    public void setStartDate(Date date) {
        /* N/A, based on old assumption:
        if (date.before(Calendar.getInstance().getTime())) {
            try {
                throw new IllegalArgumentException();
            }
            catch (IllegalArgumentException e) {
                Log.e("dateError", "Supplied start date has already passed");
            }
        }*/
        this.start_date = date;
    }
    public void setShared(boolean shared) {
        this.shared = shared;
    }
}
