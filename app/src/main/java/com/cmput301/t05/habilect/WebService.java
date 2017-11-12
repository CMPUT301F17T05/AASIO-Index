package com.cmput301.t05.habilect;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * Created by Steven C on 11/10/2017.
 */
public class WebService {
    private static JestDroidClient client;

    public static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080/cmput301f17t05_habilect");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }

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

    //region UserProfile services
    public static class AddUserProfileTask extends AsyncTask<UserProfile, Void, Void> {

        @Override
        protected Void doInBackground(UserProfile... userProfile) {
            verifySettings();

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
}
