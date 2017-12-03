package com.cmput301.t05.habilect;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * Web service class to interact with elastic search using Jest.
 */
public class WebService {
    private static JestDroidClient client;

    /**
     * Ensures that the Jest client is setup properly and ready.
     */
    public static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080/cmput301f17t05_habilect");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
    /**
     * Gets the unique _id identifier generated by Elastic Search based on the userProfile's unique identifier.
     *
     * @param userProfile                 userProfile to query on
     */
    public static String getDocumentIdFromUserProfile(UserProfile userProfile) {
        String query = "{\n" + " \"query\": { \"term\": {\"identifier\":\"" + userProfile.getIdentifier() + "\"} }\n" + "}";

        Search search = new Search.Builder(query)
                .addIndex("user")
                .build();
        try {
            SearchResult result = client.execute(search);
            if (result.isSucceeded()) {
                SearchResult.Hit hit = result.getFirstHit(Map.class);
                Map source = (Map) hit.source;

                return (String) source.get(JestResult.ES_METADATA_ID);
            }
        } catch (Exception e) {
            Log.i("Error", "Error grabbing id");
        }
        return null;
    }
    /**
     * Gets the list of HabitTypes belonging to a particular userProfile
     *
     * @param userProfile                 userProfile to query on
     */
    public static List<HabitType> getListOfHabitTypes(UserProfile userProfile) {
        String query = "{\n" + " \"query\": { \"term\": {\"identifier\":\"" + userProfile.getIdentifier() + "\"} }\n" + "}";

        Search search = new Search.Builder(query)
                .addIndex("user")
                .build();
        try {
            SearchResult result = client.execute(search);
            if (result.isSucceeded()) {
                List<SearchResult.Hit<UserProfile, Void>> hits = result.getHits(UserProfile.class);
                List<HabitType> listOfHabitTypes = hits.stream().flatMap(m -> m.source.getHabitTypesList().stream()).collect(Collectors.toList());
                Log.i("Success", "Success retrieving habitTypes");
                return listOfHabitTypes;
            }
        } catch (Exception e) {
            Log.i("Error", "Error retrieving habitTypes");
        }
        return null;
    }

    //region UserProfile services
    /**
     * Task that adds a new user profile to elastic search.
     */
    public static class AddUserProfileTask extends AsyncTask<UserProfile, Void, Void> {

        @Override
        protected Void doInBackground(UserProfile... userProfile) {
            verifySettings();

            userProfile[0].setContext();
            Index index = new Index.Builder(userProfile[0]).index("user").build();

            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    Log.i("Success", "Success creating");
                } else {
                    Log.i("Error", "Error creating");
                }
            } catch (Exception e) {
                Log.i("Error", "");
            }
            return null;
        }
    }
    /**
     * Task that updates a user profile to elastic search.
     */
    public static class UpdateUserProfileTask extends AsyncTask<UserProfile, Void, Void> {

        @Override
        protected Void doInBackground(UserProfile... userProfile) {
            verifySettings();

            try {
                String documentId = getDocumentIdFromUserProfile(userProfile[0]);
                if (documentId != null) {
                    Index index = new Index.Builder(userProfile[0]).index("user").type(documentId).build();
                    DocumentResult resultUpdate = client.execute(index);
                    if (resultUpdate.isSucceeded()) {
                        Log.i("Success", "Success updating");
                    } else {
                        Log.i("Error", "Error updating");
                    }
                } else {
                    Log.i("Error", "");
                }

            } catch (Exception e) {
                Log.i("Error", "");
            }
            return null;
        }
    }
    /**
     * Task that deletes a user profile to elastic search.
     */
    public static class DeleteUserProfileTask extends AsyncTask<UserProfile, Void, Void> {

        @Override
        protected Void doInBackground(UserProfile... userProfile) {
            verifySettings();

            try {
                String documentId = getDocumentIdFromUserProfile(userProfile[0]);
                if (documentId != null) {
                    Delete index = new Delete.Builder(documentId).index("user").build();
                    DocumentResult resultUpdate = client.execute(index);
                    if (resultUpdate.isSucceeded()) {
                        Log.i("Success", "Success deleting");
                    } else {
                        Log.i("Error", "Error deleting");
                    }
                } else {
                    Log.i("Error", "Error deleting");
                }
            } catch (Exception e) {
                Log.i("Error", "");
            }
            return null;
        }
    }
    //endregion
    /**
     * Task that gets the habit types of a user profile with elastic search.
     */
    public static class GetHabitTypesTask extends AsyncTask<UserProfile, Void, ArrayList<HabitType>> {

        ArrayList<HabitType> habitTypes = new ArrayList<HabitType>();

        @Override
        protected ArrayList<HabitType> doInBackground(UserProfile... userProfile) {
            verifySettings();


            try {
                List<HabitType> listOfHabitTypes = getListOfHabitTypes(userProfile[0]);
                habitTypes.addAll(listOfHabitTypes);
            } catch (Exception e) {
                Log.i("Error", "");
            }
            return habitTypes;
        }

        @Override
        protected void onPostExecute(ArrayList<HabitType> habitTypesList){
            habitTypes = habitTypesList;
        }
        //Call this method after execute to return list of habit types
        public ArrayList<HabitType> getHabitTypes(){
            return habitTypes;
        }
    }

    //region UserProfile services
    /**
     * Task that adds a new user profile to elastic search.
     */
    public static class AddHabitTypeTask extends AsyncTask<UserProfile, Void, Void> {

        @Override
        protected Void doInBackground(UserProfile... userProfile) {
            verifySettings();

            userProfile[0].setContext();
            Index index = new Index.Builder(userProfile[0]).index("user").build();

            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    Log.i("Success", "Success creating");
                } else {
                    Log.i("Error", "Error creating");
                }
            } catch (Exception e) {
                Log.i("Error", "");
            }
            return null;
        }
    }
}
