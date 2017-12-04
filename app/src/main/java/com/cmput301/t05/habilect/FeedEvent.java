package com.cmput301.t05.habilect;

/**
 * Created by Oliver on 02/12/2017.
 */

public class FeedEvent {
    UserAccount user;
    HabitEvent event;

    FeedEvent(UserAccount user, HabitEvent event) {
        this.user = user;
    }

    public void setEvent(HabitEvent event) {
        this.event = event;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public HabitEvent getEvent() {
        return event;
    }

    public UserAccount getUser() {
        return user;
    }
}
