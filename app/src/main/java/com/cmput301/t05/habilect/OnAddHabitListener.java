package com.cmput301.t05.habilect;

import java.util.Date;

/**
 * @author ioltuszy
 */

public interface OnAddHabitListener {
    void OnAddedOrEdited(String title, String reason, Date start_date, boolean[] weekly_plan);
    void OnCancelled();
}
