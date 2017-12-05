package com.cmput301.t05.habilect;

import android.test.ActivityInstrumentationTestCase2;

import java.util.Calendar;
import java.util.Date;

/**
 * @author ioltuszy
 * @author amwhitta
 */

public class HabitTypeTest extends ActivityInstrumentationTestCase2 {
    public HabitTypeTest() {
        super(com.cmput301.t05.habilect.HabitType.class);
    }

    /* Test Title
    - valid title
    - empty title
    - title longer than 20 characters
    - title starting with quotations
    - title of special characters
     */
    public void testTitle() {
        boolean[] plan = {true, true, true, true, true, true, true};
        HabitType habitType = new HabitType("title", "reason", new Date(), plan);
        String habitTypeTitle = habitType.getTitle();
        assertEquals("ERROR: valid title not accepted", "title", habitTypeTitle);

        try {
            habitType = new HabitType("", "reason", new Date(), plan);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "title");
        }

        try {
            habitType = new HabitType("This title is longer than 20 characters",
                    "reason", new Date(), plan);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "title");
        }

        habitType = new HabitType("\"title\"", "reason", new Date(), plan);
        habitTypeTitle = habitType.getTitle();
        assertEquals("ERROR: title with quotes not accepted", "\"title\"", habitTypeTitle);

        habitType = new HabitType("!@#\'()*$)<>.,~`", "reason", new Date(), plan);
        habitTypeTitle = habitType.getTitle();
        assertEquals("ERROR: title with special characters not accepted",
                "!@#\'()*$)<>.,~`", habitTypeTitle);
    }

    /* Test Reason
    - valid
    - empty reason
    - reason longer than 30 characters
    - reason starting with quotations
    - reason of special characters
     */
    public void testReason() {
        boolean[] plan = {true, true, true, true, true, true, true};
        HabitType habitType = new HabitType("title", "reason", new Date(), plan);
        String habitTypeReason = habitType.getReason();
        assertEquals("ERROR: valid reason not accepted", "reason", habitTypeReason);

        try {
            habitType = new HabitType("title", "", new Date(), plan);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "reason");
        }

        try {
            habitType = new HabitType("title",
                    "This reason is definitely longer than 30 characters", new Date(), plan);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "reason");
        }

        habitType = new HabitType("title", "\"reason\"", new Date(), plan);
        habitTypeReason = habitType.getReason();
        assertEquals("ERROR: reason with quotes not accepted", "\"reason\"", habitTypeReason);

        habitType = new HabitType("title", "!@#\'()*$)<>.,~`", new Date(), plan);
        habitTypeReason = habitType.getReason();
        assertEquals("ERROR: reason with special characters not accepted",
                "!@#\'()*$)<>.,~`", habitTypeReason);
    }

    /* Test Date
    - N/A: this unit test was for an old assumption.
    HabitType invalidHabitType = new HabitType("Nail Biting", "Aesthetics", new Date());
    boolean result = invalidHabitType.setStartDate(new Date(1995, 1, 1));
    assertFalse(result);
    - valid date in the future
    - valid date in the past
    // having a DatePicker would eliminate invalid input, yes?
    - invalid date in the future?
    - invalid date in the past?
     */
    public void testDate() {
        boolean[] plan = {true, true, true, true, true, true, true};
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 5);
        HabitType habitType = new HabitType("title", "reason", c.getTime(), plan);
        assertEquals("ERROR: valid future date not accepted",
                c.getTime(), habitType.getStartDate());

        c.setTime(new Date());
        c.add(Calendar.DATE, -5);
        habitType = new HabitType("title", "reason", c.getTime(), plan);
        assertEquals("ERROR: valid past date not accepted",
                c.getTime(), habitType.getStartDate());
    }

    /* Shared Test
    - shared has been set to false by default
    - changing shared to true
    - changing shared to false
     */
    public void testShared() {
        boolean[] plan = {true, true, true, true, true, true, true};
        HabitType habitType = new HabitType("title", "reason", new Date(), plan);
        assertFalse(habitType.getShared());

        habitType.setShared(true);
        assertTrue(habitType.getShared());

        habitType.setShared(false);
        assertFalse(habitType.getShared());
    }

    /* Weekly Plan Test
    - valid weekly plan
    - weekly plan has one day
    - weekly plan has all days
    - weekly plan is invalid (no days)
     */
    public void testWeeklyPlan() {
        boolean[] plan1 = {true, false, true, false, true, false, true};
        HabitType habitType = new HabitType("title", "reason", new Date(), plan1);
        assertEquals("Every Monday, Wednesday, Friday, Sunday", habitType.getWeeklyPlanString());

        boolean[] plan2 = {false, false, true, false, false, false, false};
        habitType = new HabitType("title", "reason", new Date(), plan2);
        assertEquals("Every Wednesday", habitType.getWeeklyPlanString());

        boolean[] plan3 = {true, true, true, true, true, true, true};
        habitType = new HabitType("title", "reason", new Date(), plan3);
        assertEquals("Every day", habitType.getWeeklyPlanString());

        boolean[] plan4 = {false, false, false, false, false, false, false};
        try {
            habitType = new HabitType("title", "reason", new Date(), plan4);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "plan");
        }
    }
}