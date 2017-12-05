package com.cmput301.t05.habilect;

import java.util.Calendar;

/**
 * Class that is responsible for tree growth to track nutrient level, the last rank up tier as well as the last sync date.
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

    /**
     *
     * @return the nutrient level of the tree
     */
    public int getNutrientLevel() {
        return nutrientLevel;
    }

    /**
     * Sets the nutrient level to a specified amount
     * @param nutrientLevel to set to
     */
    public void setNutrientLevel(int nutrientLevel) {
        this.nutrientLevel = nutrientLevel;
        if (this.nutrientLevel < 0) {
            this.nutrientLevel = 0;
        }
    }

    /**
     *
     * @return the last date that a check was performed to determine incomplete habit types of the previous day
     */
    public int getLastCheckDateForPreviousDaysIncompleteHabitTypes(){
        return lastCheckDateForPreviousDaysIncompleteHabitTypes;
    }

    /**
     * Sets the date when a check was performed to determine incomplete habit types of the previous day
     */
    public void setLastCheckDateForPreviousDaysIncompleteHabitTypes(){
        this.lastCheckDateForPreviousDaysIncompleteHabitTypes = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    /**
     *
     * @return the last occurence when there was a level up. This is used to determine whether a level up dialog is shown.
     */
    public int getPreviousNutrientLevelTierRankUp() {
        return previousNutrientLevelTierRankUp;
    }

    /**
     * Sets the last nutrient level tier
     * @param level to set the last nutrient level tier
     */
    public void setPreviousNutrientLevelTierRankUp(int level){
        this.previousNutrientLevelTierRankUp = level;
    }

    /**
     * Increase the nutrient level by a specified amount
     * @param amount to increase the nutrient level by
     */
    public void increaseNutrientLevel(int amount) {
        this.nutrientLevel += amount;
    }

    /**
     * Decrease the nutrient level by a specified amount
     * @param amount to decrease the nutrient level by
     */
    // nutrient level should never decrease below zero
    public void decreaseNutrientLevel(int amount) {
        this.nutrientLevel -= amount;
        if (this.nutrientLevel < 0) {
            this.nutrientLevel = 0;
        }
    }

}
