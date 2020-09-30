package com.ridowanahmed.childlocator.Model;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ridowan Ahmed on 0012, August, 12, 2017.
 */

public class ChildInformation {
    private static final String TAG = ChildInformation.class.getSimpleName();
    private String childName;
    private double latitude, longitude;
    private long time;
    private Double[] latlog;
    private String addressName;
    private GetAddress getAddress = new GetAddress();

    public ChildInformation() {
        Log.e(TAG, "Default");
    }

    public ChildInformation(String childName, double latitude, double longitude, long time) {
        this.childName = childName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.latlog = new Double[] {latitude, longitude};

        //this to set delegate/listener back to this class
        //execute the async task
        try {
            this.addressName = getAddress.execute(latlog).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Not Default");
    }

    public String getChildName() {
        return childName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTime() {
        return time;
    }

    public String getTimeString() {
        return DateFormat.getDateTimeInstance().format(new Date(getTime()));
    }

    public String getAddressName() {
        Log.e(TAG, addressName);
        return addressName;
    }

    @Override
    public String toString() {
        String info = getChildName() + "\nTime: " + getTimeString() + "\nAddress: " + getAddressName();
        return info;
    }

    private class GetAddress extends AsyncTask<Double, Void, String> {
        @Override
        protected void onPreExecute() {
//            Log.e(TAG, "onPreExecute");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Double...latlong) {
//            Log.e(TAG, "doInBackground");
            String response;
            try{
                HttpDataHandler http = new HttpDataHandler(latlong[0], latlong[1]);
                response = http.getHTTPData();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String formatted_address = ((JSONArray)jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString();
                    return formatted_address;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
//            Log.e(TAG, "onPostExecute");
            super.onPostExecute(s);
        }
    }
}
