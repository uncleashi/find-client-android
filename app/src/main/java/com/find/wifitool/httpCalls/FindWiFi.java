package com.find.wifitool.httpCalls;

import com.squareup.okhttp.Callback;

import org.json.JSONObject;

/**
 * Created by akshay on 30/12/16.
 */

/**
 * Asynchronous HTTP library for the Find API.
 */
public interface FindWiFi {

    /**
     * Track
     */
    void findTrack(Callback callback, String serverAddr, JSONObject requestBody);

    /**
     * Learn
     */
    void findLearn(Callback callback, String serverAddr, JSONObject requestBody);
}
