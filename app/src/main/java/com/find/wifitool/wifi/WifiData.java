package com.find.wifitool.wifi;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay on 30/12/16.
 */

public class WifiData implements Parcelable {

    private List<WifiDataNetwork> mNetworks;

    public WifiData() {
        mNetworks = new ArrayList<WifiDataNetwork>();
    }

    public WifiData(Parcel in) {
        in.readTypedList(mNetworks, WifiDataNetwork.CREATOR);
    }

    public static final Parcelable.Creator<WifiData> CREATOR = new Parcelable.Creator<WifiData>() {
        public WifiData createFromParcel(Parcel in) {
            return new WifiData(in);
        }

        public WifiData[] newArray(int size) {
            return new WifiData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mNetworks);
    }

    /**
     * @return Returns a string containing a concise, human-readable description
     *         of this object.
     */
    @Override
    public String toString() {
        if (mNetworks == null || mNetworks.size() == 0)
            return "Empty data";
        else
            return mNetworks.size() + " networks data";
    }

    /**
     * @return Returns the list of scanned networks
     */
    public List<WifiDataNetwork> getNetworks() {
        return mNetworks;
    }
}
