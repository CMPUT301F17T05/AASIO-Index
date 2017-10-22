package com.cmput301.t05.habilect;

import android.test.ActivityInstrumentationTestCase2;
import java.util.Calendar;
import java.util.Date;

/**
 * @author ioltuszy
 * @author amwhitta
 * @version 1.2 10/21/17
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
        HabitType habit_type = new HabitType("title", "reason", new Date());
        String habit_type_title = habit_type.getTitle();
        assertEquals("ERROR: valid title not accepted", "title", habit_type_title);

        try {
            habit_type = new HabitType("", "reason", new Date());
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        try {
            habit_type = new HabitType("This title is longer than 20 characters",
                    "reason", new Date());
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        habit_type = new HabitType("\"title\"", "reason", new Date());
        habit_type_title = habit_type.getTitle();
        assertEquals("ERROR: title with quotes not accepted", "\"title\"", habit_type_title);

        habit_type = new HabitType("!@#\'()*$)<>.,~`", "reason", new Date());
        habit_type_title = habit_type.getTitle();
        assertEquals("ERROR: title with special characters not accepted",
                "!@#\'()*$)<>.,~`", habit_type_title);
    }

    /* Test Reason
    - valid
    - empty reason
    - reason longer than 30 characters
    - reason starting with quotations
    - reason of special characters
    */
    public void testReason() {
        HabitType habit_type = new HabitType("title", "reason", new Date());
        String habit_type_reason = habit_type.getReason();
        assertEquals("ERROR: valid reason not accepted", "reason", habit_type_reason);

        try {
            habit_type = new HabitType("title", "", new Date());
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        try {
            habit_type = new HabitType("title",
                    "This reason is definitely longer than 30 characters", new Date());
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        habit_type = new HabitType("title", "\"reason\"", new Date());
        habit_type_reason = habit_type.getReason();
        assertEquals("ERROR: reason with quotes not accepted", "\"reason\"", habit_type_reason);

        habit_type = new HabitType("title", "!@#\'()*$)<>.,~`", new Date());
        habit_type_reason = habit_type.getReason();
        assertEquals("ERROR: reason with special characters not accepted",
                "!@#\'()*$)<>.,~`", habit_type_reason);
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
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 5);
        HabitType habit_type = new HabitType("title", "reason", c.getTime());
        assertEquals("ERROR: valid future date not accepted",
                c.getTime(), habit_type.getStartDate());

        c.setTime(new Date());
        c.add(Calendar.DATE, -5);
        habit_type = new HabitType("title", "reason", c.getTime());
        assertEquals("ERROR: valid past date not accepted",
                c.getTime(), habit_type.getStartDate());
    }

    /* Shared Test
    - shared has been set to false by default
    - changing shared to true
    - changing shared to false
    */
    public void testShared() {
        HabitType habit_type = new HabitType("title", "reason", new Date());
        assertFalse(habit_type.getShared());

        habit_type.setShared(true);
        assertTrue(habit_type.getShared());

        habit_type.setShared(false);
        assertFalse(habit_type.getShared());
    }
}