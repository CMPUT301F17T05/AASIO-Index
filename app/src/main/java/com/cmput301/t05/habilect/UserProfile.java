package com.cmput301.t05.habilect;

import android.media.Image;

import java.util.List;
import java.util.UUID;

/**
 * Created by ioltuszy on 10/19/17.
 */

public class UserProfile {
    String displayName;
    Image profilePicture;
    UUID identifier;
    List<HabitPlan> plans;

    public String getDisplayName() {
        return displayName;
    }
    public Image getProfilePicture() {
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
    public void setProfilePicture(Image profilePicture) {
        this.profilePicture = profilePicture;
    }
    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
    }
    public void setPlans(List<HabitPlan> plans) {
        this.plans = plans;
    }

    public UserProfile(String displayName, Image profilePicture, List<HabitPlan> plans) {
        this.setIdentifier(this.LookupUUID());
    }

    static UUID LookupUUID() {
        // installation ID
        return null;
    }
}
