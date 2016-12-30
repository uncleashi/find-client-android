package com.find.wifitool.httpCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

/**
 * Created by akshay on 30/12/16.
 */

public class FindWiFiImpl implements FindWiFi {

    private static final String TAG = FindWiFiImpl.class.getSimpleName();
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    // private variables
    private static final int GET = 0;
    private static final int PUT = 1;
    private static final int POST = 2;
    private static final int DELETE = 3;

    private final Context ctx;
    private final OkHttpClient httpClient;

    // Constructor
    public FindWiFiImpl(Context context) {
        this.ctx = context;
        this.httpClient = new OkHttpClient();
    }

    @Override
    public void findTrack(Callback callback, String serverAddr, JSONObject requestBody) {
        new AuthTask("track", serverAddr, POST, requestBody.toString(), callback).execute();
    }

    @Override
    public void findLearn(Callback callback, String serverAddr, JSONObject requestBody) {
        new AuthTask("learn", serverAddr, POST, requestBody.toString(), callback).execute();
    }

    private class AuthTask extends AsyncTask<Void, Void, Void> {
        private final String urlPart;
        private final int method;
        private final String json;
        private final String serverAddr;
        private final Callback callback;

        AuthTask(String urlPart, String serverAddr, int method, String json, Callback callback) {
            this.urlPart = urlPart;
            this.serverAddr = serverAddr;
            this.method = method;
            this.json = json;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Request.Builder requestBuilder = new Request.Builder()
                        .url(serverAddr + urlPart);
                switch (method) {
                    case PUT:
                        requestBuilder.put(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    case POST:
                        requestBuilder.post(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    case DELETE:
                        requestBuilder.delete(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    default: break;
                }
                Request request = requestBuilder.build();
                httpClient.newCall(request).enqueue(new HttpCallback(callback));
            } catch (Exception e) {
                Log.e(TAG, "IOException", e);
            }
            return null;
        }
    }
}
