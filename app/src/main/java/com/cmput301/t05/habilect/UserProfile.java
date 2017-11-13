package com.cmput301.t05.habilect;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author ioltuszy
 * @author amwhitta
 */

public class UserProfile {
    String displayName;
    Bitmap profilePicture;
    UUID identifier;
    List<HabitType> plans = new ArrayList<>();

    public UserProfile(String displayName, Bitmap profilePicture, List<HabitType> plans) {
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
    public List<HabitType> getPlans() {
        return plans;
    }
    public void addPlans(HabitType habitType){
        this.plans.add(habitType);
    }
    public void deletePlans(HabitType habitType){
        this.plans.remove(habitType);
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
    public void setPlans(List<HabitType> plans) {
        this.plans = plans;
    }

    static UUID LookupUUID() {
        // installation ID
        return null;
    }
}
