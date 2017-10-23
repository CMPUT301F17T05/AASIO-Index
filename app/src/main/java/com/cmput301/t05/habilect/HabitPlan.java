package com.cmput301.t05.habilect;

import java.util.List;

/**
 * @author ioltuszy
 * @author amwhitta
 */

public class HabitPlan {

    private HabitType habitType;
    private int[] targetFrequency;
    boolean[] targetDays;
    List<HabitEvent> history;

    public HabitPlan(HabitType habitType, int[] targetFrequency, boolean[] targetDays, List<HabitEvent> history) {
        this.setHabitType(habitType);
        this.setTargetFrequency(targetFrequency);
        this.setTargetDays(targetDays);
        this.setHistory(history);
    }

    public HabitType getHabitType() {
        return this.habitType;
    }

    public int[] getTargetFrequency() {
        return this.targetFrequency;
    }

    public boolean[] getTargetDays() {
        return this.targetDays;
    }

    public List<HabitEvent> getHistory() {
        return this.history;
    }

    public void setHabitType(HabitType habitType) {
        if (habitType == null) {
            throw new IllegalArgumentException();
        } else {
            this.habitType = habitType;
        }
    }

    public void setTargetFrequency(int[] targetFrequency) {
        if (targetFrequency.length != 7) {
            throw new IllegalArgumentException();
        } else {
            this.targetFrequency = targetFrequency;
        }
    }

    public void setTargetDays(boolean[] targetDays) {
        if (targetDays.length != 7) {
            throw new IllegalArgumentException();
        } else {
            this.targetDays = targetDays;
        }
    }

    public void setHistory(List<HabitEvent> history) {
        this.history = history;
    }
}
