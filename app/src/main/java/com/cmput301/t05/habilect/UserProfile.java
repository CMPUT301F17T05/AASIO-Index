package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class that tracks everything that has to do with the user profile.
 *
 * @author ioltuszy
 * @author amwhitta
 */

public class UserProfile {
    public static final String HABILECT_USER_INFO = "HABILECT_USER_INFO";
    public static final String HABILECT_USER_INSTALL_ID = "HABILECT_USER_INSTALL_ID";
    public static final String HABILECT_USER_DISPLAY_NAME = "HABILECT_USER_DISPLAY_NAME";
    public static final String HABILECT_USER_PICTURE = "HABILECT_USER_PICTURE";
    public static final String HABILECT_USER_TREE_GROWTH = "HABILECT_USER_TREE_GROWTH";
    public static final String HABILECT_USER_PREVIOUS_NUTRIENT_LEVEL_TIER_RANK_UP = "HABILECT_USER_PREVIOUS_NUTRIENT_LEVEL_TIER_RANK_UP";

    Context context;

    String identifier;
    String displayName;
    String profilePicture;
    TreeGrowth treeGrowth = new TreeGrowth();
    List<HabitType> habitTypesList = new ArrayList<>();

    boolean isNewUser = false;

    public UserProfile(Context context) {
        this.context = context;

        this.setIdentifier(this.Lookup(context, HABILECT_USER_INSTALL_ID));
        this.setDisplayName(this.Lookup(context, HABILECT_USER_DISPLAY_NAME));
        this.setProfilePicture(this.Lookup(context, HABILECT_USER_PICTURE));
        this.setTreeGrowth(this.Lookup(context, HABILECT_USER_TREE_GROWTH));
        this.setPreviousNutrientLevelTierRankUp(this.Lookup(context, HABILECT_USER_PREVIOUS_NUTRIENT_LEVEL_TIER_RANK_UP));

    }

    /**
     * Checks locally if there are values set for the specified preferenceKey.
     *
     * @param context       the current application context
     * @param preferenceKey the key to search for locally
     * @return the value if found or a default value
     */
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
                case HABILECT_USER_TREE_GROWTH:
                    newPreference = "0";
                    editor.putString(preferenceKey, newPreference);
                    editor.putString(HABILECT_USER_PREVIOUS_NUTRIENT_LEVEL_TIER_RANK_UP, newPreference);
                    editor.commit();
                    break;
                case HABILECT_USER_PREVIOUS_NUTRIENT_LEVEL_TIER_RANK_UP:
                    newPreference = "0";
                    editor.putString(preferenceKey, newPreference);
                    editor.commit();
                    break;
            }
            return newPreference;
        }
        return preference;
    }

    public void setContext() {
        context = null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Bitmap getProfilePicture() {
        if (profilePicture == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(Base64.decode(profilePicture, Base64.DEFAULT), 0, Base64.decode(profilePicture, Base64.DEFAULT).length);
    }

    public void setProfilePicture(String encodedProfilePicture) {
        this.profilePicture = encodedProfilePicture;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<HabitType> getHabitTypesList() {
        return habitTypesList;
    }

    public void setHabitTypesList(List<HabitType> habitTypesList) {
        this.habitTypesList = habitTypesList;
    }

    public void addPlans(HabitType habitType) {
        this.habitTypesList.add(habitType);
    }

    public void deletePlans(HabitType habitType) {
        this.habitTypesList.remove(habitType);
    }

    public void setTreeGrowth(String nutrientLevel) {
        this.treeGrowth.setNutrientLevel(Integer.parseInt(nutrientLevel));
    }

    public void setPreviousNutrientLevelTierRankUp(String level) {
        this.treeGrowth.setPreviousNutrientLevelTierRankUp(Integer.parseInt(level));
    }
}