package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private static HabitEventEditListAdapter eventListAdapter;
    FragmentManager fragmentManager;
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

        fragmentManager = this.getSupportFragmentManager();

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
                final AddHabitEventDialog addHabitEventDialog = new AddHabitEventDialog();
                addHabitEventDialog.setOnAddHabitEventListener(new OnAddHabitEventListener() {
                    @Override
                    public void OnAdded() {
                        HabitEvent event =
                                createHabitEventFromBundle(addHabitEventDialog.getResultBundle());
                        GSONController.GSON_CONTROLLER.saveHabitEventInFile(event);
                        habit_type.addHabitEvent(event);
                        if(eventListAdapter != null) {
                            eventListAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void OnCancelled() {
                        // do nothing...
                    }
                });
                FragmentManager fragmentManager = getSupportFragmentManager();
                Bundle bundle = sendHabitInfoToDialog();
                addHabitEventDialog.setArguments(bundle);
                addHabitEventDialog.show(fragmentManager, "addHabitEventDialog");
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
        Context context = this;
        HabitTypeListener habitTypeListener = new HabitTypeListener() {
            @Override
            public void OnAddedOrEdited(String title, String reason, Date start_date, boolean[] weekly_plan) {
            }

            @Override
            public void OnDeleted() {
                GSONController.GSON_CONTROLLER.deleteHabitTypeInFile(habit_type);
            }
        };
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            final EditHabitTypeDialog editHabitTypeDialog = new EditHabitTypeDialog();
            editHabitTypeDialog.setHabitTypeListener(new HabitTypeListener() {
                @Override
                public void OnAddedOrEdited(String title, String reason, Date start_date, boolean[] weekly_plan) {
                    try {
                        HabitType edited_habit_type = new HabitType(habit_type);

                        if (title.length() > 0) { edited_habit_type.setTitle(title); }
                        if (reason.length() > 0) { edited_habit_type.setReason(reason); }
                        edited_habit_type.setStartDate(start_date);
                        edited_habit_type.setWeeklyPlan(weekly_plan);

                        GSONController.GSON_CONTROLLER.editHabitTypeInFile(edited_habit_type, habit_type.getTitle());
                        habit_type = edited_habit_type;

                    } catch (IllegalArgumentException e) {
                        throw e;
                    }
                }
                @Override
                public void OnDeleted() {
                    // TODO: implement OnDeleted
                }
            });
            editHabitTypeDialog.show(fragmentManager, "editHabitTypeDialog");
        }
        if (id == R.id.action_delete) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("DELETE");
            alertDialog.setMessage("Are you sure you want to delete this habit type and its " +
                    "associated habit events?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            habitTypeListener.OnDeleted();
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Bundle sendHabitInfoToDialog() {
        Bundle bundle = new Bundle();
        ArrayList<String> list = new ArrayList<>();
        list.add(habit_type.getTitle());
        bundle.putString("Title", habit_type.getTitle());
        bundle.putSerializable("Habit Type", list);

        return bundle;
    }

    private HabitEvent createHabitEventFromBundle(Bundle bundle) {
        AddHabitEventDialogInformationGetter getter =
                new AddHabitEventDialogInformationGetter(bundle);
        String title = getter.getTitle();
        String comment = getter.getComment();
        Location location = getter.getLocation();
        Date date = getter.getDate();
        String filePath = getter.getFileName();
        String directory = getter.getDirectory();
        Bitmap eventImage = getBitmapFromFilePath(directory, filePath);

        return new HabitEvent(comment, eventImage, location, date, title);
    }

    private Bitmap getBitmapFromFilePath(String directory, String filePath) {
        File image = new File(directory, filePath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
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
        TextView habitTitle;
        TextView habitReason;
        TextView habitStartDate;
        TextView habitWeeklyPlan;

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

            habitTitle = rootView.findViewById(R.id.textViewTitle);
            habitReason = rootView.findViewById(R.id.textViewReason);
            habitStartDate = rootView.findViewById(R.id.textViewStartDate);
            habitWeeklyPlan = rootView.findViewById(R.id.textViewWeeklyPlan);

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();

            habitTitle.setText(habit_type.getTitle());
            habitReason.setText(habit_type.getReason());
            habitStartDate.setText(habit_type.getStartDate().toString());
            habitWeeklyPlan.setText(habit_type.getWeeklyPlanString());
        }
    }

    /**
     * The fragment that allows the user to edit the details of the habit type
     */
    public static class EditHabitFragment extends Fragment {

        // The fragment argument representing the section number for this fragment.
        private static final String ARG_SECTION_NUMBER = "section_number";
        private GraphView graph;
        private TextView displayAverage;

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_edit_habit_type, container, false);
            graph = rootView.findViewById(R.id.graphHabitType);
            displayAverage = rootView.findViewById(R.id.textViewAverageStat);
            return rootView;
        }

        @Override
        public void onResume() {
            StatisticsCalculator calculator = new StatisticsCalculator(habit_type);
            DataPoint[] points = calculator.pastFourWeeks();

            int average = calculator.averageCompletion();
            displayAverage.setText(Integer.toString(average));

            graph.setTitle("Past 4 Weeks of Habit Events");

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
            graph.addSeries(series);

            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
            staticLabelsFormatter.setHorizontalLabels(new String[] {"4", "3", "2", "1", "current"});
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            super.onResume();

        }
    }

    /**
     * A fragment that allows the user to delete, set shared, or something else -- might delete
     */
    public static class HabitTypeEventsFragment extends Fragment {

        // The fragment argument representing the section number for this fragment.
        private static final String ARG_SECTION_NUMBER = "section_number";

        ArrayList<HabitEvent> eList;

        public HabitTypeEventsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static HabitTypeEventsFragment newInstance(int sectionNumber) {
            HabitTypeEventsFragment fragment = new HabitTypeEventsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        // TODO: does not upadate the list view automatically when you make event, must exit out of activity then reopen habit type
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_habit_type_events, container, false);

            ListView eventList = rootView.findViewById(R.id.fragmentHabitTypeOptionsListView);

            eList = GSONController.GSON_CONTROLLER.loadHabitEventFromFile();
            eList = filterEventList(habit_type, eList);

            eventListAdapter = new HabitEventEditListAdapter(eList, getActivity());
            eventList.setAdapter(eventListAdapter);

            return rootView;
        }

        /**
         * Filters the event list such that only the events with the matching habit type remain
         * @param habitType the habit type you want to filer by
         * @param eventList the event list you want to filer
         * @return returns a filter event list
         */
        private  ArrayList<HabitEvent> filterEventList(HabitType habitType, ArrayList<HabitEvent> eventList) {
            ArrayList<HabitEvent> newList = new ArrayList<>();
            for (HabitEvent event : eventList) {
                if(event.getHabitType().equals(habitType.getTitle())) {
                    newList.add(event);
                }
            }
            return newList;
        }

        @Override
        public void onStart() {
            super.onStart();
            eList = GSONController.GSON_CONTROLLER.loadHabitEventFromFile();
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
            else if (position == 2) {
                return new HabitTypeEventsFragment();
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
                    return "STATISTICS";
                case 2:
                    return "EVENTS";
            }
            return null;
        }
    }
}
