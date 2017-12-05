package com.cmput301.t05.habilect;

/**
 * This interface is a Listener for the addHabitEvent dialog, it is used so that when the user
 * presses the create or cancel button, the calling activity can be informed and take appropriate
 * action
 *
 * @author rarog
 */
public interface OnAddHabitEventListener {
    public void OnAdded();

    public void OnCancelled();
}
