package com.example.inspirationalr;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class EditProfileAsyncTask  extends AsyncTask<String, Void, String> {
    private static final String TAG = "CreateProfileAsyncTask";
    private static final String baseUrl =
            "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String loginEndPoint ="/profiles";
    private UserDetails userDetails;
    private EditProfileActivity editProfileActivity;

    public EditProfileAsyncTask(UserDetails userDetails, EditProfileActivity editProfileActivity) {
        this.userDetails = userDetails;
        this.editProfileActivity = editProfileActivity;
    }

    @Override
    protected void onPostExecute(String connectionResult) {
        editProfileActivity.redirectTOActivity(connectionResult);
    }


    @Override
    protected String doInBackground(String... strings) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentId", userDetails.getStudentID());
            jsonObject.put("username", userDetails.getUsername());
            jsonObject.put("password", userDetails.getPassword());

            jsonObject.put("firstName", userDetails.getFirstname());
            jsonObject.put("lastName", userDetails.getLastname());

            jsonObject.put("pointsToAward", userDetails.getPointstoaward());
            jsonObject.put("department", userDetails.getDepartment());
            jsonObject.put("story", userDetails.getStory());
            jsonObject.put("position", userDetails.getPosition());
            jsonObject.put("admin", userDetails.getAdmin());
            jsonObject.put("location", userDetails.getLocation());
            jsonObject.put("imageBytes", userDetails.getImage());
            JSONArray rewards = new JSONArray();
            //cerate array object
            for(RewardsDetails r : userDetails.getRewards()){
                JSONObject rr = new JSONObject();
//                rr.put("studentId", r.getStudentID());
//                rr.put("username", r.getUserName());
                rr.put("name", r.getRewardProvider());
                rr.put("date", r.getHistoryDate());
                rr.put("notes", r.getComments());
                rr.put("value", r.getPoints());
                rewards.put(rr);
            }
            jsonObject.put("rewardRecords", rewards);

            return doAPICall(jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String doAPICall(JSONObject jsonObject) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            String urlString = baseUrl + loginEndPoint;  // Build the full URL

            Uri uri = Uri.parse(urlString);    // Convert String url to URI
            URL url = new URL(uri.toString()); // Convert URI to URL

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");  // POST - others might use PUT, DELETE, GET

            // Set the Content-Type and Accept properties to use JSON data
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("cache-control", "no-cache");
            connection.connect();

            // Write the JSON (as String) to the open connection
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();

            // If successful (HTTP_OK)
            if (responseCode == HTTP_OK) {

                // Read the results - use connection's getInputStream
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                // Return the results (to onPostExecute)
                Log.d(TAG, "doAPICall: " + result.toString());
//                parseJSON(result.toString());
                if(result.toString().equals("Profile Updated Successfully\n")){
                    return "SUCCESS";
                } else
                    return "Some error has occurred"; // Return an error message if Exception occurred

            } else {
                // Not HTTP_OK - some error occurred - use connection's getErrorStream
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                // Return the results (to onPostExecute)
//                parseJSON(result.toString());
                return "Some error has occurred"; // Return an error message if Exception occurred
            }

        } catch (Exception e) {
            // Some exception occurred! Log it.
            Log.d(TAG, "doAuth: " + e.getClass().getName() + ": " + e.getMessage());

        } finally { // Close everything!
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        return "Some error has occurred"; // Return an error message if Exception occurred
    }
}