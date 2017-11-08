package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.List;
import java.util.UUID;

/**
 * @author ioltuszy
 * @author amwhitta
 */

public class UserProfile {
    static final String HABILECT_INSTALL_ID = "HABILECT_INSTALL_ID";

    Context context;

    String identifier;
    String displayName;
    Bitmap profilePicture;
    List<HabitPlan> plans;

    public UserProfile(Context context, String displayName, Bitmap profilePicture, List<HabitPlan> plans) {
        this.context = context;

        this.setIdentifier(this.LookupID(context));
        this.profilePicture = profilePicture;
        this.displayName = displayName;
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

    public List<HabitPlan> getPlans() {
        return plans;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPlans(List<HabitPlan> plans) {
        this.plans = plans;
    }

    static String LookupID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HABILECT_INSTALL_ID, Context.MODE_PRIVATE);
        String identifier = sharedPreferences.getString(HABILECT_INSTALL_ID, null);
        if (identifier == null) {
            String newIdentifier = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(HABILECT_INSTALL_ID, newIdentifier);
            return newIdentifier;
        }
        return identifier;
    }


}
