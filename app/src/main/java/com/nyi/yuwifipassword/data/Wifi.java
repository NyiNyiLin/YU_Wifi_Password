package com.nyi.yuwifipassword.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by IN-3442 on 17-Jan-16.
 */
public class Wifi implements Serializable{
    private String ssid;
    private String status;
    private  String password;

    public Wifi() {
    }

    public Wifi(String ssid) {
        this.ssid = ssid;
    }

    public Wifi(String ssid, String status) {
        this.ssid = ssid;
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public Wifi(String ssid, String status, String password) {
        this.ssid = ssid;
        this.status = status;
        this.password = password;
    }

    public String getSsid() {
        return ssid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

}
