package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
    private UserAccount userAccount;
    private List<HabitType> habitTypeList;
    private HabitType relatedHabitType;
    private Context context;
    private HabitEvent event;
    private Context mContext;
    private String habitType;
    private Date date;
    public ImageButton deleteButton;

    HabitEventEditListAdapter(String habitType, Context context, Context mContext) {
        this.context = context;
        this.mContext = mContext;
        userAccount = new UserAccount().load(context);
        habitTypeList = userAccount.getHabits();
        relatedHabitType = findRelatedHabitType(habitType);
        this.eventList = relatedHabitType.getHabitEvents();
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
        relatedHabitType = findRelatedHabitType(event.getHabitType());

        final UserAccount user = new UserAccount();
        TreeGrowth profileTreeGrowth = user.getTreeGrowth();

        Button viewButton = view.findViewById(R.id.habitEventEditRowSelectButton);

        TextView habitTitle = view.findViewById(R.id.habitEventRowEditTitle);
        TextView habitDate = view.findViewById(R.id.habitEventRowEditDate);
        TextView habitComment = view.findViewById(R.id.habitEventRowEditComment);
        ImageButton editButton = view.findViewById(R.id.habitEventRowEditEditButton);
        deleteButton = view.findViewById(R.id.habitEventRowEditDeleteButton);

        habitTitle.setText(event.getHabitType());
        habitDate.setText(event.getCompletionDateString());
        String comment = event.getComment();
        if(comment.equals("")) {
            habitComment.setText("[no comment]");
        } else {
            habitComment.setText(comment);
        }

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
                        //relatedHabitType = findRelatedHabitType(event.getHabitType());
                        HabitEvent newEvent =
                                editHabitEventFromBundle(editHabitEventDialog.getResultBundle());
                        relatedHabitType.removeHabitEvent(event);
                        relatedHabitType.addHabitEvent(newEvent);
                        userAccount.setHabits(habitTypeList);
                        userAccount.save(context);
                        userAccount.sync(context);

                        //eventList.remove(event);
                        //eventList.add(newEvent);
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
                //relatedHabitType = findRelatedHabitType(event.getHabitType());
                relatedHabitType.removeHabitEvent(event);
                userAccount.save(context);
                userAccount.sync(context);

                eventList.remove(event);
                notifyDataSetChanged();


                int nutrientLevel = userAccount.getTreeGrowth().getNutrientLevel();

                nutrientLevel -= 1;

                if(nutrientLevel < 0){
                    nutrientLevel = 0;
                }

                userAccount.getTreeGrowth().setNutrientLevel(nutrientLevel);
                Log.i("NUTRIENTLEVEL: ", "" + userAccount.getTreeGrowth().getNutrientLevel());
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

    private HabitType findRelatedHabitType(String habitType) {
        Iterator<HabitType> iterator = habitTypeList.iterator();
        while(iterator.hasNext()) {
            HabitType habit = iterator.next();
            if(habit.getTitle().equals(habitType)) {
                return habit;
            }
        }
        return null;
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
        bundle.putString("Date", event.getCompletionDateString());
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        eventImage.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedString = Base64.encodeToString(byteArray, Base64.URL_SAFE | Base64.NO_WRAP);
        return new HabitEvent(comment, encodedString, location, date, title);
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
