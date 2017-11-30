package com.cmput301.t05.habilect;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment ;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author ioltuszy
 * @author amwhitta
 */

public class AddHabitTypeDialog extends DialogFragment {
    private HabitTypeListener habitTypeListener;        // the controller for adding or editing
    private EditText habitTitleText;
    private EditText habitReasonText;
    private DatePicker habitStartDate;
    private boolean[] weekly_plan = {true,true,true,true,true,true,true}; // initialize all checkboxes to checked

    public void setHabitTypeListener(HabitTypeListener habitTypeListener) {
        this.habitTypeListener = habitTypeListener;
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

    /**
     * ADD BUTTON: will retrieve the information from the view and pass it on
     * to the habitTypeListener, which will try creating a new HabitType with the given
     * information. If any errors are thrown, they will be caught here.
     *
     * DELETE BUTTON: N/A here.
     *
     * @see HomePrimaryFragment
     * @see HabitTypeListener
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_addhabit, null);
        dialog.setContentView(view);

        habitTitleText = dialog.findViewById(R.id.enterHabitTypeTitle);
        habitReasonText = dialog.findViewById(R.id.enterHabitTypeReason);
        habitStartDate = dialog.findViewById(R.id.datePickerStartDate);
        CheckBox checkMonday = dialog.findViewById(R.id.checkBoxMonday);
        CheckBox checkTuesday = dialog.findViewById(R.id.checkBoxTuesday);
        CheckBox checkWednesday = dialog.findViewById(R.id.checkBoxWednesday);
        CheckBox checkThursday = dialog.findViewById(R.id.checkBoxThursday);
        CheckBox checkFriday = dialog.findViewById(R.id.checkBoxFriday);
        CheckBox checkSaturday = dialog.findViewById(R.id.checkBoxSaturday);
        CheckBox checkSunday = dialog.findViewById(R.id.checkBoxSunday);

        ArrayList<CheckBox> checkboxes = new ArrayList<>();
        checkboxes.add(checkMonday);
        checkboxes.add(checkTuesday);
        checkboxes.add(checkWednesday);
        checkboxes.add(checkThursday);
        checkboxes.add(checkFriday);
        checkboxes.add(checkSaturday);
        checkboxes.add(checkSunday);
        setListeners(checkboxes);

        ((Button)dialog.findViewById(R.id.add_button))
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = getActivity();
                    String title = habitTitleText.getText().toString();
                    String reason = habitReasonText.getText().toString();
                    Date start_date = new Date(habitStartDate.getYear(), habitStartDate.getMonth(),
                            habitStartDate.getDayOfMonth());
                    try {
                        habitTypeListener.OnAddedOrEdited(title, reason, start_date, weekly_plan);
                        dialog.dismiss();
                    } catch (IllegalArgumentException e) {
                        if (e.getMessage().equals("title")) {
                            habitTitleText.setError("This field cannot be blank, and cannot be greater than 20 characters. The title has to be unique.");
                        }
                        if (e.getMessage().equals("reason")) {
                            habitReasonText.setError("This field cannot be blank, and cannot be greater than 30 characters.");
                        }
                        if (e.getMessage().equals("plan")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                            alertDialog.setMessage("You must select at least one day for your weekly plan.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }
                }
            });

        ((Button)dialog.findViewById(R.id.cancel_button))
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
     * The method called by the OnClickListener of each CheckBox. Changes the boolean value of
     * weekly_plan at the index that corresponds to the CheckBox that was clicked.
     *
     * @see <a href="https://developer.android.com/guide/topics/ui/controls/checkbox.html">Android Developers -- Checkboxes</a>
     */
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkBoxMonday:
                weekly_plan[0] = checked;
                break;
            case R.id.checkBoxTuesday:
                weekly_plan[1] = checked;
                break;
            case R.id.checkBoxWednesday:
                weekly_plan[2] = checked;
                break;
            case R.id.checkBoxThursday:
                weekly_plan[3] = checked;
                break;
            case R.id.checkBoxFriday:
                weekly_plan[4] = checked;
                break;
            case R.id.checkBoxSaturday:
                weekly_plan[5] = checked;
                break;
            case R.id.checkBoxSunday:
                weekly_plan[6] = checked;
                break;
        }
        //Log.d("Debugging", "plan:"+ Arrays.toString(weekly_plan));
    }

    /**
     * Sets OnClickListeners for each CheckBox object in the ArrayList. The onClick method
     * is overridden with the custom method onCheckboxClicked.
     *
     * @param checkboxes            an ArrayList of CheckBox objects in the view that need listeners
     */
    public void setListeners(ArrayList<CheckBox> checkboxes) {
        for (CheckBox c : checkboxes) {
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCheckboxClicked(view);
                }
            });
        }
    }
}
