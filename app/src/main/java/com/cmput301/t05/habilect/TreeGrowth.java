package com.cmput301.t05.habilect;

import java.util.Calendar;

/**
 * Class that is responsible for tree growth to track nutrient level and also the last rank up tier.
 *
 * Created by StevenC on 2017-11-23.
 */

public class TreeGrowth {
    int nutrientLevel;
    int previousNutrientLevelTierRankUp;
    int lastCheckDateForPreviousDaysIncompleteHabitTypes;
    public TreeGrowth() {
        this.nutrientLevel = 0;
        this.previousNutrientLevelTierRankUp = 0;
        this.lastCheckDateForPreviousDaysIncompleteHabitTypes = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    public int getNutrientLevel() {
        return nutrientLevel;
    }

    public void setNutrientLevel(int nutrientLevel) {
        this.nutrientLevel = nutrientLevel;
        if (this.nutrientLevel < 0) {
            this.nutrientLevel = 0;
        }
    }

    public int getLastCheckDateForPreviousDaysIncompleteHabitTypes(){
        return lastCheckDateForPreviousDaysIncompleteHabitTypes;
    }

    public void setLastCheckDateForPreviousDaysIncompleteHabitTypes(){
        this.lastCheckDateForPreviousDaysIncompleteHabitTypes = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }
    public int getPreviousNutrientLevelTierRankUp() {
        return previousNutrientLevelTierRankUp;
    }

    public void setPreviousNutrientLevelTierRankUp(int level){
        this.previousNutrientLevelTierRankUp = level;
    }
    public void increaseNutrientLevel(int amount) {
        this.nutrientLevel += amount;
    }

    // nutrient level should never decrease below zero
    public void decreaseNutrientLevel(int amount) {
        this.nutrientLevel -= amount;
        if (this.nutrientLevel < 0) {
            this.nutrientLevel = 0;
        }
    }

}
