package com.find.wifitool.wifi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.find.wifitool.R;

import java.util.List;

/**
 * Created by akshay on 30/12/16.
 */

public class WifiArrayAdapter extends ArrayAdapter<WifiObject> {

    private static final String TAG = WifiArrayAdapter.class.getSimpleName();

    // Constructor
    public WifiArrayAdapter(Context mContext, int layoutResourceId, List<WifiObject> objects) {
        super(mContext, layoutResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final WifiObject wifiItem = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wifi_list_item, parent, false);
        }

        // Getting UI components
        TextView wifiName = (TextView) convertView.findViewById(R.id.wifiName);
        TextView wifiGroup = (TextView) convertView.findViewById(R.id.fieldGrpName);
        TextView wifiUser = (TextView) convertView.findViewById(R.id.fieldUsrName);

        // Setting UI components
        wifiName.setText(wifiItem.wifiName);
        wifiGroup.setText(wifiItem.grpName);
        wifiUser.setText(wifiItem.userName);

        return convertView;
    }
}
