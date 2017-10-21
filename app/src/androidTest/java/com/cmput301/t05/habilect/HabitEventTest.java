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
import java.util.Date;

/**
 * @author amwhitta
 * @see <a href="https://stackoverflow.com/questions/37527603/how-to-use-files-in-instrumented-unit-tests">StackOverflow source for getBitmapFromTestAssets</a>
 */

public class HabitEventTest extends ActivityInstrumentationTestCase2 {

    private String TEST1_FILENAME = "test1.1.jpg";            // valid picture, 40000 bytes
    private String TEST2_FILENAME = "test2.1.jpg";            // invalid picture, 31961088 bytes

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
        Location uni_location = generateLocation();

        try {
            HabitEvent habit_event = new HabitEvent("comment", test_pic_1, uni_location, new Date());
            Bitmap habit_event_picture = habit_event.getEventPicture();
            assertEquals("ERROR: pictures not equal", test_pic_1, habit_event_picture);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
            Log.e("ERROR:", "valid picture not accepted");
        }

        try {
            HabitEvent habit_event = new HabitEvent("comment", test_pic_2, uni_location, new Date());
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        HabitEvent habit_event = new HabitEvent("comment", null, uni_location, new Date());
        Bitmap habit_event_picture = habit_event.getEventPicture();
        assertEquals("ERROR: no picture not accepted", null, habit_event_picture);
    }

    //TODO: unit tests for location
    //TODO: unit tests for date (if necessary)

    /*
    read image files that are in the src/androidTest/assets directory
     */
    public Bitmap getBitmapFromTestAssets(String filename) {
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
    public Location generateLocation() {
        Location uni_location = new Location("provider");
        uni_location.setLatitude(53.5232);
        uni_location.setLongitude(113.5263);
        return uni_location;
    }
}
