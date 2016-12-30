package com.find.wifitool.wifi;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by akshay on 30/12/16.
 */

public class WifiDataNetwork implements Comparable<WifiDataNetwork>, Parcelable {

    private static final String TAG = WifiDataNetwork.class.getSimpleName();

    // private variables
    private String bssid;
    private String ssid;
    private String capabilities;
    private int frequency;
    private int level;
    private long timestamp;

    // Constructor
    public WifiDataNetwork(ScanResult result) {
        bssid = result.BSSID;
        ssid = result.SSID;
        capabilities = result.capabilities;
        frequency = result.frequency;
        level = result.level;
        timestamp = System.currentTimeMillis();
    }

    public WifiDataNetwork(Parcel in) {
        bssid = in.readString();
        ssid = in.readString();
        capabilities = in.readString();
        frequency = in.readInt();
        level = in.readInt();
        timestamp = in.readLong();
    }

    public static final Parcelable.Creator<WifiDataNetwork> CREATOR = new Parcelable.Creator<WifiDataNetwork>() {
        public WifiDataNetwork createFromParcel(Parcel in) {
            return new WifiDataNetwork(in);
        }

        public WifiDataNetwork[] newArray(int size) {
            return new WifiDataNetwork[size];
        }
    };

    @Override
    public int compareTo(WifiDataNetwork another) {
        return another.level - this.level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bssid);
        dest.writeString(ssid);
        dest.writeString(capabilities);
        dest.writeInt(frequency);
        dest.writeInt(level);
        dest.writeLong(timestamp);
    }

    @Override
    public String toString() {
        return ssid + " addr:" + bssid + " lev:" + level + "dBm freq:" + frequency + "MHz cap:" + capabilities;
    }
}
