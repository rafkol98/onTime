package com.example.ontime.MapRelatedClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ontime.DateTimeClasses.SelectTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**The instance of this class is called by "MainActivty". It gets the time taken reach the destination from Google Distance Matrix API in the background.
  This class contains interface "Geo" to call the function setDouble(String) defined in "MainActivity.class" to display the result. This class is essential as
 this is what allows me to calculate how many minutes the user needs to go from his current location to a destination based on his own unique speed.
 This is achieved with the implementation of the calculateTimeAndDist method in the desired class from the Geo interface.*/
public class GeoTask extends AsyncTask<String, Void, String> {
    ProgressDialog pd;
    Context mContext;

    SelectTime selectTime;
    Geo geo1;


    //constructor is used to get the context.
    public GeoTask(Context mContext) {
        this.mContext = mContext;
        geo1 = (Geo) mContext;
    }


    //This function is executed after the execution of "doInBackground(String...params)" to dismiss the dispalyed progress dialog and call "setDouble(Double)" defined in "MainActivity.java"
    @Override
    protected void onPostExecute(String aDouble) {
        super.onPostExecute(aDouble);
        if (aDouble != null) {
            geo1.calculateTimeAndDist(aDouble);
        } else {
            geo1.tripFromLocation();
        }
    }

    //gets duration and distance in the background. Remember, this is an AsyncTask so it doesnt run with the natural flow of the program. It runs in the background.
    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int statuscode = con.getResponseCode();
            if (statuscode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }
                String json = sb.toString();
                Log.d("JSON", json);
                JSONObject root = new JSONObject(json);
                JSONArray array_rows = root.getJSONArray("rows");
                Log.d("JSON", "array_rows:" + array_rows);
                JSONObject object_rows = array_rows.getJSONObject(0);
                Log.d("JSON", "object_rows:" + object_rows);
                JSONArray array_elements = object_rows.getJSONArray("elements");
                Log.d("JSON", "array_elements:" + array_elements);
                JSONObject object_elements = array_elements.getJSONObject(0);
                Log.d("JSON", "object_elements:" + object_elements);
                JSONObject object_duration = object_elements.getJSONObject("duration");
                JSONObject object_distance = object_elements.getJSONObject("distance");

                Log.d("JSON", "object_duration:" + object_duration);
                return object_duration.getString("value") + "," + object_distance.getString("value");

            }
        } catch (MalformedURLException e) {
            Log.d("error", "error1");
        } catch (IOException e) {
            Log.d("error", "error2");
        } catch (JSONException e) {
            Log.d("error", "error3");
        }


        return null;
    }


    interface Geo {
        public void calculateTimeAndDist(String min);

        public void tripFromLocation();
    }

}
