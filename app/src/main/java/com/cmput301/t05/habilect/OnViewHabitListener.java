package com.cmput301.t05.habilect;

/**
 * This interface is created so that appropriate action can be taken when a habit is viewed
 *
 * @author ioltuszy
 */

public interface OnViewHabitListener {
    public void OnDeleted();

    public void OnSaved();

    public void OnFollowed();

    public void OnCancelled();
}
