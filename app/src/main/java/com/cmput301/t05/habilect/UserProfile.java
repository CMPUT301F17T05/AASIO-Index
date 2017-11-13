package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;

import java.util.List;
import java.util.UUID;

/**
 * @author ioltuszy
 * @author amwhitta
 */

public class UserProfile {
    public static final String HABILECT_USER_INFO = "HABILECT_USER_INFO";

    public static final String HABILECT_USER_INSTALL_ID = "HABILECT_USER_INSTALL_ID";
    public static final String HABILECT_USER_DISPLAY_NAME = "HABILECT_USER_DISPLAY_NAME";
    public static final String HABILECT_USER_PICTURE = "HABILECT_USER_PICTURE";

    Context context;

    String identifier;
    String displayName;
    Bitmap profilePicture;

    boolean isNewUser = false;

    public UserProfile(Context context) {
        this.context = context;

        this.setIdentifier((String)this.Lookup(context, HABILECT_USER_INSTALL_ID));
        this.setDisplayName((String)this.Lookup(context, HABILECT_USER_DISPLAY_NAME));
        this.setProfilePicture((String)this.Lookup(context, HABILECT_USER_PICTURE));

        if (isNewUser) {
            WebService.AddUserProfileTask addUserProfileTask = new WebService.AddUserProfileTask();
            addUserProfileTask.execute(this);
        }
    }

    public void setContext() {
        context = null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfilePicture(String encodedProfilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    static Object Lookup(Context context, String preferenceKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HABILECT_USER_INFO, Context.MODE_PRIVATE);
        String preference = sharedPreferences.getString(preferenceKey, null);
        if (preference == null) {
            String newPreference = "";
            SharedPreferences.Editor editor = sharedPreferences.edit();
            switch (preferenceKey) {
                default:
                    return null;
                case HABILECT_USER_INSTALL_ID:
                    newPreference = Base64.encodeToString(UUID.randomUUID().toString().getBytes(), Base64.DEFAULT);
                    editor.putString(preferenceKey, newPreference);
                    editor.commit();
                    return UUID.randomUUID().toString();
                case HABILECT_USER_DISPLAY_NAME:
                    newPreference = Base64.encodeToString("First Name".getBytes(), Base64.DEFAULT);
                    editor.putString(preferenceKey, newPreference);
                    editor.commit();
                    return "First Name";
                case HABILECT_USER_PICTURE:
                    newPreference = Base64.encodeToString("null".getBytes(), Base64.DEFAULT);
                    editor.putString(preferenceKey, newPreference);
                    editor.commit();
                    return "null";
            }
        }
        return new String(Base64.decode(preference, Base64.DEFAULT));
    }
}