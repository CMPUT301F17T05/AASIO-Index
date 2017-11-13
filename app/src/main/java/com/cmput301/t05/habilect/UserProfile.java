package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
    String profilePicture;
    List<HabitType> plans = new ArrayList<>();

    boolean isNewUser = false;

    public UserProfile(Context context) {
        this.context = context;

        this.setIdentifier(this.Lookup(context, HABILECT_USER_INSTALL_ID));
        this.setDisplayName(this.Lookup(context, HABILECT_USER_DISPLAY_NAME));
        this.setProfilePicture(this.Lookup(context, HABILECT_USER_PICTURE));

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
        if (profilePicture==null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(Base64.decode(profilePicture, Base64.DEFAULT), 0, Base64.decode(profilePicture, Base64.DEFAULT).length);
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<HabitType> getPlans() {
        return plans;
    }

    public void addPlans(HabitType habitType){
        this.plans.add(habitType);
    }

    public void deletePlans(HabitType habitType){
        this.plans.remove(habitType);
    }

    public void setPlans(List<HabitType> plans) {
        this.plans = plans;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfilePicture(String encodedProfilePicture) { this.profilePicture = encodedProfilePicture; }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    static String Lookup(Context context, String preferenceKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HABILECT_USER_INFO, Context.MODE_PRIVATE);
        String preference = sharedPreferences.getString(preferenceKey, null);
        if (preference == null) {
            String newPreference;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            switch (preferenceKey) {
                default:
                    return null;
                case HABILECT_USER_INSTALL_ID:
                    newPreference = UUID.randomUUID().toString();
                    editor.putString(preferenceKey, newPreference);
                    editor.commit();
                    break;
                case HABILECT_USER_DISPLAY_NAME:
                    newPreference = "First Name";
                    editor.putString(preferenceKey, newPreference);
                    editor.commit();
                    break;
                case HABILECT_USER_PICTURE:
                    newPreference = null;
                    editor.putString(preferenceKey, newPreference);
                    editor.commit();
                    break;
            }
            return newPreference;
        }
        return preference;
    }
}