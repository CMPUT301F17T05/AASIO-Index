/*
package com.cmput301.t05.habilect;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * Created by Andrea on 07-11-17.
 *//*


public class ElasticsearchHabitTypeController {
    private static JestDroidClient client;

    public static class AddHabitTypeTask extends AsyncTask<HabitType, Void, Void> {

        @Override
        protected Void doInBackground(HabitType... habits) {
            verifySettings();

            for (HabitType habit : habits) {
                Index index = new Index.Builder(habit).index("testing").type("habit_type").build();

                try {
                    // where is the client?
                    DocumentResult execute = client.execute(index);
                    if(execute.isSucceeded()) {
                        habit.setId(execute.getId());
                    }
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and add the habits");
                }
            }
            return null;
        }
    }

    public static class GetHabitTypeTask extends AsyncTask<String, Void, ArrayList<HabitType>> {
        @Override
        protected ArrayList<HabitType> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<HabitType> habits = new ArrayList<HabitType>();
            Log.d("Debugging", "Search Params: " + search_parameters[0]);
            if (search_parameters[0] == null) {
                String query = "{\"query\" : { \"match_all\" : {} } }";
            } else {
                String query = "{\"query\" : { \"term\" : { \"message\" : \"" + search_parameters[0] + "\"} } }";
            }

            Search search = new Search.Builder(query)
                    .addIndex("testing")
                    .addType("habit_type")
                    .build();
            try {

                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<HabitType> foundHabits = result.getSourceAsObjectList(HabitType.class);
                    habits.addAll(foundHabits);
                } else {
                    Log.e("Error", "the search query failed to find any habit types that matched");
                }
            } catch (Exception e) {
                Log.e("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return habits;
        }
    }




    public static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}
*/
