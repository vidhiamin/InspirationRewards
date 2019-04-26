package com.example.inspirationalr;

import android.annotation.SuppressLint;
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

public class LeaderBoardAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "LeaderBoardAsyncTask";
    private static final String baseUrl =
            "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String loginEndPoint ="/allprofiles";
    //    private String responseObject = "";
    private List<UserDetails> uList = new ArrayList<>();
    private String stuId = "A20413890";

    private LeaderBoardActivity leaderBoardActivity;

    public LeaderBoardAsyncTask(LeaderBoardActivity leaderBoardActivity) {
        this.leaderBoardActivity = leaderBoardActivity;
    }

    @Override
    protected void onPostExecute(String connectionResult) {
        leaderBoardActivity.setLeaderBoardParams(uList);
    }

    @Override
    protected String doInBackground(String... strings) {
//        stuId = strings[0];
        String uName = strings[1];
        String pswd = strings[2];

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
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
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

                // Return the results (to onPostExecute)
                Log.d(TAG, "doAPICall: " + result.toString());
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
            JSONArray jsonArray = new JSONArray(s);
//            JSONArray jj = new JSONObject(s);
            for(int j = 0; j < jsonArray.length(); j++) {
                JSONObject jObjMain = (JSONObject) jsonArray.get(j);
                List<RewardsDetails> rArr = new ArrayList<>();
                if (jObjMain.optJSONArray("rewards") != null) {
                    JSONArray rewardsArr = jObjMain.getJSONArray("rewards");
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
                }

                UserDetails userDetails = new UserDetails(
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
                uList.add(userDetails);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
