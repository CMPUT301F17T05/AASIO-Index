package com.cmput301.t05.habilect;

import android.graphics.Bitmap;
import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author ioltuszy
 * @author amwhitta
 */

class HabitEvent {
    static int MAX_COMMENT_LENGTH = 20;

    private String comment;
    private Bitmap eventPicture;
    private Location location;
    private Date completionDate;
    private String habitType;

    /**
     * Creates a new habit event
     * @param comment comment that goes with the event. Must be less than 20 characters
     * @param eventPicture bitmap image of the event
     * @param location Location object of the event
     * @param completionDate Date object of the event
     * @param habitType The associated habitType for the event
     */
    public HabitEvent(String comment, Bitmap eventPicture, Location location, Date completionDate, String habitType) {
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
    public Bitmap getEventPicture() {
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
    public void setEventPicture(Bitmap eventPicture) {
        if (eventPicture != null) {
            if (eventPicture.getByteCount() >= 65536) {
                eventPicture = Bitmap.createScaledBitmap(eventPicture, 127, 127, false);
            }
            if(eventPicture.getByteCount() >= 65536) {
                throw new IllegalArgumentException();
            }
            this.eventPicture = eventPicture;
        } else {
            this.eventPicture = null;
        }
    }

    public void setHabitType(String habitType) {
        this.habitType = habitType;
    }

    //TODO: error handling for setLocation?
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
