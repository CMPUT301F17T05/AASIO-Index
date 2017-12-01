package com.cmput301.t05.habilect;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.cmput301.t05.habilect.UserProfile.HABILECT_USER_INFO;

/**
 * This allows a habit event to be displayed in a ListView. It also includes edit and
 * delete buttons for the row to edit or delete the habit event.
 *
 * @author rarog
 */
// TODO: when editing or deleting does not immediately update the view
public class HabitEventEditListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<HabitEvent> eventList = new ArrayList<>();
    private Context context;
    private HabitEvent event;
    private Context mContext;
    private String habitType;
    private Date date;

    HabitEventEditListAdapter(ArrayList<HabitEvent> eventList, Context context, Context mContext) {
        this.eventList = eventList;
        this.context = context;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int i) {
        return eventList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // inflates the view
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.habit_event_row_with_edit, null);
        }
        // gets the counter object that we want to display
        event = eventList.get(i);

        final UserProfile profile = new UserProfile(mContext);
        TreeGrowth profileTreeGrowth = profile.treeGrowth;

        Button viewButton = view.findViewById(R.id.habitEventEditRowSelectButton);

        TextView habitTitle = view.findViewById(R.id.habitEventRowEditTitle);
        TextView habitDate = view.findViewById(R.id.habitEventRowEditDate);
        TextView habitComment = view.findViewById(R.id.habitEventRowEditComment);
        ImageButton editButton = view.findViewById(R.id.habitEventRowEditEditButton);
        ImageButton deleteButton = view.findViewById(R.id.habitEventRowEditDeleteButton);

        habitTitle.setText(event.getHabitType());
        habitDate.setText(event.getCompletionDate().toString());
        habitComment.setText(event.getComment());

        habitType = habitTitle.getText().toString();
        date = event.getCompletionDate();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditHabitEventDialog editHabitEventDialog = new EditHabitEventDialog();
                editHabitEventDialog.setOnEditHabitEventListener(new OnEditHabitEventListener() {
                    @Override
                    public void OnAdded() {
                        // TODO: implement OnAdded
                        HabitEvent newEvent =
                                editHabitEventFromBundle(editHabitEventDialog.getResultBundle());
                        // TODO: GSON
                        GSONController.GSON_CONTROLLER.editHabitEventInFile(newEvent);
                        eventList.remove(event);
                        eventList.add(newEvent);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void OnCancelled() {
                        // do nothing...
                    }
                });
                FragmentActivity activity = (FragmentActivity) context;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                Bundle bundle = sendHabitInfoToDialog();
                editHabitEventDialog.setArguments(bundle);
                editHabitEventDialog.show(fragmentManager, "addHabitEventDialog");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: GSON
                GSONController.GSON_CONTROLLER.deleteHabitEventInFile(event);

                eventList.remove(event);
                notifyDataSetChanged();

                SharedPreferences sharedPreferences = context.getSharedPreferences(HABILECT_USER_INFO, Context.MODE_PRIVATE);
                String preference = sharedPreferences.getString(UserProfile.HABILECT_USER_TREE_GROWTH, null);

                int nutrientLevel = Integer.parseInt(preference);

                //test numbers
                nutrientLevel -= 30;

                if(nutrientLevel < 0){
                    nutrientLevel = 0;
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                profile.setTreeGrowth(Integer.toString(nutrientLevel));
                editor.putString(UserProfile.HABILECT_USER_TREE_GROWTH, Integer.toString(nutrientLevel));
                editor.commit();

                Log.i("NUTRIENTLEVEL: ", "" + profileTreeGrowth.getNutrientLevel());
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = sendHabitInfoToView(event);
                Intent intent = new Intent(view.getContext(), ViewHabitEventActivity.class);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);

            }
        });

        return view;
    }

    /**
     * Using the events information, makes a bundle so the edit dialog can be properly filled
     *
     * @return a bundle that can be sent off to the dialog
     */
    private Bundle sendHabitInfoToDialog() {
        Bundle bundle = new Bundle();
        ArrayList<String> list = new ArrayList<>();
        list.add(habitType);
        bundle.putString("Title", habitType);
        bundle.putString("Comment", event.getComment());
        String dateString = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).format(date);
        bundle.putString("Date", dateString);
        bundle.putSerializable("Habit Type", list);

        return bundle;
    }

    /**
     * Using the events information, makes a bundle so the view event activity can be properly filled
     *
     * @param event the habit event that you want to view
     * @return a bundle that can be sent off to the activity
     */
    private Bundle sendHabitInfoToView(HabitEvent event) {
        Bundle bundle = new Bundle();
        bundle.putString("Title", habitType);
        String dateString = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).format(date);
        bundle.putString("Date", dateString);
        bundle.putString("Comment", event.getComment());
        bundle.putString("File Path", habitType.replace(" ", "_") + "_" + dateString);

        return bundle;
    }

    /**
     * Makes a new habit event based on the provided information in the bundle
     *
     * @param bundle a bundle with the result from the dialog
     * @return a new HabitEvent
     * @see AddHabitEventDialogInformationGetter
     */
    private HabitEvent editHabitEventFromBundle(Bundle bundle) {
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
     *
     * @param directory the directory with the image file
     * @param filePath  the image file name
     * @return a bitmap of the decoded file
     */
    private Bitmap getBitmapFromFilePath(String directory, String filePath) {
        File image = new File(directory, filePath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        return bitmap;
    }
}
