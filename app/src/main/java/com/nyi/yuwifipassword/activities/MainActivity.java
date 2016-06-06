package com.nyi.yuwifipassword.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.widget.SwitchCompat;
import android.util.EventLogTags;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nyi.yuwifipassword.Adapter.MyListViewAdapter;
import com.nyi.yuwifipassword.Manifest;
import com.nyi.yuwifipassword.R;
import com.nyi.yuwifipassword.YUWifiPassword;
import com.nyi.yuwifipassword.data.Wifi;
import com.nyi.yuwifipassword.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public final String LOG_TAG = "Wifi";

    private TextView textView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwitchCompat wifi_switch;
    public static MyListViewAdapter myListViewAdapter;
    public static ListView listView;

    public static WifiManager wifiManager;
    private Boolean wifi_Is_open = false;
    private ArrayList<Wifi> emptyList = new ArrayList<>();
    public static ArrayList<Wifi> mDefault_Firebasse_Wifi_List = new ArrayList<>();
    private MyNetworkMonitor myNetworkMonitor;
    private boolean isRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if(intent != null){
            mDefault_Firebasse_Wifi_List = (ArrayList<Wifi>) intent.getSerializableExtra(Constants.DATA);
        }
        //Declaration
        textView = (TextView) findViewById(R.id.text);
        wifi_switch = (SwitchCompat) findViewById(R.id.wifi_switch);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        listView = (ListView) findViewById(R.id.listview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);


        //This is to check wifi status
        sync_wifi();
        emptyList.add(new Wifi("Wifi is turned off",Constants.NOT_AVAILABLE));


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("Finished first time", true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
        Adding Listener
        */

        //listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Wifi wifi = mDefault_Firebasse_Wifi_List.get(position);
                OpenInputDialogBox(wifi.getSsid(), wifi.getPassword(), "Connect");
                Toast.makeText(getApplicationContext(), wifi.getPassword(), Toast.LENGTH_LONG).show();
            }
        });

        //switch compat
        wifi_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //To open or close Wifi
                wifiManager.setWifiEnabled(isChecked);

                wifi_Is_open = isChecked;
                if (wifi_Is_open) {
                    //When wifi is opened, then assign all wifi list to adapter
                    registerNetworkMonitor();
                } else {
                    //This is to hide the adapter
                    setBlankAdapter();
                }

            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
/*

        //Sync button
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Synchronize", "Inside synchonize buttin");
                sync_wifi();
            }
        });
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(myNetworkMonitor, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        sync_wifi();

        //When this activity is running again, show snackbar to synchronize but need to check wifi open or not
        if (wifiManager.isWifiEnabled()) showSnackbar();
    }

    protected void onPause() {
        if(isRegister){
            unregisterReceiver(myNetworkMonitor);
            isRegister = false;
        }
        super.onPause();
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
        } else if(id == R.id.update){
            Intent intent = new Intent(YUWifiPassword.getContext(), WelcomeActivity.class);
            intent.putExtra(Constants.UPDATE,true);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    This method is called when user swipe the screen
     */
    @Override
    public void onRefresh() {
        Toast.makeText(YUWifiPassword.getContext(),"Refreshing",Toast.LENGTH_SHORT).show();
        sync_wifi();
        swipeRefreshLayout.setRefreshing(false);
    }
    /*
           This is the helper function to syn the wifi status
        */
    private void sync_wifi() {
        //If wifi is opened, show all wifi list and if not, set blank adapter P.S- set Switch Compact icon 'on' or 'off'
        if (wifiManager.isWifiEnabled()) {
            wifi_switch.setChecked(true);
            registerNetworkMonitor();
        } else {
            Log.i("Blank", "Blank Adapter");
            Toast.makeText(getApplicationContext(), "Wifi is turned off.", Toast.LENGTH_SHORT).show();
            wifi_switch.setChecked(false);
            setBlankAdapter();
        }
    }

    /*
    Create wifi receiver and start scan
     */
    private void registerNetworkMonitor(){
        if(myNetworkMonitor == null){
            myNetworkMonitor = new MyNetworkMonitor();
        }
        registerReceiver(myNetworkMonitor, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        isRegister = true;
        wifiManager.startScan();

        //Then set the listview onClicklistener enable
        listView.setEnabled(true);
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

            //If already existed wifi SSID name is same with the wifi that is trying to connect now, then delete it
            if (i.SSID.compareTo("\"" + networkSSID + "\"") == 0) {
                wifiManager.removeNetwork(i.networkId);
                break;
            }
        }

         //Create new wifi configure and add SSID and password
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
        conf.preSharedKey = "\"" + networkPass + "\"";

        //Then, you need to add it to Android wifi manager settings:
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> configList = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration in : configList) {
            if (in.SSID != null && in.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(in.networkId, true);
                wifiManager.reconnect();
                Log.i("Connecting", "Connect Wifi");
                break;
            }
        }
    }



    /*
    This is the helper function to set the wifi adapter empty
     */
    private void setBlankAdapter() {
        //Create New emptylist to show when wifi is turned off
        myListViewAdapter = new MyListViewAdapter(YUWifiPassword.getContext(), R.layout.list_view_row, emptyList);
        listView.setEnabled(false);
        listView.setAdapter(myListViewAdapter);
        Log.i(LOG_TAG, "De a hti");
        if(isRegister){
            unregisterReceiver(myNetworkMonitor);
            isRegister = false;
        }

    }


    public void OpenInputDialogBox(String title, String text,String buttonText) {
        LayoutInflater layoutInflater = LayoutInflater.from(YUWifiPassword.getContext());
        View promptView = layoutInflater.inflate(R.layout.pop_up_info_layout, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert.setTitle(title);
        alert.setView(promptView);

        final TextView popup_main_text1 = (TextView) promptView.findViewById(R.id.pop_up_main_text1);
        final TextView popup_main_text2 = (TextView) promptView.findViewById(R.id.pop_up_main_text2);
//        alert.setTitle(title);
//        alert.setMessage(text);
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
            //networkInfo = connManager.getActiveNetworkInfo(ConnectivityManager.TYPE_WIFI);

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
        Snackbar.make(findViewById(R.id.listview), "Synchronize wifi again", Snackbar.LENGTH_LONG).setAction("SYNC", new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sync_wifi();
                    }
                }).show();
    }

    public static class MyNetworkMonitor extends BroadcastReceiver {
        Context mContext;

        public MyNetworkMonitor() {

        }

        /*
        this method is called when there is a change in wifi service
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            mContext = context;
            // Process the Intent here
            Toast.makeText(mContext,"Wifi status changed",Toast.LENGTH_SHORT).show();
            show_wifi_list();
        }

        /*
        To show all wifi list
         */
        public void show_wifi_list() {
            //Firstly assign all wifi status to Not in Range
            setAllWifiNotInRange();
            Manifest manifest = new Manifest();
            List<android.net.wifi.ScanResult> mScanResults = wifiManager.getScanResults();
            int temp_position = 0;

            //Loop through search result list
            for (int a = 0; a < mScanResults.size(); a++) {

                //Loop through the default wifi list
                int size = mDefault_Firebasse_Wifi_List.size();
                for(int b = 0; b < size; b++){
                    Wifi mWifi = mDefault_Firebasse_Wifi_List.get(b);
                    //Compare SSID name.  If same, show first and assign status to Available
                    if (mWifi.getSsid().compareTo(mScanResults.get(a).SSID) == 0) {
                        Log.i("Wifi", "Inside the same wife : Found " + mWifi.getSsid());
                        mWifi.setStatus(Constants.AVAILABLE);
                        mDefault_Firebasse_Wifi_List.add(temp_position, mWifi);
                        mDefault_Firebasse_Wifi_List.remove(b + 1);
                        temp_position++;
/*
                        //If wifi SSID in the default list is same with current connected SSID, then assgin status to Connected
                        String wifi = "\"" + mWifi.getSsid() + "\"";  //Take care of ""
                        if (wifi.compareTo(getCurrentSsid()) == 0) {
                            mWifi.setStatus("Connected");
                        }*/
                        break;
                    }
                }
            }

            //Finally assign wifi list to listview adapter
            assignWifiListToListview();
        }

        /*
    This is the helper function to delete all wifi status
     */
        public void setAllWifiNotInRange() {
            for (Wifi wifi : mDefault_Firebasse_Wifi_List) {
                wifi.setStatus("Not in Range");
            }
        }

        /*
    Assign wifi List to listview
     */
        public void assignWifiListToListview(){
            myListViewAdapter = new MyListViewAdapter(mContext, R.layout.list_view_row, mDefault_Firebasse_Wifi_List);
            listView.setAdapter(myListViewAdapter);
            Log.i("Wifi","Assign Adapter");
        }
    }

}
