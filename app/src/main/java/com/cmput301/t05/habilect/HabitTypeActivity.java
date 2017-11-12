package com.cmput301.t05.habilect;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class HabitTypeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static HabitType habit_type;      // The habit type that has been clicked on for viewing

    /**
     * sets up the activity and grabs the habit type that was passed in through a different
     * activity
     * @param savedInstanceState        non-persistent, dynamic data saved in the state of the app
     * @see ViewHabitTypesActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_type);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Will be able to add a habit event when clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        habit_type = (HabitType) getIntent().getSerializableExtra("ClickedHabitType");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_habit_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        // The fragment argument representing the section number for this fragment.
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.placeholder_fragment, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * The fragment that displays all of the details of the habit type, including the
     * statistics on how closely the user is following the habit type weekly plan.
     */
    public static class HabitDetailsFragment extends Fragment {

        // The fragment argument representing the section number for this fragment.
        private static final String ARG_SECTION_NUMBER = "section_number";

        public HabitDetailsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static HabitDetailsFragment newInstance(int sectionNumber) {
            HabitDetailsFragment fragment = new HabitDetailsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        /**
         * sets the TextViews with the details of the habit type.
         *
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return the rootView
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_habit_type_details, container, false);

            TextView habitTitle = rootView.findViewById(R.id.textViewTitle);
            TextView habitReason = rootView.findViewById(R.id.textViewReason);
            TextView habitStartDate = rootView.findViewById(R.id.textViewStartDate);
            TextView habitWeeklyPlan = rootView.findViewById(R.id.textViewWeeklyPlan);

            habitTitle.setText(habit_type.getTitle());
            habitReason.setText(habit_type.getReason());
            habitStartDate.setText(habit_type.getStartDate().toString());
            habitWeeklyPlan.setText(habit_type.getWeeklyPlanString());

            return rootView;
        }
    }

    /**
     * The fragment that allows the user to edit the details of the habit type
     */
    public static class EditHabitFragment extends Fragment {

        // The fragment argument representing the section number for this fragment.
        private static final String ARG_SECTION_NUMBER = "section_number";
        private HabitTypeListener habitTypeListener;
        boolean[] weekly_plan = habit_type.getWeeklyPlan();
        CheckBox newMonday;
        CheckBox newTuesday;
        CheckBox newWednesday;
        CheckBox newThursday;
        CheckBox newFriday;
        CheckBox newSaturday;
        CheckBox newSunday;

        public EditHabitFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static EditHabitFragment newInstance(int sectionNumber) {
            EditHabitFragment fragment = new EditHabitFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public void setHabitTypeListener(HabitTypeListener habitTypeListener) {
            this.habitTypeListener = habitTypeListener;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_edit_habit_type, container, false);
            final EditText newTitle = rootView.findViewById(R.id.editTextEditHabitTypeTitle);
            final EditText newReason = rootView.findViewById(R.id.editTextEditHabitTypeReason);
            final DatePicker newStartDate = rootView.findViewById(R.id.datePickerEditHabitTypeStartDate);

            newMonday = rootView.findViewById(R.id.checkBoxEditMonday);
            newTuesday = rootView.findViewById(R.id.checkBoxEditTuesday);
            newWednesday = rootView.findViewById(R.id.checkBoxEditWednesday);
            newThursday = rootView.findViewById(R.id.checkBoxEditThursday);
            newFriday = rootView.findViewById(R.id.checkBoxEditFriday);
            newSaturday = rootView.findViewById(R.id.checkBoxEditSaturday);
            newSunday = rootView.findViewById(R.id.checkBoxEditSunday);
            setCheckboxes();

            ArrayList<CheckBox> checkboxes = new ArrayList<>();
            checkboxes.add(newMonday);
            checkboxes.add(newTuesday);
            checkboxes.add(newWednesday);
            checkboxes.add(newThursday);
            checkboxes.add(newFriday);
            checkboxes.add(newSaturday);
            checkboxes.add(newSunday);
            setListeners(checkboxes);

            this.setHabitTypeListener(new HabitTypeListener() {
                /**
                 * Implements the method that will try adding the entered information to the
                 * existing habit type. If an exception is thrown by the habit type's setters,
                 * it will be caught and thrown to the view to deal with
                 *
                 * @param title                 a String 0 <= length <= 20
                 * @param reason                a String 0 <= length <= 30
                 * @param start_date            a Date specifying when the user
                 *                              started the habit type
                 * @param weekly_plan           a boolean array specifying the days of the week the
                 *                              user plans on completing the habit type
                 * @see HabitTypeListener
                 */
                @Override
                public void OnAddedOrEdited(String title, String reason, Date start_date, boolean[] weekly_plan) {
                    try {
                        if (title.length() > 0) { habit_type.setTitle(title); }
                        if (reason.length() > 0) { habit_type.setReason(reason); }
                        habit_type.setStartDate(start_date);
                        habit_type.setWeeklyPlan(weekly_plan);
                    } catch (IllegalArgumentException e) {
                        throw e;
                    }
                }

                /**
                 * hasn't been implemented yet -- might delete
                 */
                @Override
                public void OnCancelled() {

                }
            });

            ((Button)rootView.findViewById(R.id.buttonSaveHabitTypeEdits))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String title = newTitle.getText().toString();
                            String reason = newReason.getText().toString();
                            Date start_date = new Date(newStartDate.getYear(), newStartDate.getMonth(),
                                    newStartDate.getDayOfMonth());
                            try {
                                habitTypeListener.OnAddedOrEdited(title, reason, start_date, weekly_plan);
                            } catch (IllegalArgumentException e) {
                                if (e.getMessage().equals("title")) {
                                    newTitle.setError("This field cannot be greater than 20 characters");
                                }
                                if (e.getMessage().equals("reason")) {
                                    newReason.setError("This field cannot be greater than 30 characters");
                                }
                                if (e.getMessage().equals("plan")) {
                                    //TODO: implement an error dialog, fix outstanding issue where weekly plan is still edited
                                    Log.d("Debugging", "no days selected");
                                }
                            }
                        }
                    });
            return rootView;
        }

        /**
         * sets the CheckBoxes to the values in the habit type weekly plan
         *
         * @see HabitType
         */
        public void setCheckboxes() {
            newMonday.setChecked(habit_type.getWeeklyPlan()[0]);
            newTuesday.setChecked(habit_type.getWeeklyPlan()[1]);
            newWednesday.setChecked(habit_type.getWeeklyPlan()[2]);
            newThursday.setChecked(habit_type.getWeeklyPlan()[3]);
            newFriday.setChecked(habit_type.getWeeklyPlan()[4]);
            newSaturday.setChecked(habit_type.getWeeklyPlan()[5]);
            newSunday.setChecked(habit_type.getWeeklyPlan()[6]);
        }

        //TODO: get rid of duplicate code
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
                case R.id.checkBoxEditMonday:
                    weekly_plan[0] = checked;
                    break;
                case R.id.checkBoxEditTuesday:
                    weekly_plan[1] = checked;
                    break;
                case R.id.checkBoxEditWednesday:
                    weekly_plan[2] = checked;
                    break;
                case R.id.checkBoxEditThursday:
                    weekly_plan[3] = checked;
                    break;
                case R.id.checkBoxEditFriday:
                    weekly_plan[4] = checked;
                    break;
                case R.id.checkBoxEditSaturday:
                    weekly_plan[5] = checked;
                    break;
                case R.id.checkBoxEditSunday:
                    weekly_plan[6] = checked;
                    break;
            }
            //Log.d("Debugging", "plan:"+ Arrays.toString(weekly_plan));
        }

        //TODO: get rid of duplicate code
        /**
         * Sets OnClickListeners for each CheckBox object in the ArrayList. The onClick method
         * is overridden with the custom method onCheckboxClicked.
         *
         * @param checkboxes        an ArrayList of CheckBox objects in the view that need listeners
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

    /**
     * A fragment that allows the user to delete, set shared, or something else -- might delete
     */
    public static class HabitOptionsFragment extends Fragment {

        // The fragment argument representing the section number for this fragment.
        private static final String ARG_SECTION_NUMBER = "section_number";

        public HabitOptionsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static HabitOptionsFragment newInstance(int sectionNumber) {
            HabitOptionsFragment fragment = new HabitOptionsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_habit_type_options, container, false);


            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //TODO: fix issue where details and edit fragments aren't updated upon changing tabs
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // if something goes wrong, return placeholder
            if (position == 0) {
                return new HabitDetailsFragment();
            }
            else if (position == 1) {
                return new EditHabitFragment();
            }
            else if (position == 3) {
                return new HabitOptionsFragment();
            }
            else {
                return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "DETAILS";
                case 1:
                    return "EDIT";
                case 2:
                    return "OPTIONS";
            }
            return null;
        }
    }
}
