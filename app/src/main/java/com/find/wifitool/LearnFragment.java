package com.find.wifitool;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.find.wifitool.OnFragmentInteractionListener;
import com.find.wifitool.database.Event;
import com.find.wifitool.database.InternalDataBase;
import com.find.wifitool.internal.Constants;
import com.find.wifitool.internal.Utils;
import com.find.wifitool.wifi.WifiArrayAdapter;
import com.find.wifitool.wifi.WifiIntentReceiver;
import com.find.wifitool.wifi.WifiObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay on 30/12/16.
 */

public class LearnFragment extends Fragment {

    private static final String TAG = LearnFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    //private variables
    private Context mContext = getActivity();
    private OnFragmentInteractionListener mListener;
    Handler handler = new Handler();

    private SharedPreferences sharedPreferences;
    private String strUsername;
    private String strServer;
    private String strGroup;
    private int learnIntervalVal;
    private int learnPeriodVal;

    private ArrayList<WifiObject> arrayList;
    private WifiArrayAdapter wifiArrayAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LearnFragment newInstance(String param1, String param2) {
        LearnFragment fragment = new LearnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Required empty public constructor
    public LearnFragment() {
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
            // notify user
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(getActivity());
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

        // Creating WiFi list adapter
        arrayList = new ArrayList<>();
        wifiArrayAdapter = new WifiArrayAdapter(getActivity(), R.layout.wifi_list_item, arrayList);

        // Getting values from Shared prefs for Learning
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        strGroup = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
        strUsername = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
        strServer = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
        learnIntervalVal = sharedPreferences.getInt(Constants.LEARN_INTERVAL, Constants.DEFAULT_LEARNING_INTERVAL);
        learnPeriodVal = sharedPreferences.getInt(Constants.LEARN_PERIOD, Constants.DEFAULT_LEARNING_PERIOD);

        // Initialising internal DB n retriving values from it to fill our listView
        final InternalDataBase internalDataBase = new InternalDataBase(getActivity());

        List<Event> eventList = internalDataBase.getAllEvents();
        for(Event event : eventList) {
            WifiObject wifi = new WifiObject(event.getWifiName(), event.getWifiGroup(), event.getWifiUser());
            wifiArrayAdapter.add(wifi);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_learn, container, false);

        // Listening to Fab button click
        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("Add location");
                final EditText editText = new EditText(getActivity());
                editText.setHintTextColor(getResources().getColor(R.color.hintTextColor));
                editText.setHint(Constants.DEFAULT_LOCATION_NAME);
                builder.setView(editText);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strLocationName = editText.getText().toString();
                        InternalDataBase internalDataBase = new InternalDataBase(getActivity());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.LOCATION_NAME, strLocationName);
                        editor.apply();
                        dialog.dismiss();
                        WifiObject wifi = new WifiObject(strLocationName, strGroup , strUsername);
                        insertIntoList(wifi);
                        internalDataBase.addEvent(new Event(strLocationName, strGroup, strUsername));
                        handler.post(runnableCode);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

                Snackbar.make(view, "Add unique location name", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ListView listView = (ListView)rootView.findViewById(R.id.listView);
        listView.setAdapter(wifiArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Add your custom Fragment here
            }
        });

        return rootView;
    }

    // Insert new location into listView
    private void insertIntoList(WifiObject wifi) {
        wifiArrayAdapter.add(wifi);

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle("Learning");
        progress.setMessage("Please wait while we are collecting the Wifi APs around you...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                progress.cancel();
                handler.removeCallbacks(runnableCode);
            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, learnPeriodVal * 60 * 1000);
    }

    // Timers to keep track of our Learning period
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Passing values to WifiIntent for further processing
            if (Build.VERSION.SDK_INT >= 23 ) {
                if(Utils.isWiFiAvailable(mContext) && Utils.hasAnyLocationPermission(mContext)) {
                    Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                    intent.putExtra("event", Constants.LEARN_TAG);
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
                    intent.putExtra("event", Constants.LEARN_TAG);
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
            handler.postDelayed(runnableCode, learnIntervalVal * 1000);
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler.removeCallbacks(runnableCode);
        mListener = null;
    }

    // Logging message in form of Toasts
    private void logMeToast(String message) {
        Log.e(TAG, message);
        toast(message);
    }

    private void toast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }
}
