package com.cmput301.t05.habilect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment ;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AddHabitDialog extends DialogFragment {
    private OnAddHabitListener onAddHabitListener;
    public void setOnAddHabitListener(OnAddHabitListener onAddHabitListener) {
        this.onAddHabitListener = onAddHabitListener;
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
        View view = inflater.inflate(R.layout.dialog_addhabit, null);
        dialog.setContentView(view);
        ((Button)dialog.findViewById(R.id.add_button))
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAddHabitListener.OnAdded();
                    dialog.dismiss();
                }
            });
        ((Button)dialog.findViewById(R.id.cancel_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddHabitListener.OnCancelled();
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
