package com.cmput301.t05.habilect;

/**
 * Created by Oliver on 02/12/2017.
 */

public class FeedEvent {
    UserAccount profile;
    HabitEvent event;

    FeedEvent(UserAccount profile, HabitEvent event) {
        this.event = event;
        this.profile = profile;
    }

    public void setEvent(HabitEvent event) {
        this.event = event;
    }

    public void setProfile(UserAccount profile) {
        this.profile = profile;
    }

    public HabitEvent getEvent() {
        return event;
    }

    public UserAccount getProfile() {
        return profile;
    }
}
