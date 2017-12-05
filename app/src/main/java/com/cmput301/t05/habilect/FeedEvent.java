package com.cmput301.t05.habilect;

/**
 * This class creates events comprised of a user account and habit event, these events
 * are meant to be put into the social feed so that users can see habit events from their friends
 * @see SocialActivity
 * @see SocialFeedAdapter
 * @see SocialFeedFragment
 * @author rarog
 */

public class FeedEvent {
    UserAccount user; // user account of the friend
    HabitEvent event; // habit event you want displayed

    FeedEvent(UserAccount user, HabitEvent event) {
        this.user = user;
        this.event = event;
    }

    /**
     * Sets the HabitEvent event
     */
    public void setEvent(HabitEvent event) {
        this.event = event;
    }

    /**
     * Sets the User user
     */
    public void setUser(UserAccount user) {
        this.user = user;
    }

    /**
     * Gets the habit event
     */
    public HabitEvent getEvent() {
        return event;
    }

    /**
     * Get the user
     */
    public UserAccount getUser() {
        return user;
    }
}
