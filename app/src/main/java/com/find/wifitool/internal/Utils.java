package com.find.wifitool.internal;

import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiManager;

/**
 * Created by akshay on 30/12/16.
 */

public class Utils {

    // empty construtor
    private Utils() {

    }

    // Checking if WiFi adapter is ON or OFF
    public static boolean isWiFiAvailable(Context context) {
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            return true;
        }
        else return false;
    }

    // Checking Location service status
    public static boolean isLocationAvailable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled)
            return false;
        else return true;
    }

    // Checking if we have location permission or not
    public static boolean hasAnyLocationPermission(Context context) {
        int fineLocationPermission = context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        int coarseLocationPermission = context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION");
        return fineLocationPermission == 0 || coarseLocationPermission == 0;
    }
}
