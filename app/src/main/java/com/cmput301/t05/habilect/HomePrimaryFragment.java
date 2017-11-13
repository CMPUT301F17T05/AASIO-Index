package com.cmput301.t05.habilect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

/**
 * Shows a list of habit types that can be completed today, allows the user to navigate to
 * another activity using the drawer, allows the user to create a new habit event or habit type
 * using the FAB (to be implemented). ViewHabitDialog and OnViewHabitListener to be deleted or
 * modified.
 *
 * @author ioltuszy
 * @author amwhitta
 * @author rarog
 * @see HabitTypeListener
 * @see HabitType
 * @see HabitEvent
 */
public class HomePrimaryFragment extends Fragment {
    FragmentManager fragmentManager;

    private ListView habitTypeList;
    private ArrayList<HabitType> all_habit_types;
    private ArrayList<HabitType> incomplete_habit_types;
    //UserProfile user_profile = new UserProfile(getApplicationContext());
    ArrayAdapter<HabitType> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_home_primary, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();
        habitTypeList = rootView.findViewById(R.id.incompleteHabitsListView);


        habitTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), HabitTypeActivity.class);
                intent.putExtra("ClickedHabitType", incomplete_habit_types.get(i));
                startActivity(intent);
            }
        });

        //habit_types = GSONController.GSON_CONTROLLER.loadHabitTypeFromFile();
        //adapter = new ArrayAdapter<>(getActivity(), R.layout.habit_type_list_item, habit_types);
        //habitTypeList.setAdapter(adapter);

        final Button addHabitButton = (Button) rootView.findViewById(R.id.addHabitButton);
        addHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AddHabitDialog addHabitDialog = new AddHabitDialog();
                addHabitDialog.setHabitTypeListener(new HabitTypeListener() {
                    @Override
                    public void OnAddedOrEdited(String title, String reason, Date start_date, boolean[] weekly_plan) {
                        try {
                            HabitType habit_type = new HabitType(title, reason, start_date, weekly_plan);

                            GSONController.GSON_CONTROLLER.saveHabitTypeInFile(habit_type);
                            all_habit_types = GSONController.GSON_CONTROLLER.loadHabitTypeFromFile();
                            incomplete_habit_types = getIncompleteHabitTypes();
                            adapter.notifyDataSetChanged();

                            /*user_profile.addPlans(habit_type);
                            WebService.UpdateUserProfileTask updateUserProfileTask = new WebService.UpdateUserProfileTask();
                            updateUserProfileTask.execute(user_profile);*/

                        } catch (IllegalArgumentException e) {
                            throw e;
                        }
                    }
                    @Override
                    public void OnCancelled() {
                        // TODO: implement OnCancelled
                    }
                });
                addHabitDialog.show(fragmentManager, "addHabitDialog");
            }
        });

        final Button viewHabitButton = (Button) rootView.findViewById(R.id.viewHabitButton);
        viewHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ViewHabitDialog viewHabitDialog = new ViewHabitDialog();
                viewHabitDialog.setOnViewHabitListener(new OnViewHabitListener() {
                    @Override
                    public void OnDeleted() {
                        // TODO: implement OnDeleted
                    }

                    @Override
                    public void OnSaved() {
                        // TODO: implement OnSaved
                    }

                    @Override
                    public void OnFollowed() {
                        // TODO: implement OnFollowed
                    }

                    @Override
                    public void OnCancelled() {
                        // TODO: implement OnCancelled
                    }
                });
                viewHabitDialog.show(fragmentManager, "viewHabitDialog");
            }
        });

        // TODO: Add habit event from title once information saving is done
        final Button addHabitEventButton = rootView.findViewById(R.id.addHabitEvent);
        addHabitEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AddHabitEventDialog addHabitEventDialog = new AddHabitEventDialog();
                addHabitEventDialog.setOnAddHabitEventListener(new OnAddHabitEventListener() {
                    @Override
                    public void OnAdded() {
                        // TODO: implement OnAdded
                        HabitEvent event =
                                createHabitEventFromBundle(addHabitEventDialog.getResultBundle());
                        GSONController.GSON_CONTROLLER.saveHabitEventInFile(event);
                    }

                    @Override
                    public void OnCancelled() {
                        // TODO: implement OnCancelled
                    }
                });
                ArrayList<String> titleList = getHabitTitles();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Habit Type", titleList);
                addHabitEventDialog.setArguments(bundle);
                addHabitEventDialog.show(fragmentManager, "addHabitEventDialog");

            }
        });

        return rootView;
    }

    /**
     * From a returning bundle, make a new habit event
     * @param bundle the bundle which contains the information from AddHabitEventDialog
     * @return a new HabitEvent
     * @see AddHabitEventDialog
     */
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

    /**
     * Gets a bitmap from a file name and directory path
     * @param directory the directory with the image file
     * @param filePath the image file name
     * @return a bitmap of the decoded file
     */
    private Bitmap getBitmapFromFilePath(String directory, String filePath) {
        File image = new File(directory, filePath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }

    /**
     * Creates a list of string of the titles of all stored habit types
     * @return An ArrayList String of all habit Type title
     */
    private ArrayList<String> getHabitTitles() {
        ArrayList<String> list = new ArrayList<>();
        for(HabitType type : all_habit_types) {
            list.add(type.getTitle());
        }
        return list;
    }

    /**
     * gets all of the user's habit types locally or from elasticsearch (to be implemented) and
     * searches through the list to find the ones that need to be completed today.
     *
     * @see HabitType
     */
    @Override
    public void onStart() {
        super.onStart();

        all_habit_types = GSONController.GSON_CONTROLLER.loadHabitTypeFromFile();
        incomplete_habit_types = getIncompleteHabitTypes();
        adapter = new ArrayAdapter<>(getActivity(), R.layout.habit_type_list_item, incomplete_habit_types);
        habitTypeList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    /**
     * From the list of habit types, checks which ones still need to be done today
     * @return A list of habit types that need to be done
     */
    private ArrayList<HabitType> getIncompleteHabitTypes() {

        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.DAY_OF_WEEK);
        //Log.d("Debugging", "today in int:" + Integer.toString(today));
        boolean[] plan;
        ArrayList<HabitType> incomplete_habits = new ArrayList<>();

        for (HabitType h : all_habit_types) {
            plan = h.getWeeklyPlan();
            if (today == Calendar.MONDAY && plan[0]) {
                incomplete_habits.add(h);
            }
            else if (today == Calendar.TUESDAY && plan[1]) {
                incomplete_habits.add(h);
            }
            else if (today == Calendar.WEDNESDAY && plan[2]) {
                incomplete_habits.add(h);
            }
            else if (today == Calendar.THURSDAY && plan[3]) {
                incomplete_habits.add(h);
            }
            else if (today == Calendar.FRIDAY && plan[4]) {
                incomplete_habits.add(h);
            }
            else if (today == Calendar.SATURDAY && plan[5]) {
                incomplete_habits.add(h);
            }
            else if (today == Calendar.SUNDAY && plan[6]) {
                incomplete_habits.add(h);
            }
        }
        return incomplete_habits;
    }
}
