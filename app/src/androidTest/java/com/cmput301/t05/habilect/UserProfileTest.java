package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import java.io.InputStream;
import java.util.Arrays;

/**
 * @author rarog
 * @version 1.0 22/10/17
 */

public class UserProfileTest extends ActivityInstrumentationTestCase2  {
    private String TEST1_FILENAME = "test1.1.jpg";            // valid picture, 40000 bytes
    private String TEST2_FILENAME = "test2.1.jpg";            // invalid picture, 31961088 bytes

    public UserProfileTest() {
        super(com.cmput301.t05.habilect.UserProfile.class);
    }

    /* Test displayName
    - valid name
    - name longer than 256 characters
    - names with numbers, '_" or '-' are ok
     */
    public void testDisplayName() {
        UserProfile profile = new UserProfile("UserName", null, null);
        assertEquals("ERROR: valid display name was rejected", "UserName", profile.getDisplayName());

        profile.setDisplayName("Has accepted characters _ - _- 1 1234");
        assertEquals("ERROR: valid display name was rejected", "Has accepted characters _ - _- 1 1234", profile.getDisplayName());
        assertEquals("ERROR: valid display name was rejected", "Has accepted characters _ - _- 1 1234", profile.getDisplayName());

        try {
            profile.setDisplayName("");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // makes a string of 257 length characters
        char[] charArray = new char[257];
        Arrays.fill(charArray, ' ');

        try {
            profile.setDisplayName(new String(charArray));
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /* Test displayImage
    - valid image
    - must be shorter than 65536 bytes
     */
    public void testDisplayImage() {
        Bitmap testPicture1 = getBitmapFromTestAssets(TEST1_FILENAME);
        Bitmap testPicture2 = getBitmapFromTestAssets(TEST2_FILENAME);

        UserProfile profile = new UserProfile("ProfileName", null, null);

        try {
            profile.setProfilePicture(testPicture1);
            Bitmap profilePicture = profile.getProfilePicture();
            assertEquals("ERROR: pictures not equal", testPicture1, profilePicture);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
            Log.e("ERROR:", "valid picture not accepted");
        }

        try {
            profile.setProfilePicture(testPicture2);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        profile.setProfilePicture(null);
        Bitmap profilePicture = profile.getProfilePicture();
        assertEquals("ERROR: no picture not accepted", null, profilePicture);

    }

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
}
