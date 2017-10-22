package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * @author amwhitta
 * @version 1.1
 * @see <a href="https://stackoverflow.com/questions/37527603/how-to-use-files-in-instrumented-unit-tests">StackOverflow source for getBitmapFromTestAssets</a>
 */

public class HabitEventTest extends ActivityInstrumentationTestCase2 {

    private String TEST1_FILENAME = "test1.1.jpg";            // valid picture, 40000 bytes
    private String TEST2_FILENAME = "test2.1.jpg";            // invalid picture, 31961088 bytes
    private double UNI_LATITUDE = 53.5232;
    private double UNI_LONGITUDE = 113.5263;

    public HabitEventTest() {
        super(com.cmput301.t05.habilect.HabitEvent.class);
    }

    /* Test Comment
    - valid comment
    - valid empty comment
    - comment longer than 20 characters
    - comment starts with quotations
    - comment of special characters
     */
    public void testComment() {

        HabitEvent habit_event = new HabitEvent("comment", null, null, new Date());
        String habit_event_comment = habit_event.getComment();
        assertEquals("ERROR: valid comment not accepted", "comment", habit_event_comment);

        habit_event = new HabitEvent("", null, null, new Date());
        habit_event_comment = habit_event.getComment();
        assertEquals("ERROR: valid empty comment not accepted", "", habit_event_comment);

        try {
            habit_event = new HabitEvent("thiscommentislongerthan20characters", null,
                    null, new Date());
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        habit_event = new HabitEvent("\"comment\"", null, null, new Date());
        habit_event_comment = habit_event.getComment();
        assertEquals("ERROR: valid comment with quotes not accepted", "\"comment\"", habit_event_comment);

        habit_event = new HabitEvent("@#'()*$)<>.,~`", null, null, new Date());
        habit_event_comment = habit_event.getComment();
        assertEquals("ERROR: valid comment with special characters not accepted", "@#'()*$)<>.,~`",
                habit_event_comment);
    }

    /* Test Event Picture
    - valid picture
    - picture larger than 65536 bytes (65.536 KB)
    - no picture
     */
    public void testEventPicture() {
        Bitmap test_pic_1 = getBitmapFromTestAssets(TEST1_FILENAME);
        Bitmap test_pic_2 = getBitmapFromTestAssets(TEST2_FILENAME);

        try {
            HabitEvent habit_event = new HabitEvent("comment", test_pic_1, null, new Date());
            Bitmap habit_event_picture = habit_event.getEventPicture();
            assertEquals("ERROR: pictures not equal", test_pic_1, habit_event_picture);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
            Log.e("ERROR:", "valid picture not accepted");
        }

        try {
            HabitEvent habit_event = new HabitEvent("comment", test_pic_2, null, new Date());
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        HabitEvent habit_event = new HabitEvent("comment", null, null, new Date());
        Bitmap habit_event_picture = habit_event.getEventPicture();
        assertEquals("ERROR: no picture not accepted", null, habit_event_picture);
    }

    /* Test Location
    - valid location
    - no location
     */
    public void testLocation() {
        Location location = generateLocation(UNI_LATITUDE, UNI_LONGITUDE);
        HabitEvent habit_event = new HabitEvent("comment", null, location, new Date());
        assertEquals("ERROR: valid location not accepted", location, habit_event.getLocation());

        habit_event = new HabitEvent("comment", null, null, new Date());
        assertEquals("ERROR: no location not accepted", null, habit_event.getLocation());
    }

    /* Test Completion Date
    - valid completion date in the past
    - valid completion date (today's date)
    - invalid completion date in the future
    - invalid empty date
     */
    public void testCompletionDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -5);
        HabitEvent habit_event = new HabitEvent("comment", null, null, c.getTime());
        assertEquals("ERROR: valid date in the past not accepted", c.getTime(),
                habit_event.getCompletionDate());

        habit_event = new HabitEvent("comment", null, null, new Date());
        assertEquals("ERROR: today's date not accepted", new Date(),
                habit_event.getCompletionDate());

        try {
            c.setTime(new Date());
            c.add(Calendar.DATE, 5);
            habit_event = new HabitEvent("comment", null, null, c.getTime());
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
            Log.e("ERROR", "Completion date cannot be in the future");
        }

        try {
            habit_event = new HabitEvent("comment", null, null, null);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
            Log.e("ERROR", "Completion date cannot be null");
        }
    }
    /*
    read image files that are in the src/androidTest/assets directory
     */
    private Bitmap getBitmapFromTestAssets(String filename) {
        try {
            Context testContext = InstrumentationRegistry.getInstrumentation().getContext();
            AssetManager assetManager = testContext.getAssets();
            InputStream test_input = assetManager.open(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(test_input);
            return bitmap;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    /*
    generate a valid test location (the University of Alberta)
     */
    private Location generateLocation(double latitude, double longitude) {
        Location uni_location = new Location("provider");
        uni_location.setLatitude(latitude);
        uni_location.setLongitude(longitude);
        return uni_location;
    }
}
