package com.cmput301.t05.habilect;

import android.test.ActivityInstrumentationTestCase2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sychan1
 */

public class HabitPlanTest extends ActivityInstrumentationTestCase2 {

    public HabitPlanTest() {
        super(com.cmput301.t05.habilect.HabitPlan.class);
    }

    /* Test habitType
    - cannot be null
     */
    public void testHabitType() {
        HabitType habitType = new HabitType("title", "reason", new Date());
        HabitPlan habitPlan = new HabitPlan(habitType, new int[]{1, 2, 3, 0, 0, 0, 0},
                new boolean[]{true, true, true, false, false, false, false}, new ArrayList<HabitEvent>());

        assertEquals("ERROR: habitType cannot be null!", habitType, habitPlan.getHabitType());

        try {
            habitPlan.setHabitType(null);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /* Test targetFrequency
    - must be length of 7 (days in a week)
     */
    public void testtargetFrequency() {
        int[] targetFrequency = new int[]{1, 2, 3, 0, 0, 0, 0};
        HabitPlan habitPlan = new HabitPlan(new HabitType("title", "reason", new Date()), targetFrequency,
                new boolean[]{true, true, true, false, false, false, false}, new ArrayList<HabitEvent>());

        assertEquals("ERROR: targetFrequency must be of length 7!", targetFrequency, habitPlan.getTargetFrequency());

        try {
            habitPlan.setTargetFrequency(new int[]{1, 2, 3});
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /* Test targetDays
    - must be length of 7 (days in a week)
     */
    public void testtargetDays() {
        boolean[] targetDays = new boolean[]{true, true, true, false, false, false, false};
        HabitPlan habitPlan = new HabitPlan(new HabitType("title", "reason", new Date()), new int[]{1, 2, 3, 0, 0, 0, 0},
                targetDays, new ArrayList<HabitEvent>());

        assertEquals("ERROR: targetDays must be of length 7!", targetDays, habitPlan.getTargetDays());

        try {
            habitPlan.setTargetDays(new boolean[]{true, true, false});
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }
}
