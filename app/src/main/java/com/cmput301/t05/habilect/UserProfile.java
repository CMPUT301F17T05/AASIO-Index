package com.cmput301.t05.habilect;

import android.graphics.Bitmap;

import java.util.List;
import java.util.UUID;

/**
 * Created by ioltuszy on 10/19/17.
 */

public class UserProfile {
    String displayName;
    Bitmap profilePicture;
    UUID identifier;
    List<HabitPlan> plans;

    public UserProfile(String displayName, Bitmap profilePicture, List<HabitPlan> plans) {
        this.setIdentifier(this.LookupUUID());
        this.profilePicture = profilePicture;
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }
    public Bitmap getProfilePicture() {
        return profilePicture;
    }
    public UUID getIdentifier() {
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
    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
    }
    public void setPlans(List<HabitPlan> plans) {
        this.plans = plans;
    }

    static UUID LookupUUID() {
        // installation ID
        return null;
    }
}
