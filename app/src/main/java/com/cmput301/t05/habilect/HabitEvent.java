package com.cmput301.t05.habilect;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This class represents a habit event. For each habit type, there can be 1 event per day. This event
 * describes what happened by a user supplied comment and photo. If the user wishes, they can
 * enable their location to track it on a map and so that their friends can view their events
 * as well
 * @see HabitType
 * @author ioltuszy
 * @author amwhitta
 */

class HabitEvent {
    static int MAX_COMMENT_LENGTH = 20;

    private String comment;
    private String eventPicture;
    private Location location;
    private Date completionDate;
    private String habitType;

    /**
     * Creates a new habit event
     * @param comment comment that goes with the event. Must be less than 20 characters
     * @param eventPicture Base64 encoded string for the bitmap image of an event
     * @param location Location object of the event
     * @param completionDate Date object of the event
     * @param habitType The associated habitType for the event
     */
    public HabitEvent(String comment, String eventPicture, Location location, Date completionDate, String habitType) {
        this.setComment(comment);
        this.setEventPicture(eventPicture);
        this.setLocation(location);
        this.setCompletionDate(completionDate);
        this.setHabitType(habitType);
    }

    // Getters
    public String getComment() {
        return comment;
    }
    public String getEventPicture() {
        return eventPicture;
    }
    public Location getLocation() {
        return location;
    }
    public Date getCompletionDate() {
        return completionDate;
    }
    public String getCompletionDateString() {
        Locale locale = new Locale("English", "Canada");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE',' MMMM d',' yyyy", locale);
        return simpleDateFormat.format(this.completionDate);
    }
    public String getHabitType() { return habitType; }

    // Setters
    public void setComment(String comment) {
        if (comment.length() > 20) {
            throw new IllegalArgumentException();
        } else {
            this.comment = comment;
        }
    }
    public void setEventPicture(String eventPicture) {
        if (eventPicture != null) {
            this.eventPicture = eventPicture;
        }
    }

    public void setHabitType(String habitType) {
        this.habitType = habitType;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    public void setCompletionDate(Date completion_date) {
        if (completion_date == null || completion_date.after(Calendar.getInstance().getTime())) {
            throw new IllegalArgumentException();
        } else {
            this.completionDate = completion_date;
        }
    }
}
