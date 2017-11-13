package com.cmput301.t05.habilect;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

/**
 * This is the model for habit types.
 * @author ioltuszy
 * @author amwhitta
 */
public class HabitType extends Observable implements Serializable {

    private String title;
    private String reason;
    private Date start_date;
    private boolean[] weekly_plan;
    private boolean shared;
    private List<UserProfile> followers;
    private Date recent_habit_event;
    private List<HabitEvent> habitEvents = new ArrayList<>();

    /**
     * The Constructor for HabitType
     * @param title                 a String with 0 < length <= 20
     * @param reason                a String with 0 < length <= 30
     * @param start_date            a Date specifying when the user started the habit type
     * @param weekly_plan           the days of the week that the user plans on doing the habit type
     */
    public HabitType(String title, String reason, Date start_date, boolean[] weekly_plan) {
        this.setTitle(title);
        this.setReason(reason);
        this.setStartDate(start_date);
        this.setWeeklyPlan(weekly_plan);
        this.setShared(false);
        this.followers = null;
    }

    /**
     * Copy constructor for HabitType
     *
     * @param copy
     */
    public HabitType(HabitType copy) {
        this.title = copy.title;
        this.reason = copy.reason;
        this.start_date = copy.start_date;
        this.weekly_plan = copy.weekly_plan;
        this.shared = copy.shared;
        this.followers = copy.followers;
        this.recent_habit_event = copy.recent_habit_event;
        this.habitEvents = copy.habitEvents;
    }

    // GETTERS

    public String getTitle() {
        return this.title;
    }
    public String getReason() {
        return this.reason;
    }
    public Date getStartDate() {
        return this.start_date;
    }
    public boolean[] getWeeklyPlan() {
        return this.weekly_plan;
    }
    public String getWeeklyPlanString() {
        String plan = "Every ";
        int count = 0;
        for (int i = 0; i < weekly_plan.length; ++i) {
            if (weekly_plan[i]) {
                if (i == 0) { plan = plan + "Monday, "; ++count; }
                else if (i == 1) { plan = plan + "Tuesday, "; ++count; }
                else if (i == 2) { plan = plan + "Wednesday, "; ++count; }
                else if (i == 3) { plan = plan + "Thursday, "; ++count; }
                else if (i == 4) { plan = plan + "Friday, "; ++count; }
                else if (i == 5) { plan = plan + "Saturday, "; ++count; }
                else if (i == 6) { plan = plan + "Sunday, "; ++count; }
            }
        }
        if (count == 7) { plan = "Every day"; }
        else { plan = plan.substring(0, plan.length() - 2); }
        return plan;
    }
    public boolean getShared() {
        return this.shared;
    }
    public List<UserProfile> getFollowers() {
        return this.followers;
    }

    // SETTERS

    /**
     * sets the habit type title. An IllegalArgumentException is thrown
     * when the character length is out of range.
     *
     * @param title                 a String with 0 < length <= 20
     */
    public void setTitle(String title) {
        if (title.length() == 0 || title.length() > 20) {
            throw new IllegalArgumentException("title");
        } else {
            this.title = title;
        }
    }

    /**
     * sets the habit type reason. An IllegalArgumentException is thrown
     * when the character length is out of range
     *
     * @param reason                a String with 0 < length <= 30
     */
    public void setReason(String reason) {
        if (reason.length() == 0 || reason.length() > 30) {
            throw new IllegalArgumentException("reason");
        } else {
            this.reason = reason;
        }
    }

    /**
     * sets the habit type start date.
     * @param date                  a Date specifying when the user started the habit type
     */
    public void setStartDate(Date date) {
        this.start_date = date;
    }

    /**
     * sets the habit type weekly plan. An IllegalArgumentException is thrown
     * when there are no days checked.
     *
     * @param weekly_plan           a boolean array with index 0 corresponding to Monday and index
     *                              6 corresponding to Sunday
     */
    public void setWeeklyPlan(boolean[] weekly_plan) {
        int count_true = 0;
        for (boolean b : weekly_plan) {
            if (b) {
                ++count_true;
            }
        }
        if (count_true == 0) {
            throw new IllegalArgumentException("plan");
        } else {
            this.weekly_plan = weekly_plan;
        }
    }

    /**
     * sets whether or not the user wants this habit type to be shared online
     * @param shared                a boolean specifying shared or not
     */
    public void setShared(boolean shared) {
        this.shared = shared;
    }

    /**
     * sets the date of the most recent habit event created from this habit type
     *
     * @param date                  the Date when the habit event was created
     */
    public void setRecentHabitEvent(Date date) {
        this.recent_habit_event = date;
    }

    /**
     * Adds a habit event to this habit type.
     *
     * @param habitEvent                  the habitEvent which to add
     */
    public void addHabitEvent(HabitEvent habitEvent){
        this.habitEvents.add(habitEvent);
    }

    /**
     * Removes a habit event from this habit type.
     *
     * @param habitEvent                  the habitEvent which to remove
     */
    public void removeHabitEvent(HabitEvent habitEvent){
        this.habitEvents.remove(habitEvent);
    }

    // BEHAVIOURS

    /**
     * counts up the number of days in the habit type's weekly plan that have passed
     * since the start date to determine how many times the user could have created a
     * habit event if they were following their plan perfectly
     *
     * @return      an int specifying the number of total opportunities
     *              to create a habit event
     */
    public int totalEventOpportunities() {
        int count = 0;
        Calendar start = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        start.setTime(start_date);
        while (start.before(today)) {
            switch (start.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    if (this.weekly_plan[0]) { ++count; }
                    break;
                case Calendar.TUESDAY:
                    if (this.weekly_plan[1]) { ++count; }
                    break;
                case Calendar.WEDNESDAY:
                    if (this.weekly_plan[2]) { ++count; }
                    break;
                case Calendar.THURSDAY:
                    if (this.weekly_plan[3]) { ++count; }
                    break;
                case Calendar.FRIDAY:
                    if (this.weekly_plan[4]) { ++count; }
                    break;
                case Calendar.SATURDAY:
                    if (this.weekly_plan[5]) { ++count; }
                    break;
                case Calendar.SUNDAY:
                    if (this.weekly_plan[6]) { ++count; }
                    break;
            }
            start.add(Calendar.DATE, 1);
        }
        return count;
    }

    /**
     * lets the Observers of this habit type know that it has been created, edited, or deleted
     */
    public void notifyViews() {
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * specifies the way habit types should be presented in views such as ListView
     *
     * @return      the title of the habit type (for now, until we decide what is necessary)
     */
    @Override
    public String toString() {
        return this.getTitle();
    }
}
