package com.cmput301.t05.habilect;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * @author rarog
 */

public class AddHabitEventDialog extends DialogFragment {
    private OnAddHabitEventListener onAddHabitEventListener;

    public void setOnAddHabitEventListener(OnAddHabitEventListener onAddHabitEventListener) {
        this.onAddHabitEventListener = onAddHabitEventListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_habit_event, null);
        dialog.setContentView(view);
        return dialog;
    }
}
