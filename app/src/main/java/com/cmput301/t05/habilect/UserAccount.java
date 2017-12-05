package com.cmput301.t05.habilect;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

/**
 * Class that tracks everything that has to do with the user
 */
public class UserAccount {
    private static JestDroidClient client;

    /**
     * Initialize and setup objects if needed
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

    private UUID Id;
    private String DisplayName;
    private String ProfilePicture;
    private List<UUID> Followees;
    private List<UUID> Followers;

    private List<HabitType> Habits;

    private TreeGrowth treeGrowth;

    public Boolean Exists = false;

    UserAccount() {
        verifySettings();
    }

    /**
     * Syncs all property data with Elastic Search
     */
    public static class syncTask extends AsyncTask<UserAccount, Void, Void> {
        @Override
        protected Void doInBackground(UserAccount... userAccounts) {
            Map<String, Object> source = new LinkedHashMap<String,Object>();

            source.put("id", userAccounts[0].Id.toString());

            if (userAccounts[0].DisplayName!=null) {
                source.put("display_name", userAccounts[0].DisplayName.toString());
            }
            else {
                source.put("display_name", "");
            }

            if (userAccounts[0].ProfilePicture!=null) {
                source.put("profile_picture", userAccounts[0].ProfilePicture);
            }
            else {
                source.put("profile_picture", null);
            }

            List<String> followees = new ArrayList<>();
            if (userAccounts[0].Followees!=null) {
                for (UUID followeeId : userAccounts[0].Followees) {
                    followees.add(followeeId.toString());

                }
            }

            source.put("followees", followees);
            List<String> followers = new ArrayList<>();
            if (userAccounts[0].Followers!=null) {
                for (UUID followerId : userAccounts[0].Followers) {
                    followers.add(followerId.toString());

                }
            }
            source.put("followers", followers);

            List<HabitType> habits = new ArrayList<>();
            if (userAccounts[0].Habits!=null) {
                for (HabitType habitId : userAccounts[0].Habits) {
                    habits.add(habitId);
                }
            }
            source.put("habits", habits);

            if (userAccounts[0].treeGrowth!=null) {
                source.put("nutrientLevel",userAccounts[0].treeGrowth.getNutrientLevel());
                source.put("previousNutrientLevelTierRankUp",userAccounts[0].treeGrowth.getPreviousNutrientLevelTierRankUp());
            }
            else {
                source.put("nutrientLevel",0);
                source.put("previousNutrientLevelTierRankUp",0);
            }

            try {
                DocumentResult result =  client.execute(new Update.Builder(source).index("user").id(userAccounts[0].Id.toString()).build());
                if (!result.isSucceeded()) {
                    result =  client.execute(new Index.Builder(source).index("user").id(userAccounts[0].Id.toString()).build());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Initialize User Properties
     */
    public void init() {
        Id = UUID.randomUUID();
        Followees = new ArrayList<UUID>();
        Followers = new ArrayList<UUID>();
        Habits = new ArrayList<HabitType>();
        treeGrowth = new TreeGrowth();
    }

    /**
     * Save user property data locally to file
     */
    public UserAccount save(Context context) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("userData", Context.MODE_PRIVATE);
        File path = new File(directory, "LocalUserAccount.sav");
        FileWriter fw = null;
        try {
            fw = new FileWriter(path);
            BufferedWriter out = new BufferedWriter(fw);

            Gson gson = new Gson();
            String json = gson.toJson(this);
            out.write(json);

            out.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Loads user property data through file and deserializes with gson
     */
    public UserAccount load(Context context) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("userData", Context.MODE_PRIVATE);
        File path = new File(directory, "LocalUserAccount.sav");
        FileReader fr = null;
        try {
            fr = new FileReader(path);
            BufferedReader in = new BufferedReader(fr);

            Gson gson = new Gson();
            UserAccount info = gson.fromJson(in, UserAccount.class);
            this.Id = info.Id;
            this.DisplayName = info.DisplayName;
            this.ProfilePicture = info.ProfilePicture;
            this.Followees = info.Followees;
            this.Followers = info.Followers;
            this.Habits = info.Habits;

            this.Exists = true;

            this.treeGrowth = info.treeGrowth;

            fr.close();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static List<UserAccount> findSimilarDisplayNames(String displayName) {
        SearchResult result = null;
        List<UserAccount> userAccountListResult = new ArrayList<UserAccount>();
        try {
            result = new findSimilarDisplayNamesTask().execute(displayName).get();
            if (result!=null) {
                JsonObject json = result.getJsonObject();
                JsonObject hits = json.getAsJsonObject("hits");
                JsonArray hitArray = hits.getAsJsonArray("hits");
                for (Object hit : hitArray) {
                    if (hit instanceof JsonObject) {
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                        JsonObject object = (JsonObject) hit;
                        JsonObject source = object.getAsJsonObject("_source");
                        UserAccount user = new UserAccount();
                        user.Followees = new ArrayList<UUID>();
                        user.Followers = new ArrayList<UUID>();
                        user.Habits = new ArrayList<HabitType>();
                        user.treeGrowth = new TreeGrowth();
                        for (Map.Entry<String, JsonElement> e : source.entrySet()) {
                            switch (e.getKey()) {
                                case "id":
                                    user.Id = UUID.fromString(e.getValue().getAsString());
                                    break;
                                case "display_name":
                                    user.DisplayName = e.getValue().getAsString();
                                    break;
                                case "profile_picture":
                                    user.ProfilePicture = e.getValue().getAsString();
                                    break;
                                case "followees":
                                    List<UUID> followees = new ArrayList<UUID>();
                                    for (JsonElement element : e.getValue().getAsJsonArray()) {
                                        followees.add(gson.fromJson(element.getAsString(), UUID.class));
                                    }
                                    user.Followees = followees;
                                    break;
                                case "followers":
                                    List<UUID> followers = new ArrayList<UUID>();
                                    for (JsonElement element : e.getValue().getAsJsonArray()) {
                                        followers.add(gson.fromJson(element.getAsString(), UUID.class));
                                    }
                                    user.Followers = followers;
                                    break;
                                case "habits":
                                    List<HabitType> habits = new ArrayList<HabitType>();
                                    for (JsonElement element : e.getValue().getAsJsonArray()) {
                                        JsonElement events = element.getAsJsonObject().get("habitEvents");
                                        List<Date> dates = new ArrayList<Date>();
                                        List<Location> locations = new ArrayList<Location>();
                                        for (JsonElement member : events.getAsJsonArray()) {
                                            JsonElement date = member.getAsJsonObject().get("completionDate");
                                            if (date!=null) {
                                                String stringDate = date.getAsString();
                                                Date parsedDate = null;
                                                try {
                                                    parsedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA).parse(stringDate);
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }
                                                dates.add(parsedDate);
                                            }
                                            JsonElement location = member.getAsJsonObject().get("location");
                                            if (location!=null) {
                                                float latitude = Float.parseFloat(location.getAsJsonObject().get("mLatitude").getAsString());
                                                float longitude = Float.parseFloat(location.getAsJsonObject().get("mLongitude").getAsString());
                                                Location loc = new Location("");
                                                loc.setLatitude(latitude);
                                                loc.setLongitude(longitude);
                                                locations.add(loc);
                                            }

                                        }
                                        HabitType intermediateHabit = gson.fromJson(element.getAsJsonObject(), HabitType.class);
                                        for (int i = dates.size(); i<dates.size(); i++) {
                                            intermediateHabit.getHabitEvents().get(i).setCompletionDate(dates.get(i));
                                            intermediateHabit.getHabitEvents().get(i).setLocation(locations.get(i));
                                        }
                                        habits.add(intermediateHabit);
                                    }
                                    user.Habits = habits;
                                    break;
                                case "nutrientLevel":
                                    user.treeGrowth.setNutrientLevel(e.getValue().getAsInt());
                                    break;
                                case "previousNutrientLevelTierRankUp":
                                    user.treeGrowth.setPreviousNutrientLevelTierRankUp(e.getValue().getAsInt());
                                    break;
                            }
                        }
                        user.Exists = true;
                        userAccountListResult.add(user);
                    }
                }
                return userAccountListResult;
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class findSimilarDisplayNamesTask extends AsyncTask<String, Void, SearchResult> {
        @Override
        protected SearchResult doInBackground(String... displayNames) {
            String source = "{\n" + " \"query\": { \"match\": {\"display_name\":\".*" + displayNames[0].toString() + ".*\"} }\n" + "}";

            Search search = new Search.Builder(source)
                    .addIndex("user")
                    .build();
            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Execute sync task
     */
    public UserAccount sync(Context context) {
        new syncTask().execute(this);
        return this;
    }

    /**
     * Search task from user id
     */
    private static class fromIdTask extends AsyncTask<UUID, Void, SearchResult> {
        @Override
        protected SearchResult doInBackground(UUID... uuids) {
            String source = "{\n" + " \"query\": { \"match\": {\"id\":\"" + uuids[0].toString() + "\"} }\n" + "}";

            Search search = new Search.Builder(source)
                    .addIndex("user")
                    .build();
            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Gets and assigns the property values
     */
    public static UserAccount fromId(UUID id) {
        SearchResult result = null;
        try {
            result = new fromIdTask().execute(id).get();
            if (result!=null) {
                JsonObject json = result.getJsonObject();
                JsonObject hits = json.getAsJsonObject("hits");
                JsonArray hitArray = hits.getAsJsonArray("hits");
                for (Object hit : hitArray) {
                    if (hit instanceof JsonObject) {
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                        JsonObject object = (JsonObject) hit;
                        JsonObject source = object.getAsJsonObject("_source");
                        UserAccount user = new UserAccount();
                        user.Followees = new ArrayList<UUID>();
                        user.Followers = new ArrayList<UUID>();
                        user.Habits = new ArrayList<HabitType>();
                        user.treeGrowth = new TreeGrowth();
                        for (Map.Entry<String, JsonElement> e : source.entrySet()) {
                            switch (e.getKey()) {
                                case "id":
                                    user.Id = UUID.fromString(e.getValue().getAsString());
                                    break;
                                case "display_name":
                                    user.DisplayName = e.getValue().getAsString();
                                    break;
                                case "profile_picture":
                                    user.ProfilePicture = e.getValue().getAsString();
                                    break;
                                case "followees":
                                    List<UUID> followees = new ArrayList<UUID>();
                                    for (JsonElement element : e.getValue().getAsJsonArray()) {
                                        followees.add(gson.fromJson(element.getAsString(), UUID.class));
                                    }
                                    user.Followees = followees;
                                    break;
                                case "followers":
                                    List<UUID> followers = new ArrayList<UUID>();
                                    for (JsonElement element : e.getValue().getAsJsonArray()) {
                                        followers.add(gson.fromJson(element.getAsString(), UUID.class));
                                    }
                                    user.Followers = followers;
                                    break;
                                case "habits":
                                    List<HabitType> habits = new ArrayList<HabitType>();
                                    for (JsonElement element : e.getValue().getAsJsonArray()) {
                                        JsonElement events = element.getAsJsonObject().get("habitEvents");
                                        List<Date> dates = new ArrayList<Date>();
                                        List<Location> locations = new ArrayList<Location>();
                                        for (JsonElement member : events.getAsJsonArray()) {
                                            JsonElement date = member.getAsJsonObject().get("completionDate");
                                            if (date!=null) {
                                                String stringDate = date.getAsString();
                                                Date parsedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA).parse(stringDate);
                                                dates.add(parsedDate);
                                            }
                                            JsonElement location = member.getAsJsonObject().get("location");
                                            if (location!=null) {
                                                float latitude = Float.parseFloat(location.getAsJsonObject().get("mLatitude").getAsString());
                                                float longitude = Float.parseFloat(location.getAsJsonObject().get("mLongitude").getAsString());
                                                Location loc = new Location("");
                                                loc.setLatitude(latitude);
                                                loc.setLongitude(longitude);
                                                locations.add(loc);
                                            }

                                        }
                                        HabitType intermediateHabit = gson.fromJson(element.getAsJsonObject(), HabitType.class);
                                        for (int i = dates.size(); i<dates.size(); i++) {
                                            intermediateHabit.getHabitEvents().get(i).setCompletionDate(dates.get(i));
                                            intermediateHabit.getHabitEvents().get(i).setLocation(locations.get(i));
                                        }
                                        habits.add(intermediateHabit);
                                    }
                                    user.Habits = habits;
                                    break;
                                case "nutrientLevel":
                                    user.treeGrowth.setNutrientLevel(e.getValue().getAsInt());
                                    break;
                                case "previousNutrientLevelTierRankUp":
                                    user.treeGrowth.setPreviousNutrientLevelTierRankUp(e.getValue().getAsInt());
                                    break;
                            }
                        }
                        user.Exists = true;
                        return user;
                    }
                }
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the Id to a passed in UUID
     */
    public void setId(UUID id) {
        Id = id;
    }

    /**
     * Sets the display name to a specified string
     */
    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    /**
     * Sets the profile picture to a specified image
     */
    public void setProfilePicture(Bitmap profilePicture) {
        if (profilePicture!=null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            profilePicture.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedString = Base64.encodeToString(byteArray, Base64.URL_SAFE | Base64.NO_WRAP);
            ProfilePicture = encodedString;
        }
    }

    /**
     *
     * @return the user id
     */
    public UUID getId() {
        return Id;
    }

    /**
     *
     * @return the user's display name
     */
    public String getDisplayName() {
        return DisplayName;
    }

    /**
     *
     * @return the user's profile picture
     */
    public Bitmap getProfilePicture() {
        if (ProfilePicture!=null) {
            byte[] decodedByteArray = Base64.decode(ProfilePicture, Base64.URL_SAFE | Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        }
        return null;
    }

    /**
     *
     * @return a list of followees
     */
    public List<UserAccount> getFollowees() {
        List<UserAccount> followees = new ArrayList<UserAccount>();
        for (UUID id : Followees) {
            UserAccount followee = UserAccount.fromId(id);
            if (followee!=null) {
                followees.add(followee);
            }
        }
        return followees;
    }

    /**
     *
     * @return a list of followers
     */
    public List<UserAccount> getFollowers() {
        List<UserAccount> followers = new ArrayList<UserAccount>();
        for (UUID id : Followers) {
            UserAccount follower = UserAccount.fromId(id);
            if (follower!=null){
                followers.add(follower);
            }
        }
        return followers;
    }

    /**
     * Adds a followee by their user id
     */
    public void addFollowee(UUID id) {
        if (!Followees.contains(id)) {
            this.Followees.add(id);
        }
    }

    /**
     * Adds a follower by their user id
     */
    public void addFollower(UUID id) {
        if (!Followers.contains(id)) {
            this.Followers.add(id);
        }
    }

    /**
     * Removes a followee by their user id
     */
    public void removeFollowee(UUID id) {
        if (Followees.contains(id)) {
            this.Followees.remove(id);
        }
    }

    /**
     * Removes a follower by their user id
     */
    public void removeFollower(UUID id) {
        if (Followers.contains(id)) {
            this.Followers.remove(id);
        }
    }

    /**
     * Sets the habits based on a passed in list of habit types
     */
    public void setHabits(List<HabitType> habits) {
        Habits = habits;
    }

    /**
     *
     * @return a list of habit types
     */
    public List<HabitType> getHabits() {
        return Habits;
    }

    /**
     *
     * @return a TreeGrowth object
     */
    public TreeGrowth getTreeGrowth() {
        return treeGrowth;
    }
}