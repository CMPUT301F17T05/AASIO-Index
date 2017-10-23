package com.cmput301.t05.habilect;

/**
 * @author ioltuszy
 * @author rarog
 */

class FollowRequest {
    private UserProfile prospectiveFollower;
    private HabitType habitToBeFollowed;

    public FollowRequest(UserProfile prospectiveFollower, HabitType habitToBeFollowed) {
        setProspectiveFollower(prospectiveFollower);
        setHabitToBeFollowed(habitToBeFollowed);
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