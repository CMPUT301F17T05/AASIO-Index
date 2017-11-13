package com.cmput301.t05.habilect;


import android.content.Context;
import android.content.ContextWrapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Facilitates the use of GSON for saving habit types, habit events and user profile information.
 * Useful to save and load a relevant information. This is a singleton class so that the stored
 * event and type information is held within one class and accessible across the whole app
 * @author rarog
 */

public class GSONController {
    //TODO: find a better way to do this
    public static final GSONController GSON_CONTROLLER = new GSONController();

    public static Context context;

    // lists which hold all of the users habit event and habit type information
    private static ArrayList<HabitEvent> eventList;
    private static ArrayList<HabitType> typeList;

    // for easy checking if events are types are already stored
    private static ArrayList<String> eventTitleAndDateList;
    private static ArrayList<String> typeTitleList;

    // file name locations
    private static String EVENT_FILE_NAME = "HabitEventList.sav";
    private static String TYPE_FILE_NAME = "HabitTypeList.sav";

    GSONController() {
        if(context != null) {
            eventList = loadAllEventsFromFile();
            typeList = loadAllTypesFromFile();
            eventTitleAndDateList = getAllEventTitleAndDate();
            typeTitleList = getAllTypeTitles();
        }
    }
    //region initialization region
    private ArrayList<String> getAllEventTitleAndDate() {
        ArrayList<String> eventTitleList = new ArrayList<String>();
        for (HabitEvent event : eventList) {
            eventTitleList.add(event.getHabitType() + "_" + event.getCompletionDate());
        }
        return eventTitleList;
    }

    private ArrayList<String> getAllTypeTitles() {
        ArrayList<String> typeTitleList = new ArrayList<String>();
        for (HabitType type : typeList) {
            typeTitleList.add(type.getTitle());
        }
        return typeTitleList;
    }

    private ArrayList<HabitEvent> loadAllEventsFromFile() {
        ArrayList<HabitEvent> fullList;
        try {
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            File directory = cw.getDir("userData", Context.MODE_PRIVATE);
            File myPath = new File(directory, EVENT_FILE_NAME);

            FileReader fr = new FileReader(myPath);

            BufferedReader in = new BufferedReader(fr);

            Gson gson = new Gson();

            //Taken from https://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt
            // 2017-09-19
            Type listType = new TypeToken<ArrayList<HabitEvent>>(){}.getType();
            fullList = gson.fromJson(in, listType);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            fullList = new ArrayList<>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }

        return fullList == null ? new ArrayList<HabitEvent>() : fullList;
    }

    private ArrayList<HabitType> loadAllTypesFromFile() {
        ArrayList<HabitType> fullList;
        try {
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            File directory = cw.getDir("userData", Context.MODE_PRIVATE);
            File myPath = new File(directory, TYPE_FILE_NAME);

            FileReader fr = new FileReader(myPath);

            BufferedReader in = new BufferedReader(fr);

            Gson gson = new Gson();

            //Taken from https://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt
            // 2017-09-19
            Type listType = new TypeToken<ArrayList<HabitType>>(){}.getType();
            fullList = gson.fromJson(in, listType);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            fullList = new ArrayList<>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }

        return fullList == null ? new ArrayList<HabitType>() : fullList;
    }

    /**
     *
     * @return an ArrayList of all habit events stored on file
     */
    public ArrayList<HabitEvent> loadHabitEventFromFile() {
        try {
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            File directory = cw.getDir("userData", Context.MODE_PRIVATE);
            File myPath = new File(directory, EVENT_FILE_NAME);

            FileReader fr = new FileReader(myPath);

            BufferedReader in = new BufferedReader(fr);

            Gson gson = new Gson();

            //Taken from https://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt
            // 2017-09-19
            Type listType = new TypeToken<ArrayList<HabitEvent>>(){}.getType();
            eventList = gson.fromJson(in, listType);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            eventList = new ArrayList<HabitEvent>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }

        return eventList == null ? new ArrayList<HabitEvent>() : eventList;
    } //endregion

    /**
     * Saves a habit event in file if we don't already have it
     * @param event the habit evnt you want to save
     */
    public void saveHabitEventInFile(HabitEvent event) {
        try {
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            File directory = cw.getDir("userData", Context.MODE_PRIVATE);
            File myPath = new File(directory, EVENT_FILE_NAME);

            FileWriter fw = new FileWriter(myPath);

            BufferedWriter out = new BufferedWriter(fw);

            // if we don't already have the event saved, add it to the list
            if(!eventInEventList(event) && event != null) {
                eventList.add(event);
                eventTitleAndDateList.add(event.getHabitType() + "_" + event.getCompletionDate());
            }

            Gson gson = new Gson();
            gson.toJson(eventList, out);
            out.flush();

            fw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    /**
     * Saves the current Habit event list to file
     */
    private void saveHabitEventListInFile() {
        try {
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            File directory = cw.getDir("userData", Context.MODE_PRIVATE);
            File myPath = new File(directory, EVENT_FILE_NAME);

            FileWriter fw = new FileWriter(myPath);

            BufferedWriter out = new BufferedWriter(fw);

            Gson gson = new Gson();
            gson.toJson(eventList, out);
            out.flush();

            fw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    /**
     * For deleting a habit event on file
     * @param event the habit event you want to delete
     */
    public void deleteHabitEventInFile(HabitEvent event) {
        HabitEvent rmEvent = findHabitEvent(event.getHabitType(), event.getCompletionDate());
        eventList.remove(rmEvent);
        saveHabitEventListInFile();
    }

    /**
     * If you want to edit a saved event, pass the event to this method
     * @param event the event you edited
     */
    public void editHabitEventInFile(HabitEvent event) {
        try {
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            File directory = cw.getDir("userData", Context.MODE_PRIVATE);
            File myPath = new File(directory, EVENT_FILE_NAME);

            FileWriter fw = new FileWriter(myPath);

            BufferedWriter out = new BufferedWriter(fw);

            HabitEvent rmEvent = findHabitEvent(event.getHabitType(), event.getCompletionDate());
            eventList.remove(rmEvent);
            eventList.add(event);

            Gson gson = new Gson();
            gson.toJson(eventList, out);
            out.flush();

            fw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    /**
     * Finds the habit event object that is saved
     * @param title title of habit event
     * @param date completion date of habit event
     * @return
     */
    private HabitEvent findHabitEvent(String title, Date date) {
        String inDate = new SimpleDateFormat("yyyy_MM_dd").format(date);
        for(HabitEvent event : eventList) {
            String eventDate = new SimpleDateFormat("yyyy_MM_dd").format(event.getCompletionDate());
            if(title.equals(event.getHabitType()) && inDate.equals(eventDate)) {
                return event;
            }
        }
        return null;
    }

    /**
     *
     * @return an ArrayList of all habit types stored on file
     */
    public ArrayList<HabitType> loadHabitTypeFromFile() {
        try {
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            File directory = cw.getDir("userData", Context.MODE_PRIVATE);
            File myPath = new File(directory, TYPE_FILE_NAME);

            FileReader fr = new FileReader(myPath);

            BufferedReader in = new BufferedReader(fr);

            Gson gson = new Gson();

            //Taken from https://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt
            // 2017-09-19
            Type listType = new TypeToken<ArrayList<HabitType>>(){}.getType();
            typeList = gson.fromJson(in, listType);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            typeList = new ArrayList<HabitType>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
        return typeList == null ? new ArrayList<HabitType>() : typeList;
    }

    /**
     * For saving a habit type on file
     * @param habitType the habit type you want to save
     */
    public void saveHabitTypeInFile(HabitType habitType) {
        try {
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            File directory = cw.getDir("userData", Context.MODE_PRIVATE);
            File myPath = new File(directory, TYPE_FILE_NAME);

            FileWriter fw = new FileWriter(myPath);

            BufferedWriter out = new BufferedWriter(fw);

            // if we don't already have the type saved, add it to the list
            if(!typeInEventList(habitType)) {
                typeList.add(habitType);
                typeTitleList.add(habitType.getTitle());
            }

            Gson gson = new Gson();
            gson.toJson(typeList, out);
            out.flush();

            fw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    /**
     * For deleting a habit type on file
     * @param habitType the habit type you want to save
     */
    public void deleteHabitTypeInFile(HabitType habitType) {
        if(typeInEventList(habitType)) {
            typeList.remove(habitType);
            typeTitleList.remove(habitType.getTitle());
            saveHabitTypeListInFile();
        }
    }

    /**
     * If you want to edit a habit event, pass the new habit event and title of the old one
     * to this method
     * @param habitType habitType that you edited
     * @param title old title of the habitType that you edited
     */
    public void editHabitTypeInFile(HabitType habitType, String title) {
        try {
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            File directory = cw.getDir("userData", Context.MODE_PRIVATE);
            File myPath = new File(directory, TYPE_FILE_NAME);

            FileWriter fw = new FileWriter(myPath);

            BufferedWriter out = new BufferedWriter(fw);

            HabitType rmType = findHabitType(title);
            //Log.d("Debugging:", "retrieved habit type: " + rmType.getTitle());
            typeList.remove(rmType);
            typeList.add(habitType);

            Gson gson = new Gson();
            gson.toJson(typeList, out);
            out.flush();

            fw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    /**
     * Finds specified HabitType object on file
     * @param title title of habit type object you want to find
     * @return
     */
    private HabitType findHabitType(String title) {
        for(HabitType type : typeList) {
            if(title.equals(type.getTitle())) {
                return type;
            }
        }
        return null;
    }

    /**
     * saves the whole habit type list to file
     */
    private void saveHabitTypeListInFile() {
        try {
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            File directory = cw.getDir("userData", Context.MODE_PRIVATE);
            File myPath = new File(directory, TYPE_FILE_NAME);

            FileWriter fw = new FileWriter(myPath);

            BufferedWriter out = new BufferedWriter(fw);;

            Gson gson = new Gson();
            gson.toJson(typeList, out);
            out.flush();

            fw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    /**
     * Sees if there is a particular habit event saved. Finds by title and date
     * @param event habit event that you want to check
     * @return boolean indicating whether the event was found in the list
     */
    private boolean eventInEventList(HabitEvent event) {
        String combined = event.getHabitType() + "_" + event.getCompletionDate();
        return eventTitleAndDateList.contains(combined);
    }

    /**
     * Sees if there is a particular habit type saved. Finds by title and date
     * @param type habit type that you want to check
     * @return boolean indicating whether the type was found in the list
     */
    private boolean typeInEventList(HabitType type) {
        return typeTitleList.contains(type.getTitle());
    }
}
