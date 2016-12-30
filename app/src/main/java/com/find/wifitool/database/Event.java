package com.find.wifitool.database;

/**
 * Created by akshay on 30/12/16.
 */

public class Event {

    //private variables
    private int _id;
    private String _wifiName;
    private String _wifiGroup;
    private String _wifiUser;

    // Empty constructor
    public Event(){

    }

    public Event(int id, String wifiName, String wifiGroup, String wifiUser){
        this._id = id;
        this._wifiName = wifiName;
        this._wifiGroup = wifiGroup;
        this._wifiUser = wifiUser;
    }

    public Event(String wifiName, String wifiGroup, String wifiUser){
        this._wifiName = wifiName;
        this._wifiGroup = wifiGroup;
        this._wifiUser = wifiUser;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting wifiName
    public String getWifiName(){
        return this._wifiName;
    }

    // setting wifiName
    public void setWifiName(String wifiName){
        this._wifiName = wifiName;
    }

    // getting wifiGrp
    public String getWifiGroup(){
        return this._wifiGroup;
    }

    // setting wifiGrp
    public void setWifiGroup(String wifiGroup){
        this._wifiGroup = wifiGroup;
    }

    // getting wifiUser
    public String getWifiUser(){
        return this._wifiUser;
    }

    // setting wifiUser
    public void setWifiUser(String wifiUser){
        this._wifiUser = wifiUser;
    }

}
