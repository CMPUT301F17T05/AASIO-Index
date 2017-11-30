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
    private Bitmap event_picture;
    private Location location;
    private Date completion_date;
    private String habitType;

    /**
     * Creates a new habit event
     * @param comment comment that goes with the event. Must be less than 20 characters
     * @param event_picture bitmap image of the event
     * @param location Location object of the event
     * @param completion_date Date object of the event
     * @param habitType The associated habitType for the event
     */
    public HabitEvent(String comment, Bitmap event_picture, Location location, Date completion_date, String habitType) {
        this.setComment(comment);
        this.setEventPicture(event_picture);
        this.setLocation(location);
        this.setCompletionDate(completion_date);
        this.setHabitType(habitType);
    }

    // Getters
    public String getComment() {
        return comment;
    }
    public Bitmap getEventPicture() {
        return event_picture;
    }
    public Location getLocation() {
        return location;
    }
    public Date getCompletionDate() {
        return completion_date;
    }
    public String getCompletionDateString() {
        Locale locale = new Locale("English", "Canada");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE',' MMMM d',' yyyy", locale);
        return simpleDateFormat.format(this.completion_date);
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
    public void setEventPicture(Bitmap event_picture) {
        if (event_picture != null) {
            if (event_picture.getByteCount() >= 65536) {
                event_picture = Bitmap.createScaledBitmap(event_picture, 127, 127, false);
            }
            if(event_picture.getByteCount() >= 65536) {
                throw new IllegalArgumentException();
            }
            this.event_picture = event_picture;
        } else {
            this.event_picture = null;
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
            this.completion_date = completion_date;
        }
    }
}
