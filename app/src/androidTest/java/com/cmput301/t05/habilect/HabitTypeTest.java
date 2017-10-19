package com.cmput301.t05.habilect;


import android.test.ActivityInstrumentationTestCase2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ioltuszy on 10/19/17.
 */

public class HabitTypeTest extends ActivityInstrumentationTestCase2 {
    public HabitTypeTest() {
        super(HabitType.class);
    }

    public void testValidation() {
        // Title Test
        HabitType habitType = new HabitType("Nail Biting", "Aesthetics", new Date());

        String habitTypeTitle = habitType.getTitle();
        assertEquals(habitTypeTitle, "Nail Biting");

        /*
        // Date Test
        HabitType invalidHabitType = new HabitType("Nail Biting", "Aesthetics", new Date());
        boolean result = invalidHabitType.setStartDate(new Date(1995, 1, 1));
        assertFalse(result);
        */
    }
}
