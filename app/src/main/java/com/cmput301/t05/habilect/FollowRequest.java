package com.cmput301.t05.habilect;

/**
 * Created by ioltuszy on 10/19/17.
 */

class FollowRequest {
    private UserProfile prospectiveFollower;
    private HabitType habitToBeFollowed;

    public FollowRequest(UserProfile prospectiveFollower, HabitType habitToBeFollowed) {
        this.prospectiveFollower = prospectiveFollower;
        this.habitToBeFollowed = habitToBeFollowed;
    }

    public UserProfile getProspectiveFollower() {
        return prospectiveFollower;
    }

    public HabitType getHabitToBeFollowed() {
        return habitToBeFollowed;
    }

    public void setProspectiveFollower(UserProfile prospectiveFollower) {
        this.prospectiveFollower = prospectiveFollower;
    }

    public void setHabitToBeFollowed(HabitType habitToBeFollowed) {
        this.habitToBeFollowed = habitToBeFollowed;
    }
}