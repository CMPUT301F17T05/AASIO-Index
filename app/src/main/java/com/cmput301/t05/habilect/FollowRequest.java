package com.cmput301.t05.habilect;

/**
 * @author ioltuszy
 * @author rarog
 */

class FollowRequest {
    private UserProfile prospectiveFollower;
    private HabitType habitToBeFollowed;

    /**
     * Creates a new FollowRequest
     * @param prospectiveFollower user profile of follower
     * @param habitToBeFollowed habit type that is to be followed
     */
    public FollowRequest(UserProfile prospectiveFollower, HabitType habitToBeFollowed) {
        setProspectiveFollower(prospectiveFollower);
        setHabitToBeFollowed(habitToBeFollowed);
    }

    /**
     * Gets the UserProfile of the prospectiveFollower
     */
    public UserProfile getProspectiveFollower() {
        return prospectiveFollower;
    }

    /**
     * Gets the HabitType that is to be followed
     */
    public HabitType getHabitToBeFollowed() {
        return habitToBeFollowed;
    }

    /**
     * Sets the prospective follower
     */
    public void setProspectiveFollower(UserProfile prospectiveFollower) {
        this.prospectiveFollower = prospectiveFollower;
    }

    /**
     * Sets the habit that is to be followed
     */
    public void setHabitToBeFollowed(HabitType habitToBeFollowed) {
        this.habitToBeFollowed = habitToBeFollowed;
    }
}