package com.find.wifitool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.find.wifitool.internal.Constants;
import com.find.wifitool.internal.Utils;
import com.find.wifitool.wifi.WifiIntentReceiver;

/**
 * Created by akshay on 30/12/16.
 */

public class TrackFragment extends Fragment {

    private static final String TAG = TrackFragment.class.getSimpleName();

    //   TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    //private variables
    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private String strUsername;
    private String strServer;
    private String strGroup;
    private String strLocation = null;  // We don't need any location value fr Tracking
    private int trackVal;

    private TextView currLocView;

    Handler handler = new Handler();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Required empty public constructor
    public TrackFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Checking if the Location service is enabled in case of Android M or above users
        if (!Utils.isLocationAvailable(mContext)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Location service is not On. Users running Android M and above have to turn on location services for FIND to work properly");
            dialog.setPositiveButton("Enable Locations service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    logMeToast("Thank you!! Getting things in place.");
                }
            });
            dialog.show();
        }

        // Getting values from Shared prefs for Tracking
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        strGroup = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
        strUsername = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
        strServer = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
        trackVal = sharedPreferences.getInt(Constants.TRACK_INTERVAL, Constants.DEFAULT_TRACKING_INTERVAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        currLocView = (TextView)rootView.findViewById(R.id.labelLocationName);
        handler.post(runnableCode);

        // Listener to the broadcast message from WifiIntent
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.TRACK_BCAST));

        // Inflate the layout for this fragment
        return rootView;
    }

    // Getting the CurrentLocation from the received braodcast
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String currLocation = intent.getStringExtra("location");
            currLocView.setTextColor(getResources().getColor(R.color.currentLocationColor));
            currLocView.setText(currLocation);
        }
    };

    // Timers to keep track of our Tracking period
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= 23 ) {
                if(Utils.isWiFiAvailable(mContext) && Utils.hasAnyLocationPermission(mContext)) {
                    Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                    intent.putExtra("event", Constants.TRACK_TAG);
                    intent.putExtra("groupName", strGroup);
                    intent.putExtra("userName", strUsername);
                    intent.putExtra("serverName", strServer);
                    intent.putExtra("locationName", sharedPreferences.getString(Constants.LOCATION_NAME, ""));
                    mContext.startService(intent);
                }
            }
            else if (Build.VERSION.SDK_INT < 23) {
                if(Utils.isWiFiAvailable(mContext)) {
                    Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                    intent.putExtra("groupName", strGroup);
                    intent.putExtra("userName", strUsername);
                    intent.putExtra("serverName", strServer);
                    intent.putExtra("locationName", sharedPreferences.getString(Constants.LOCATION_NAME, ""));
                    mContext.startService(intent);
                }
            }
            else {
                return;
            }
            handler.postDelayed(runnableCode, trackVal * 1000);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler.removeCallbacks(runnableCode);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
        mListener = null;
    }

    // Logging message in form of Toasts
    private void logMeToast(String message) {
        Log.d(TAG, message);
        toast(message);
    }

    private void toast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }
}
