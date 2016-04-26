package com.nyi.yuwifipassword;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    View sync;
    TextView textView;
    WifiManager wifiManager;
    SwitchCompat wifi_switch;
    Boolean wifi_Is_open = false;
    ArrayList<Wifi> emptyList = new ArrayList<>();
    ArrayList<Wifi> mDefault_Wifi_List = new ArrayList<>();
    String mNotInRange = "Not in Range";
    String mAvailable = "Available";
    RecyclerView mRecyclerView;
    CardViewAdapter mCardViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    final boolean[] isConnect = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setDafultWifiList();

        //Declaration
        textView = (TextView) findViewById(R.id.text);
        wifi_switch = (SwitchCompat) findViewById(R.id.wifi_switch);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        sync = (View) findViewById(R.id.btn_sync);

        //This is for card view
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Create New emptylist to show when wifi is closed
        emptyList.add(new Wifi("Wifi is closed", "Not Available"));

        //This is to check wifi status
        sync_wifi();

        //This is to listen switch compat
        wifi_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //To open or close Wifi
                wifiManager.setWifiEnabled(isChecked);

                wifi_Is_open = isChecked;
                if (wifi_Is_open) {
                    //When wifi is opened, then assign all wifi list to adapter
                    show_wifi_list();
                } else {
                    //This is to hide the adapter
                    setBlankAdapter();
                }

            }
        });
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Synchronize", "Inside synchonize buttin");
                sync_wifi();
            }
        });
        //this is to know the connection speed wifiManager.getConnectionInfo().getRssi();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardViewAdapter.setOnItemClickListener(new CardViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i("Recycler Adapter", " Clicked on Item " + position);
                Wifi wifi = CardViewAdapter.getWifiArrayList().get(position);

                //When user clicked wifi is Available, do connect function to connect and if not show Not in Range
                if (wifi.getStatus().compareTo(mAvailable) == 0 || wifi.getStatus().compareTo("Connected") == 0) {
                    connect(wifi.getSSID(), wifi.getPassword());
                } else if (wifi.getStatus().compareTo(mNotInRange) == 0) {
                    OpenInputDialogBox(wifi.getSSID(), mNotInRange,"OK");
                }
            }
        });

        //When this activity is running again, show snackbar to synchronize but need to check wifi open or not
        if (wifiManager.isWifiEnabled()) showSnackbar();
    }

    /*
    To create things on tool bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    To listen toolbar click
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sync) {
            //Synchronize again
            sync_wifi();
        } else if (id == R.id.about) {
            //Switch to About Activity
            Intent in = new Intent(this, AboutActivity.class);
            startActivity(in);
        }

        return super.onOptionsItemSelected(item);
    }

    /*
           This is the helper function to syn the wifi status
        */
    private void sync_wifi() {

        //If wifi is opened, show all wifi list and if not, set blank adapter P.S- set Switch Compus icon switch or not
        if (wifiManager.isWifiEnabled()) {
            wifi_switch.setChecked(true);
            show_wifi_list();
        } else {
            wifi_switch.setChecked(false);
            setBlankAdapter();
            Toast.makeText(getApplicationContext(), "Wifi is closed.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    To show all wifi list
     */
    private void show_wifi_list() {
        //Firstly assign all wifi status to Not in Range
        setAllWifiNotInRange();

        //Do wifi scan
        wifiManager.startScan();
        List<android.net.wifi.ScanResult> mScanResults = wifiManager.getScanResults();
        int temp_position = 0;

        //Loop through search result list
        for (int a = 0; a < mScanResults.size(); a++) {

            //Loop through the default wifi list
            for (int b = 0; b < mDefault_Wifi_List.size(); b++) {
                Wifi mWifi = mDefault_Wifi_List.get(b);
                //Compare SSID name.  If same, show first and assign status to Available
                if (mWifi.getSSID().compareTo(mScanResults.get(a).SSID) == 0) {
                    Log.i("Inside the same wife", "Found");
                    mWifi.setStatus(mAvailable);
                    mDefault_Wifi_List.add(temp_position, mWifi);
                    mDefault_Wifi_List.remove(b + 1);
                    temp_position++;
/*
                    //If wifi SSID in the default list is same with current connected SSID, then assgin status to Connected
                    String wifi = "\"" + mWifi.getSSID() + "\"";  //Take care of ""
                    if (wifi.compareTo(getCurrentSsid()) == 0) {
                        mWifi.setStatus("Connected");
                    }*/

                }

            }
        }

        //Finally assign list to recycler adaoter
        setmRecyclerAdapter();
    }

    /*
    This is the helper function to connect wifi
     */
    private void connect(String networkSSID, String networkPass) {
        boolean isfound = false;

        //To get all wifi name that is once connected
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        //Loop through that list
        for (WifiConfiguration i : list) {

            //If already existed wifi SSID name is same with the wifi that is trying to connect SSID name, then change the isfound value to true
            if (i.SSID.compareTo("\"" + networkSSID + "\"") == 0) {
                //User to show it is found
                Toast.makeText(getApplicationContext(), "Password has already entered", Toast.LENGTH_SHORT).show();
                OpenInputDialogBox(networkSSID, "Password has already entered","Connect");
                isfound = true;
                    Log.i("Connecting", "Connect already exited Wifi");
                    //Disconnect and reconnect again
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();

                break;
            }
        }

        //If the wifi that is trying to connect is not in the wifi setting configure list
        if (!isfound) {

            //Create new wifi configure and add SSID and password
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
            conf.preSharedKey = "\"" + networkPass + "\"";

            //Then, you need to add it to Android wifi manager settings:
            wifiManager.addNetwork(conf);

            List<WifiConfiguration> configList = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration in : configList) {
                if (in.SSID != null && in.SSID.equals("\"" + networkSSID + "\"")) {

                    OpenInputDialogBox(networkSSID, "Password has been entered","Connect");
                    Toast.makeText(getApplicationContext(), "Password has been entered", Toast.LENGTH_SHORT).show();
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(in.networkId, true);
                    wifiManager.reconnect();
                    Log.i("Connecting", "Connect Wifi");
                    isConnect[0]=false;

                    break;
                }
            }

        }
    }

    /*
    This is the helper function to assign All Wifi List;
     */
    private void setDafultWifiList() {

        mDefault_Wifi_List.add(new Wifi("YUASLIB", mNotInRange, "rasulip!$"));
        mDefault_Wifi_List.add(new Wifi("YUASAHE", mNotInRange, "rasuart!$"));
        mDefault_Wifi_List.add(new Wifi("YUASBAH", mNotInRange, "rasubago!$"));
        mDefault_Wifi_List.add(new Wifi("YUASCHE", mNotInRange, "rasuchem!$"));
        mDefault_Wifi_List.add(new Wifi("YUASCHL", mNotInRange, "rasuchina!$"));
        mDefault_Wifi_List.add(new Wifi("YUASCVH", mNotInRange, "rasuconvo!$"));
        mDefault_Wifi_List.add(new Wifi("YUASDH", mNotInRange, "rasudago!$"));
        mDefault_Wifi_List.add(new Wifi("YUASEM", mNotInRange, "rasutau!$"));
        mDefault_Wifi_List.add(new Wifi("YUASGEOG", mNotInRange, "rasugeog!$"));
        mDefault_Wifi_List.add(new Wifi("YUASGOE", mNotInRange, "rasugeo!$"));
        mDefault_Wifi_List.add(new Wifi("YUASINH", mNotInRange, "rasuinn!$"));
        mDefault_Wifi_List.add(new Wifi("YUASIYH", mNotInRange, "rasuinya!$"));
        mDefault_Wifi_List.add(new Wifi("UYL", mNotInRange, "uylasd!@#"));
        mDefault_Wifi_List.add(new Wifi("YUASLKC", mNotInRange, "rasulkc!$"));
        mDefault_Wifi_List.add(new Wifi("YUASMBZ", mNotInRange, "rasumath!$"));
        mDefault_Wifi_List.add(new Wifi("YUASORT", mNotInRange, "rasuor!$"));
        mDefault_Wifi_List.add(new Wifi("YUASPHS", mNotInRange, "rasuphy!$"));
        mDefault_Wifi_List.add(new Wifi("YUASPIH", mNotInRange, "rasupin!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSSC", mNotInRange, "rasurc!$"));
        mDefault_Wifi_List.add(new Wifi("RYU", mNotInRange, "rasurec!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSGH", mNotInRange, "rasusag!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSHH", mNotInRange, "rasushw!$"));
        mDefault_Wifi_List.add(new Wifi("YUASTAH", mNotInRange, "rasutato!$"));
        mDefault_Wifi_List.add(new Wifi("YUASTRH", mNotInRange, "rasuthi!$"));
        mDefault_Wifi_List.add(new Wifi("YUASULB", mNotInRange, "rasuulb!$"));
        mDefault_Wifi_List.add(new Wifi("YUASAHI", mNotInRange, "rasuwain!$"));
        mDefault_Wifi_List.add(new Wifi("YUASYAH", mNotInRange, "rasuyadnar!$"));
        mDefault_Wifi_List.add(new Wifi("YUASALP", mNotInRange, "rasumaya!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSCN", mNotInRange, "rasuyuas!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSCH", mNotInRange, "rasuscience!$"));
        mDefault_Wifi_List.add(new Wifi("YUASSAF", mNotInRange, "rasusaf!$"));

        mDefault_Wifi_List.add(new Wifi("NNN", mNotInRange, "123345678"));
    }

    /*
    This is the helper function to delete all wifi status
     */
    private void setAllWifiNotInRange() {
        for (int a = 0; a < mDefault_Wifi_List.size(); a++) {
            mDefault_Wifi_List.get(a).setStatus("Not in Range");
        }
    }

    /*
    This is the helper function to set the wifi adapter empty
     */
    private void setBlankAdapter() {
        Log.i("Blank", "Blank Adapter");
        Toast.makeText(getApplicationContext(), "Wifi is closed.", Toast.LENGTH_SHORT).show();
        mCardViewAdapter = new CardViewAdapter(emptyList);
        mRecyclerView.setAdapter(mCardViewAdapter);
    }

    /*
    This is the helper function to set Recycler adapter
     */
    private void setmRecyclerAdapter() {
        mCardViewAdapter = new CardViewAdapter(mDefault_Wifi_List);
        mRecyclerView.setAdapter(mCardViewAdapter);
    }

    public void OpenInputDialogBox(String title, String text,String buttonText) {


        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View promptView = layoutInflater.inflate(R.layout.pop_up_info_layout, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert.setTitle(title);
        alert.setView(promptView);

        final TextView popup_main_text1 = (TextView) promptView.findViewById(R.id.pop_up_main_text1);
        final TextView popup_main_text2 = (TextView) promptView.findViewById(R.id.pop_up_main_text2);
        popup_main_text1.setText(title);
        popup_main_text2.setText(text);


        alert.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        // create an alert dialog
        AlertDialog alert1 = alert.create();
        alert1.show();

    }

    /*
    This function is to get the current connected SSID name
     */
    public String getCurrentSsid() {
        String ssid = "";

        //Check if wifi is opened or not
        if (wifiManager.isWifiEnabled()) {

            ConnectivityManager connManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            NetworkInfo networkInfo1=connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //connManager.getActiveNetworkInfo();

            //If wifi is opened, check wifi is connected or not
             if (networkInfo.isConnected()) {

                final WifiInfo connectionInfo = wifiManager.getConnectionInfo();

                //Check if current wifi name is not null, if not return its name
                if (connectionInfo != null && !(connectionInfo.getSSID().equals(""))) {
                    ssid = connectionInfo.getSSID();
                    Toast.makeText(getApplicationContext(), "Connected wifi is "+ssid, Toast.LENGTH_SHORT).show();
                }

            }
        }
        return ssid;
    }

    /*
    For Snackbar
     */
    private void showSnackbar() {
        Snackbar.make(findViewById(R.id.my_recycler_view), "Synchronize wifi again", Snackbar.LENGTH_LONG).setAction("SYNC", new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sync_wifi();
                    }
                }).show();
    }
}
