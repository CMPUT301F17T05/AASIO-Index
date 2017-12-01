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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
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
    private static HabitEventEditListAdapter eventListAdapter;
    FragmentManager fragmentManager;
    private static ArrayList<HabitEvent> eList;

    private static Context mContext;
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

        Toolbar toolbar = findViewById(R.id.toolbar);

        mContext = getApplicationContext();

        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        String habit_type_title = getIntent().getStringExtra("ClickedHabitType");
        setTitle("Habilect - " + habit_type_title);
        habit_type = GSONController.GSON_CONTROLLER.findHabitType(habit_type_title);

        eList = GSONController.GSON_CONTROLLER.loadHabitEventFromFile();
        eList = filterEventList(habit_type, eList);
        eventListAdapter = new HabitEventEditListAdapter(eList, this, mContext);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AddHabitEventDialog addHabitEventDialog = new AddHabitEventDialog();
                addHabitEventDialog.setOnAddHabitEventListener(new OnAddHabitEventListener() {
                    @Override
                    public void OnAdded() {
                        HabitEvent event =
                                createHabitEventFromBundle(addHabitEventDialog.getResultBundle());

                        habit_type.addHabitEvent(event);

                        GSONController.GSON_CONTROLLER.editHabitTypeInFile(habit_type, habit_type.getTitle());
                        boolean saveSuccess = GSONController.GSON_CONTROLLER.saveHabitEventInFile(event);
                        if(saveSuccess) {
                            eList.add(event);
                        }
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
	Navigation.setup(findViewById(android.R.id.content));
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
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            final EditHabitTypeDialog editHabitTypeDialog = new EditHabitTypeDialog();
            editHabitTypeDialog.setHabitTypeListener(new HabitTypeListener() {
                @Override
                public void OnAddedOrEdited(String title, String reason, Date start_date, boolean[] weekly_plan) {
                    try {
                        ArrayList<HabitType> all_habit_types = GSONController.GSON_CONTROLLER.loadHabitTypeFromFile();
                        for (HabitType h: all_habit_types) {
                            if (title.equals(h.getTitle())) {
                                throw new IllegalArgumentException("title");
                            }
                        }
                        HabitType edited_habit_type = new HabitType(habit_type);

                        if (title.length() > 0) { edited_habit_type.setTitle(title); }
                        if (reason.length() > 0) { edited_habit_type.setReason(reason); }
                        edited_habit_type.setStartDate(start_date);
                        edited_habit_type.setWeeklyPlan(weekly_plan);

                        GSONController.GSON_CONTROLLER.editHabitTypeInFile(edited_habit_type, habit_type.getTitle());
                        habit_type = new HabitType(edited_habit_type);

                    } catch (IllegalArgumentException e) {
                        throw e;
                    }
                }
                @Override
                public void OnDeleted() {
                    // N/A
                }
            });
            Bundle bundle = new Bundle();
            bundle.putString("Habit Title", habit_type.getTitle());
            editHabitTypeDialog.setArguments(bundle);
            editHabitTypeDialog.show(fragmentManager, "editHabitTypeDialog");
        }
        if (id == R.id.action_delete) {
            HabitTypeListener habitTypeListener = new HabitTypeListener() {
                @Override
                public void OnAddedOrEdited(String title, String reason, Date start_date, boolean[] weekly_plan) {
                }

                @Override
                public void OnDeleted() {
                    GSONController.GSON_CONTROLLER.deleteHabitTypeInFile(habit_type);
                }
            };
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


    /**
     * The fragment that displays all of the details of the habit type (excluding stats).
     */
    public static class HabitDetailsFragment extends Fragment {

        TextView habitTitle;
        TextView habitReason;
        TextView habitStartDate;
        TextView habitWeeklyPlan;

        public HabitDetailsFragment() {
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
            habitStartDate.setText(habit_type.getStartDateString());
            habitWeeklyPlan.setText(habit_type.getWeeklyPlanString());
        }
    }

    /**
     * The fragment that allows the user to see the habit type statistics.
     */
    public static class HabitStatsFragment extends Fragment {

        private GraphView graph;
        private TextView displayAverage;

        public HabitStatsFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_habit_type_stats, container, false);
            graph = rootView.findViewById(R.id.graphHabitType);
            displayAverage = rootView.findViewById(R.id.textViewAverageStat);
            return rootView;
        }


        @Override
        public void onResume() {
            super.onResume();

            StatisticsCalculator calculator = new StatisticsCalculator(habit_type);
            DataPoint[] points = calculator.pastFourWeeks();

            int average = calculator.averageCompletion();
            String displayed_average = Integer.toString(average) + "%";
            displayAverage.setText(displayed_average);

            graph.setTitle("Past 4 Weeks of Habit Events");

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
            graph.addSeries(series);

            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
            staticLabelsFormatter.setHorizontalLabels(new String[] {"4", "3", "2", "1", "current"});
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

            graph.getGridLabelRenderer().setPadding(100);

            graph.getGridLabelRenderer().setVerticalAxisTitle("# Events Created");
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Week(s) ago");
        }
    }

    /**
     * A fragment that allows the user to see their habit events.
     */
    public static class HabitTypeEventsFragment extends Fragment {

        ArrayList<HabitEvent> eList;


        public HabitTypeEventsFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_habit_type_events, container, false);

            ListView eventList = rootView.findViewById(R.id.fragmentHabitTypeEventsListView);

            eList = GSONController.GSON_CONTROLLER.loadHabitEventFromFile();
            eList = filterEventList(habit_type, eList);

            eventListAdapter = new HabitEventEditListAdapter(eList, getActivity(), mContext);
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

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HabitDetailsFragment();
            }
            else if (position == 1) {
                return new HabitStatsFragment();
            }
            else if (position == 2) {
                return new HabitTypeEventsFragment();
            }
            else {
                return null;
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
        return BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
    }
}
