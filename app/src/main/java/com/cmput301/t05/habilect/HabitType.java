package com.cmput301.t05.habilect;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

/**
 * This is the model for habit types. A habit type is a class describing a certain habit
 * that the user wants to regularly do.
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
    private ArrayList<HabitEvent> habitEvents = new ArrayList<>();
    private static int MAX_TITLE_LEN = 20;
    private static int MAX_REASON_LEN = 30;

    /**
     * The Constructor for HabitType
     * @param title                 a String with 0 < length <= 20
     * @param reason                a String with 0 < length <= 30
     * @param start_date            a Date specifying when the user started the habit type
     * @param weekly_plan           the days of the week that the user habitTypesList on doing the habit type
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
    public String getStartDateString() {
        Locale locale = new Locale("English", "Canada");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE',' MMMM d',' yyyy", locale);
        return simpleDateFormat.format(this.start_date);
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
    public Date getRecentHabitEvent() { return this.recent_habit_event; }
    public ArrayList<HabitEvent> getHabitEvents() { return this.habitEvents; }

    // SETTERS

    /**
     * sets the habit type title. An IllegalArgumentException is thrown
     * when the character length is out of range.
     *
     * @param title                 a String with 0 < length <= 20
     */
    public void setTitle(String title) {
        if (title.length() == 0 || title.length() > MAX_TITLE_LEN) {
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
        if (reason.length() == 0 || reason.length() > MAX_REASON_LEN) {
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

    public void editHabitEvents(String newTitle) {
        Iterator<HabitEvent> eventIterator = habitEvents.iterator();
        while(eventIterator.hasNext()) {
            HabitEvent event = eventIterator.next();
            event.setHabitType(newTitle);
        }
    }

    /**
     * specifies the way habit types should be presented in views such as ListView
     *
     * @return      the title of the habit type (for now, until we decide what is necessary)
     */
    @Override
    public String toString() {
        return this.getTitle() + '\n' + this.getWeeklyPlanString();
    }
}
