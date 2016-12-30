package com.find.wifitool.wifi;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.find.wifitool.httpCalls.FindWiFi;
import com.find.wifitool.httpCalls.FindWiFiImpl;
import com.find.wifitool.internal.Constants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by akshay on 30/12/16.
 */

public class WifiIntentReceiver extends IntentService {

    private static final String TAG = WifiIntentReceiver.class.getSimpleName();

    // private variables
    private WifiManager mWifiManager;
    private WifiData mWifiData;
    private FindWiFi client;
    private String eventName, userName, groupName, serverName, locationName;
    private JSONObject wifiFingerprint;
    private String currLocation;

    private static final Set<Character> AD_HOC_HEX_VALUES =
            new HashSet<Character>(Arrays.asList('2','6', 'a', 'e', 'A', 'E'));

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public WifiIntentReceiver() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        client = new FindWiFiImpl(getApplicationContext());

        // Getting all the value passed from previous Fragment
        eventName = intent.getStringExtra("event");
        userName = intent.getStringExtra("userName");
        groupName = intent.getStringExtra("groupName");
        serverName = intent.getStringExtra("serverName");
        locationName = intent.getStringExtra("locationName");
        Long timeStamp = System.currentTimeMillis()/1000;

        // getting all wifi APs and forming data payload
        try {
            mWifiData = new WifiData();
            mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            JSONObject wifiResults = new JSONObject();
            JSONArray wifiResultsArray = new JSONArray();
            List<ScanResult> mResults = mWifiManager.getScanResults();

            for (ScanResult result : mResults) {
                if (shouldLog(result)) {
                    wifiResults.put("mac", result.BSSID);
                    wifiResults.put("rssi", result.level);
                }
                wifiResultsArray.put(wifiResults);
            }
            wifiFingerprint = new JSONObject();
            wifiFingerprint.put("group", groupName);
            wifiFingerprint.put("username", userName);
            wifiFingerprint.put("location", locationName);
            wifiFingerprint.put("time", timeStamp);
            wifiFingerprint.put("wifi-fingerprint", wifiResultsArray);
            Log.d(TAG, String.valueOf(wifiFingerprint));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Send the packet to server
        sendPayload(eventName, serverName, wifiFingerprint);

    }

    // Function to check to check the route(learn or track) and send data to server
    private void sendPayload(String event, String serverName, JSONObject json) {

        if(event.equalsIgnoreCase("track")) {
            Callback postTrackEvent = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, "Failed request: " + request, e);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String body = response.body().string();
                    if (response.isSuccessful()) {
                        Log.d(TAG, body);
                        try {
                            JSONObject json = new JSONObject(body);
                            currLocation = json.getString("location");
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to extract location from response: " + body, e);
                        }
                        sendCurrentLocation(currLocation);
                    } else {
                        Log.e(TAG, "Unsuccessful request: " + body);
                    }
                }
            };
            client.findTrack(postTrackEvent, serverName, json);

        } else {
            Callback postLearnEvent = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, "Failed request: " + request, e);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String body = response.body().string();
                    Log.d(TAG, body);
                }
            };
            client.findLearn(postLearnEvent, serverName, json);

        }

    }

    // Broadcasting current location extracted from Response
    private void sendCurrentLocation(String location) {
        if (location != null && !location.isEmpty()) {
            Intent intent = new Intent(Constants.TRACK_BCAST);
            intent.putExtra("location", location);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    /**
     * Returns true if the given scan should be logged, or false if it is an
     * ad-hoc AP or if it is an AP that has opted out of Google's collection
     * practices.
     */
    private static boolean shouldLog(final ScanResult sr) {
        // Only apply this test if we have exactly 17 character long BSSID which should
        // be the case.
        final char secondNybble = sr.BSSID.length() == 17 ? sr.BSSID.charAt(1) : ' ';

        if(AD_HOC_HEX_VALUES.contains(secondNybble)) {
            return false;
        }
        else {
            return true;
        }
    }
}
