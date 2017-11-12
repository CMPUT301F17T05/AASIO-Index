package com.cmput301.t05.habilect;

import android.app.Dialog;
import android.support.v4.app.DialogFragment ;
import android.os.Bundle;
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

public class AddHabitDialog extends DialogFragment {
    private OnAddHabitListener onAddHabitListener;
    private EditText habitTitleText;
    private EditText habitReasonText;
    //TODO: add variable for start date
    private DatePicker habitStartDate;
    private CheckBox checkMonday, checkTuesday, checkWednesday,
            checkThursday, checkFriday, checkSaturday, checkSunday;
    private boolean[] weekly_plan = {true,true,true,true,true,true,true};

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

        habitTitleText = dialog.findViewById(R.id.enterHabitTypeTitle);
        habitReasonText = dialog.findViewById(R.id.enterHabitTypeReason);
        habitStartDate = dialog.findViewById(R.id.datePickerStartDate);
        checkMonday = dialog.findViewById(R.id.checkBoxMonday);
        checkTuesday = dialog.findViewById(R.id.checkBoxTuesday);
        checkWednesday = dialog.findViewById(R.id.checkBoxWednesday);
        checkThursday = dialog.findViewById(R.id.checkBoxThursday);
        checkFriday = dialog.findViewById(R.id.checkBoxFriday);
        checkSaturday = dialog.findViewById(R.id.checkBoxSaturday);
        checkSunday = dialog.findViewById(R.id.checkBoxSunday);

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
                    String title = habitTitleText.getText().toString();
                    String reason = habitReasonText.getText().toString();
                    Date start_date = new Date(habitStartDate.getYear(), habitStartDate.getMonth(),
                            habitStartDate.getDayOfMonth());
                    try {
                        onAddHabitListener.OnAddedOrEdited(title, reason, start_date, weekly_plan);
                        dialog.dismiss();
                    } catch (IllegalArgumentException e) {
                        if (title.length() == 0 || title.length() > 20) {
                            habitTitleText.setError("This field cannot be blank, and cannot be greater than 20 characters");
                        }
                        if (reason.length() == 0 || reason.length() > 30) {
                            habitReasonText.setError("This field cannot be blank, and cannot be greater than 30 characters");
                        }
                    }
                }
            });
        ((Button)dialog.findViewById(R.id.cancel_button))
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddHabitListener.OnCancelled();
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
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
     * @author amwhitta
     * @param checkboxes is an ArrayList of CheckBox objects in the view that need listeners
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
