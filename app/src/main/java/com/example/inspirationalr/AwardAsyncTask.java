package com.example.inspirationalr;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.net.HttpURLConnection.HTTP_OK;

public class AwardAsyncTask   extends AsyncTask<String, Void, String> {
    private static final String TAG = "CreateProfileAsyncTask";
    private static final String baseUrl =
            "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String loginEndPoint ="/rewards";
    private AwardActivity awardActivity;

    public AwardAsyncTask(AwardActivity awardActivity) {
        this.awardActivity = awardActivity;
    }

    @Override
    protected void onPostExecute(String connectionResult) {
        awardActivity.redirectTOActivity(connectionResult);

    }


    @Override
    protected String doInBackground(String... params) {
        String targetStudentId = params[0];
        String targetUsername = params[1];
        String targetName = params[2];
        String targetDate = params[3];//.replace("/", "\\/");
        String targetNote = params[4];
        String targetValue = params[5];
        String userName = params[6];
        String password = params[7];
        String studentId = params[8];
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject target = new JSONObject();
            target.put("studentId", targetStudentId);
            target.put("username", targetUsername);
            target.put("name", targetName);
            target.put("date", targetDate);
            target.put("notes", targetNote);
            target.put("value", targetValue);
            JSONObject source = new JSONObject();
            source.put("studentId",studentId);
            source.put("username",userName);
            source.put("password",password);
            jsonObject.put("target", target);
            jsonObject.put("source", source);
            return doAPICall(jsonObject);
        } catch (Exception e) {

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
            connection.setRequestMethod("POST");  // POST - others might use PUT, DELETE, GET

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
                if(result.toString().equals("Reward Added Successfully\n"))
                    return "SUCCESS";
                return "Some error has occurred";

            } else {
                // Not HTTP_OK - some error occurred - use connection's getErrorStream
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                // Return the results (to onPostExecute)
//                parseJSON(result.toString());
                return "Some error has occurred";
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