package com.nyi.yuwifipassword.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nyi.yuwifipassword.R;
import com.nyi.yuwifipassword.YUWifiPassword;
import com.nyi.yuwifipassword.activities.MainActivity;
import com.nyi.yuwifipassword.data.Wifi;
import com.nyi.yuwifipassword.utils.Constants;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WelcomeActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final String LOG_TAG = "Welcome Activity";
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    //ui
    private TextView mtextView;
    private TextView mButtonExit;

    //variable declaration
    private boolean mIsFirst;
    private SharedPreferences sp;
    private long mDataQuantity;
    private ArrayList<Wifi> mFirebasse_Wifi_List = new ArrayList<>();

    //Firebase Reference
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    private DatabaseReference mDataRef = mRootRef.child("data");
    private DatabaseReference mPassRef = mDataRef.child("password");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        mtextView = (TextView) findViewById(R.id.welcome_text);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_exit).setOnTouchListener(mDelayHideTouchListener);
        mButtonExit = (TextView)findViewById(R.id.dummy_exit);
        mButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sp = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        if(intent != null){
            if(intent.getBooleanExtra(Constants.UPDATE, false)) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(Constants.FIRST_TIME, false);
                editor.apply();
            }
        }
        //Check application is run for the first time
        //If first time, go to checkInternet method
        //If not, check quantity
        mIsFirst = sp.getBoolean(Constants.FIRST_TIME,false);
        if(!mIsFirst){
            checkInternet();
        }else{
            checkQuantity();
        }
    }

    /*
    Check if there is internet connection or not
    if connection is available, go to checkStatus method to check connection with firebase database
    if not, show dialog box to user
     */
    private void checkInternet(){
        Log.i(LOG_TAG,"check internet");
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        if(networkInfo == null){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.setTitle("Opps..");
            dialog.setMessage(getResources().getString(R.string.download_data));
            dialog.setCancelable(false);
            dialog.show();
        }else{
            checkStatus();
        }
    }

    /*
    check status with the firebase server
     */
    private  void checkStatus(){
        Log.i(LOG_TAG,"check status");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (connected) {
                    Log.i(LOG_TAG, "connected");
                    mtextView.setText("Downloading...");
                    checkQuantity();
                } else {
                    Log.i(LOG_TAG, "disconnected");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
    Check quantity
     */
    private void checkQuantity(){
        Log.i(LOG_TAG,"check quantity");
        mDataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mDataQuantity = dataSnapshot.getChildrenCount();
                getDataFromFirebase();
                delayer();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
    Get data from firebase
     */
    private void getDataFromFirebase(){
        Log.i(LOG_TAG, "get Data From Firebase");
        mFirebasse_Wifi_List.clear();

        mPassRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String ssid = dataSnapshot.getKey();
                Wifi wifi = dataSnapshot.getValue(Wifi.class);
                wifi.setSsid(ssid);
                mFirebasse_Wifi_List.add(wifi);
                Log.i(LOG_TAG, wifi.getSsid() + "  " + wifi.getPassword() + "  " + wifi.getStatus());
                mtextView.setText("Loading...");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*
    Checking if we got the all data from firebase database
     */
    private void delayer() {
        Log.i(LOG_TAG, "delayer");
        final Handler mHandler_ = new Handler();
        mHandler_.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_TAG, mFirebasse_Wifi_List.size() + "");
                if (mFirebasse_Wifi_List.size() == (int) mDataQuantity) {
                    goToMainActivity();
                } else {
                    delayer();
                }
            }
        }, 2000);
    }

    /*
        Switch to main activity
     */
    private void goToMainActivity(){
        Log.i(LOG_TAG,"go to main activity");
        if(!mIsFirst){
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constants.FIRST_TIME, true);
            editor.apply();
        }
        else if(mFirebasse_Wifi_List.size() == 0){
            checkStatus();
        }

            Intent intent = new Intent(YUWifiPassword.getContext(), MainActivity.class);
            intent.putExtra(Constants.DATA, mFirebasse_Wifi_List);
            startActivity(intent);
            finish();

    }
        /*
    Upload default data to Firebase
     */

    private void uploadWifiData(){
        Constants.setDafultWifiList();
        for(Wifi wifi : Constants.mDefault_Wifi_List){
            DatabaseReference eachWifiRef = mPassRef.child(wifi.getSsid());
            eachWifiRef.setValue(wifi);
        }
        Toast.makeText(YUWifiPassword.getContext(), "Finished uploading data", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
