package com.cmput301.t05.habilect;

/**
 * Created by Oliver on 02/12/2017.
 */

public class FeedEvent {
    UserProfile profile;
    HabitEvent event;

    FeedEvent(UserProfile profile, HabitEvent event) {
        this.event = event;
        this.profile = profile;
    }

    public void setEvent(HabitEvent event) {
        this.event = event;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public HabitEvent getEvent() {
        return event;
    }

    public UserProfile getProfile() {
        return profile;
    }
}
