package com.nyi.yuwifipassword.utils;

import com.nyi.yuwifipassword.data.Wifi;

import java.util.ArrayList;

/**
 * Created by IN-3442 on 27-Apr-16.
 */
public class Constants {
    public static final String FIREBASE_URL = "https://yuwifikey.firebaseio.com/";
    public static final String WIFI_DATA_URL = Constants.FIREBASE_URL + "/data/";
    public static final String NOT_IN_RANGE = "Not in Range";
    public static final String AVAILABLE = "Available";
    public static final String NOT_AVAILABLE = "Not Available";
    public static final String UPDATE = "Update";
    public static final String FIRST_TIME = "First";
    public static final String DATA = "data";
    public static ArrayList<Wifi> mDefault_Wifi_List = new ArrayList<>();

    /*
    This is the helper function to assign All Wifi List;
     */
    public static void setDafultWifiList() {
        mDefault_Wifi_List.add(new Wifi("YUASLIB", NOT_IN_RANGE, "rasulip!$"));
        mDefault_Wifi_List.add(new Wifi("YUASAHE", NOT_IN_RANGE, "rasuart!$"));
        mDefault_Wifi_List.add(new Wifi("YUASBAH", NOT_IN_RANGE, "rasubago!$"));
        mDefault_Wifi_List.add(new Wifi("YUASCHE", NOT_IN_RANGE, "rasuchem!$"));
        mDefault_Wifi_List.add(new Wifi("YUASCHL", NOT_IN_RANGE, "rasuchina!$"));
        mDefault_Wifi_List.add(new Wifi("YUASCVH", NOT_IN_RANGE, "rasuconvo!$"));
        mDefault_Wifi_List.add(new Wifi("YUASDH", NOT_IN_RANGE, "rasudago!$"));
        mDefault_Wifi_List.add(new Wifi("YUASEM", NOT_IN_RANGE, "rasutau!$"));
        mDefault_Wifi_List.add(new Wifi("YUASGEOG", NOT_IN_RANGE, "rasugeog!$"));
        mDefault_Wifi_List.add(new Wifi("YUASGOE", NOT_IN_RANGE, "rasugeo!$"));
        mDefault_Wifi_List.add(new Wifi("YUASINH", NOT_IN_RANGE, "rasuinn!$"));
        mDefault_Wifi_List.add(new Wifi("YUASIYH", NOT_IN_RANGE, "rasuinya!$"));
        mDefault_Wifi_List.add(new Wifi("UYL", NOT_IN_RANGE, "uylasd!@#"));
        mDefault_Wifi_List.add(new Wifi("YUASLKC", NOT_IN_RANGE, "rasulkc!$"));
        mDefault_Wifi_List.add(new Wifi("YUASMBZ", NOT_IN_RANGE, "rasumath!$"));
        mDefault_Wifi_List.add(new Wifi("YUASORT", NOT_IN_RANGE, "rasuor!$"));
        mDefault_Wifi_List.add(new Wifi("YUASPHS", NOT_IN_RANGE, "rasuphy!$"));
        mDefault_Wifi_List.add(new Wifi("YUASPIH", NOT_IN_RANGE, "rasupin!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSSC", NOT_IN_RANGE, "rasurc!$"));
        mDefault_Wifi_List.add(new Wifi("RYU", NOT_IN_RANGE, "rasurec!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSGH", NOT_IN_RANGE, "rasusag!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSHH", NOT_IN_RANGE, "rasushw!$"));
        mDefault_Wifi_List.add(new Wifi("YUASTAH", NOT_IN_RANGE, "rasutato!$"));
        mDefault_Wifi_List.add(new Wifi("YUASTRH", NOT_IN_RANGE, "rasuthi!$"));
        mDefault_Wifi_List.add(new Wifi("YUASULB", NOT_IN_RANGE, "rasuulb!$"));
        mDefault_Wifi_List.add(new Wifi("YUASAHI", NOT_IN_RANGE, "rasuwain!$"));
        mDefault_Wifi_List.add(new Wifi("YUASYAH", NOT_IN_RANGE, "rasuyadnar!$"));
        mDefault_Wifi_List.add(new Wifi("YUASALP", NOT_IN_RANGE, "rasumaya!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSCN", NOT_IN_RANGE, "rasuyuas!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSCH", NOT_IN_RANGE, "rasuscience!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSAF", NOT_IN_RANGE, "rasusaf!$"));

        mDefault_Wifi_List.add(new Wifi("NNN", NOT_IN_RANGE, "123345678"));
    }
}
