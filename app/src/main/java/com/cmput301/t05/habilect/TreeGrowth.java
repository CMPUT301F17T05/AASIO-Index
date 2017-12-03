package com.cmput301.t05.habilect;

/**
 * Created by StevenC on 2017-11-23.
 */

public class TreeGrowth {
    int nutrientLevel;
    int previousNutrientLevelTierRankUp;
    public TreeGrowth() {
        this.nutrientLevel = 0;
        this.previousNutrientLevelTierRankUp = 0;
    }

    public int getNutrientLevel() {
        return nutrientLevel;
    }

    public void setNutrientLevel(int nutrientLevel) {
        this.nutrientLevel = nutrientLevel;
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

    public void decreaseNutrientLevel(int amount) {
        this.nutrientLevel -= amount;
        if (this.nutrientLevel < 0) {
            this.nutrientLevel = 0;
        }
    }

}
