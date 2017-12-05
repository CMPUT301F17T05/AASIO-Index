package com.cmput301.t05.habilect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class represents a habit event. For each habit type, there can be 1 event per day. This event
 * describes what happened by a user supplied comment and photo. If the user wishes, they can
 * enable their location to track it on a map and so that their friends can view their events
 * as well
 *
 * @author ioltuszy
 * @author amwhitta
 * @see HabitType
 */

class HabitEvent implements Parcelable {
    public static final Creator<HabitEvent> CREATOR = new Creator<HabitEvent>() {
        @Override
        public HabitEvent createFromParcel(Parcel in) {
            return new HabitEvent(in);
        }

        @Override
        public HabitEvent[] newArray(int size) {
            return new HabitEvent[size];
        }
    };
    static int MAX_COMMENT_LENGTH = 20;
    private String comment;
    private String eventPicture;
    private Location location;
    private Date completionDate;
    private String habitType;
    private String userId;

    /**
     * Creates a new habit event
     *
     * @param comment        comment that goes with the event. Must be less than 20 characters
     * @param eventPicture   Base64 encoded string for the bitmap image of an event
     * @param location       Location object of the event
     * @param completionDate Date object of the event
     * @param habitType      The associated habitType for the event
     */
    public HabitEvent(String comment, String eventPicture, Location location, Date completionDate, String habitType, String userId) {
        this.setComment(comment);
        this.setEventPicture(eventPicture);
        this.setLocation(location);
        this.setCompletionDate(completionDate);
        this.setHabitType(habitType);
        this.userId = userId;
    }

    protected HabitEvent(Parcel in) {
        comment = in.readString();
        eventPicture = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        habitType = in.readString();
        userId = in.readString();
    }

    /**
     * Returns a string of the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment
     */
    public void setComment(String comment) {
        if (comment.length() > 20) {
            throw new IllegalArgumentException();
        } else {
            this.comment = comment;
        }
    }

    /**
     * Returns a string of the event picture
     */
    public String getEventPicture() {
        return eventPicture;
    }

    /**
     * Sets the event picture
     */
    public void setEventPicture(String eventPicture) {
        if (eventPicture != null) {
            this.eventPicture = eventPicture;
        }
    }

    /**
     * Returns a Location object of the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Returns a Date object of the completion date
     */
    public Date getCompletionDate() {
        return completionDate;
    }

    /**
     * Set the completion date
     */
    public void setCompletionDate(Date completion_date) {
        if (completion_date == null) {
            throw new IllegalArgumentException();
        } else {
            this.completionDate = completion_date;
        }
    }

    /**
     * Returns a string of formatted date
     */
    public String getCompletionDateString() {
        Locale locale = new Locale("English", "Canada");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE',' MMMM d',' yyyy", locale);
        return simpleDateFormat.format(this.completionDate);
    }

    /**
     * Gets the associated habit type
     */
    public String getHabitType() {
        return habitType;
    }

    /**
     * Sets the habit type
     */
    public void setHabitType(String habitType) {
        this.habitType = habitType;
    }

    /**
     * Gets the associated user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the associated event bitmap
     */
    public Bitmap getEventBitmap() {
        if (eventPicture != null) {
            byte[] decodedByteArray = Base64.decode(eventPicture, Base64.URL_SAFE | Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(comment);
        parcel.writeString(eventPicture);
        parcel.writeParcelable(location, i);
        parcel.writeString(habitType);
        parcel.writeString(userId);
    }
}
