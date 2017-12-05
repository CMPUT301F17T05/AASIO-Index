package com.cmput301.t05.habilect;

import java.util.Date;

/**
 * @author ioltuszy
 * @author amwhitta
 */

public interface HabitTypeListener {
    void OnAddedOrEdited(String title, String reason, Date start_date, boolean[] weekly_plan);

    void OnDeleted();
}
