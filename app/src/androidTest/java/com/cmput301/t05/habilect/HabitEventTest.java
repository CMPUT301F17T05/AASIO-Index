package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * @author rarog
 * @see <a href="https://stackoverflow.com/questions/37527603/how-to-use-files-in-instrumented-unit-tests">StackOverflow source for getBitmapFromTestAssets</a>
 */

public class HabitEventTest extends ActivityInstrumentationTestCase2 {

    private String test1Filename = "test1.1.jpg";            // valid picture, 40000 bytes
    private String test2Filename = "test2.1.jpg";            // invalid picture, 31961088 bytes
    private double uniLatitude = 53.5232;
    private double uniLongitude = 113.5263;

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

        HabitEvent habitEvent = new HabitEvent("comment", null, null, new Date(), "title", "userId");
        String habitEventComment = habitEvent.getComment();
        assertEquals("ERROR: valid comment not accepted", "comment", habitEventComment);

        habitEvent = new HabitEvent("", null, null, new Date(), "title", "userId");
        habitEventComment = habitEvent.getComment();
        assertEquals("ERROR: valid empty comment not accepted", "", habitEventComment);

        try {
            habitEvent = new HabitEvent("thiscommentislongerthan20characters", null,
                    null, new Date(), "title", "userId");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        habitEvent = new HabitEvent("\"comment\"", null, null, new Date(), "title", "userId");
        habitEventComment = habitEvent.getComment();
        assertEquals("ERROR: valid comment with quotes not accepted", "\"comment\"", habitEventComment);

        habitEvent = new HabitEvent("@#'()*$)<>.,~`", null, null, new Date(), "title", "userId");
        habitEventComment = habitEvent.getComment();
        assertEquals("ERROR: valid comment with special characters not accepted", "@#'()*$)<>.,~`",
                habitEventComment);
    }

    /* Test Event Picture
    - valid picture
    - picture larger than 65536 bytes (65.536 KB)
    - no picture
     */
    public void testEventPicture() {
        Bitmap testPic1 = getBitmapFromTestAssets(test1Filename);
        String sTestPic1 = bitMapToString(testPic1);
        Bitmap testPic2 = getBitmapFromTestAssets(test2Filename);
        String sTestPic2 = bitMapToString(testPic2);

        try {
            HabitEvent habitEvent = new HabitEvent("comment", sTestPic1, null, new Date(), "title", "userId");
            String habitEventPicture = habitEvent.getEventPicture();
            assertEquals("ERROR: pictures not equal", sTestPic1, habitEventPicture);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
            Log.e("ERROR:", "valid picture not accepted");
        }

        try {
            HabitEvent habitEvent = new HabitEvent("comment", sTestPic2, null, new Date(), "title", "userId");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        HabitEvent habitEvent = new HabitEvent("comment", null, null, new Date(), "title", "userId");
        String habitEventEventPicture = habitEvent.getEventPicture();
        assertEquals("ERROR: no picture not accepted", null, habitEventEventPicture);
    }

    /* Test Location
    - valid location
    - no location
     */
    public void testLocation() {
        Location location = generateLocation(uniLatitude, uniLongitude);
        HabitEvent habitEvent = new HabitEvent("comment", null, location, new Date(), "title", "userId");
        assertEquals("ERROR: valid location not accepted", location, habitEvent.getLocation());

        habitEvent = new HabitEvent("comment", null, null, new Date(), "title", "userId");
        assertEquals("ERROR: no location not accepted", null, habitEvent.getLocation());
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
        HabitEvent habitEvent = new HabitEvent("comment", null, null, c.getTime(), "title", "userId");
        assertEquals("ERROR: valid date in the past not accepted", c.getTime(),
                habitEvent.getCompletionDate());

        habitEvent = new HabitEvent("comment", null, null, new Date(), "title", "userId");
        assertEquals("ERROR: today's date not accepted", new Date(),
                habitEvent.getCompletionDate());

        try {
            c.setTime(new Date());
            c.add(Calendar.DATE, 5);
            habitEvent = new HabitEvent("comment", null, null, c.getTime(), "title", "userId");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
            Log.e("ERROR", "Completion date cannot be in the future");
        }

        try {
            habitEvent = new HabitEvent("comment", null, null, null, "title", "userId");
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
            InputStream testInput = assetManager.open(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(testInput);
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
        Location uniLocation = new Location("provider");
        uniLocation.setLatitude(latitude);
        uniLocation.setLongitude(longitude);
        return uniLocation;
    }

    public String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
