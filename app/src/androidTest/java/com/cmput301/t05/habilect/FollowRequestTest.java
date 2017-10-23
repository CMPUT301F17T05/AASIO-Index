package com.cmput301.t05.habilect;

import android.test.ActivityInstrumentationTestCase2;

import java.util.Date;

/**
 * @author rarog
 * @version 1.0 22/10/2017
 */

public class FollowRequestTest extends ActivityInstrumentationTestCase2 {
    public FollowRequestTest() {
        super(com.cmput301.t05.habilect.FollowRequestTest.class);
    }

    /* Test prospectiveFollower
    - user profile of follower must not be null
     */
    public void testProspectiveFollower() {
        UserProfile profile = new UserProfile("TestProfile", null, null);
        HabitType habit = new HabitType("Habit", "Reason", new Date());
        FollowRequest request = new FollowRequest(profile, habit);

        assertEquals("ERROR: valid prospectiveFollower not set in request",
                profile, request.getProspectiveFollower());

        try {
            FollowRequest nullRequest = new FollowRequest(null, habit);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

    }

    /* Test habitToBeFollowed
    - habit to be followed must not be null
     */
    public void testHabitToBeFollowed() {
        UserProfile profile = new UserProfile("TestProfile", null, null);
        HabitType habit = new HabitType("Habit", "Reason", new Date());
        FollowRequest request = new FollowRequest(profile, habit);

        assertEquals("ERROR: valid habitToBeFollowed not set in request",
                habit, request.getHabitToBeFollowed());

        try {
            FollowRequest nullRequest = new FollowRequest(profile, null);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }
}
