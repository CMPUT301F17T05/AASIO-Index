package com.cmput301.t05.habilect;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

/**
 * @author ioltuszy
 * @author amwhitta
 */

public class HabitType {

    private String title;
    private String reason;
    private Date start_date;
    private boolean[] weekly_plan;
    private boolean shared;
    private List<UserProfile> followers;

    // Constructor
    public HabitType(String title, String reason, Date start_date, boolean[] weekly_plan) {
        this.setTitle(title);
        this.setReason(reason);
        this.setStartDate(start_date);
        this.setWeeklyPlan(weekly_plan);
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
    public boolean[] getWeeklyPlan() { return weekly_plan; }
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
    public void setWeeklyPlan(boolean[] weekly_plan) { this.weekly_plan = weekly_plan; }
    public void setShared(boolean shared) {
        this.shared = shared;
    }

    // Behaviours
    public int totalEventOpportunities() {
        int count = 0;
        Calendar start = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        start.setTime(start_date);
        while (start.before(today)) {
            switch (start.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    if (weekly_plan[0]) { ++count; }
                    break;
                case Calendar.TUESDAY:
                    if (weekly_plan[1]) { ++count; }
                    break;
                case Calendar.WEDNESDAY:
                    if (weekly_plan[2]) { ++count; }
                    break;
                case Calendar.THURSDAY:
                    if (weekly_plan[3]) { ++count; }
                    break;
                case Calendar.FRIDAY:
                    if (weekly_plan[4]) { ++count; }
                    break;
                case Calendar.SATURDAY:
                    if (weekly_plan[5]) { ++count; }
                    break;
                case Calendar.SUNDAY:
                    if (weekly_plan[6]) { ++count; }
                    break;
            }
            start.add(Calendar.DATE, 1);
        }
        return count;
    }
}
