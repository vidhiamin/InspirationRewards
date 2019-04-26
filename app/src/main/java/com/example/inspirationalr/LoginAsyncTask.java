package com.example.inspirationalr;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.net.HttpURLConnection.HTTP_OK;

public class LoginAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "LoginAPIAyncTask";
    private static final String baseUrl =
            "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String loginEndPoint ="/login";
    private UserDetails userDetails;
    private String status = "SUCCESS";
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private  ViewProfileActivity viewProfileActivity;
    private EditProfileActivity editProfileActivity;
    public LoginAsyncTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public LoginAsyncTask(ViewProfileActivity viewProfileActivity) {
        this.viewProfileActivity = viewProfileActivity;
    }

    public LoginAsyncTask(EditProfileActivity editProfileActivity) {
        this.editProfileActivity = editProfileActivity;
    }

    @Override
    protected void onPostExecute(String connectionResult) {
        if(mainActivity != null)
            mainActivity.redirectTOActivity(userDetails, status);
        else if(viewProfileActivity != null)
            viewProfileActivity.resetUserDetails(userDetails, status);
        else if(editProfileActivity != null)
            editProfileActivity.resetUserDetails(userDetails, status);
    }

    @Override
    protected String doInBackground(String... strings) {
        String stuId = "A20413890";
        String uName = strings[0];
        String pswd = strings[1];

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentId", stuId);
            jsonObject.put("username", uName);
            jsonObject.put("password", pswd);
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
                parseJSON(result.toString());
                return null;

            } else {
                // Not HTTP_OK - some error occurred - use connection's getErrorStream
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                JSONObject j = new JSONObject(result.toString());
                JSONObject err = j.getJSONObject("errordetails");
                String s = err.getString("status");
                if(s.equalsIgnoreCase("UNAUTHORIZED")) {
                    status = "FAIL";
                }


                // Return the results (to onPostExecute)
//                parseJSON(result.toString());
                return null;
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


    private void parseJSON(String s) {

        // Parse out the useful data = put them in a HashMap
        // https://openweathermap.org/current

        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray rewardsArr = jObjMain.getJSONArray("rewards");
            List<RewardsDetails> rArr = new ArrayList<>();
            for (int i = 0; i < rewardsArr.length(); i++){
                JSONObject o = (JSONObject) rewardsArr.get(i);
                //String historyDate, String rewardProvider, String points, String comments
                rArr.add(new RewardsDetails(
                        o.getString("studentId"),
                        o.getString("username"),
                        o.getString("date"),
                        o.getString("name"),
                        o.getString("value"),
                        o.getString("notes")
                ));
            }
//            bitmap = BitmapFactory.decodeStream(jObjMain.getString("imageBytes"));
            userDetails = new UserDetails(
                    jObjMain.getString("studentId"),
                    jObjMain.getString("firstName"),
                    jObjMain.getString("lastName"),
                    jObjMain.getString("username"),
                    jObjMain.getString("department"),
                    jObjMain.getString("story"),
                    jObjMain.getString("position"),
                    jObjMain.getString("password"),
                    Integer.parseInt(jObjMain.getString("pointsToAward")),
                    jObjMain.getString("admin").equalsIgnoreCase("true"),
                    jObjMain.getString("location"),
                    jObjMain.getString("imageBytes"),
                    rArr
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}