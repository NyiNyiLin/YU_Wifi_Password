package com.nyi.yuwifipassword;

/**
 * Created by IN-3442 on 17-Jan-16.
 */
public class Wifi {
    private String SSID;
    private String status;
    private  String password;

    public Wifi(String SSID, String status) {
        this.SSID = SSID;
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public Wifi(String SSID, String status, String password) {

        this.SSID = SSID;
        this.status = status;
        this.password = password;
    }

    public String getSSID() {
        return SSID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
