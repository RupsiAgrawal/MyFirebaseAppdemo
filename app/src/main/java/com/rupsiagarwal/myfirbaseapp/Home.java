package com.rupsiagarwal.myfirbaseapp;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgreenhalgh.android.simpleitemdecoration.grid.GridDividerItemDecoration;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rupsiagarwal.myfirbaseapp.firebase.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.rupsiagarwal.myfirbaseapp.TaskList.ALARM;
import static com.rupsiagarwal.myfirbaseapp.TaskList.DATE;
import static com.rupsiagarwal.myfirbaseapp.TaskList.KEY;
import static com.rupsiagarwal.myfirbaseapp.TaskList.TIME;
import static com.rupsiagarwal.myfirbaseapp.TaskList.TITLE;

public class Home extends BaseActivity {

    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView vertical_recycler_view, recyclerView;
    ArrayList<String> horizontalList, verticalList;
    ArrayList<HashMap<String, String>> list;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    HashMap<String, String> map = new HashMap<String, String>();
    private final String Name[] = {
            "Add Task",
            "Task List",
            "Chat",
            "Logout",
    };

    private final int Image[] = {
            R.drawable.ic_addtask,
            R.drawable.ic_tasklist,
            R.drawable.ic_chat,
            R.drawable.logo,
    };
    AdView mAdView;
    RecyclerView taskRecycler;
    Firebase reference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initViews();
        //admob
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();
        mAdView.loadAd(adRequest);
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        // mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorsimple)));
        //mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title);
        ImageView logout = (ImageView) mCustomView.findViewById(R.id.imgLogout);
        ImageView back = (ImageView) mCustomView.findViewById(R.id.imgBack);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                File sharedPreferenceFile = new File("/data/data/com.rupsiagarwal.myfirbaseapp/shared_prefs/");
                File[] listFiles = sharedPreferenceFile.listFiles();
                for (File filelist : listFiles) {
                    filelist.delete();
                }
                FirebaseAuth.getInstance().signOut();
                Intent logout = new Intent(Home.this, SignInActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                logout.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Home.this.startActivity(logout);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Home.this, SplashScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Home.this.startActivity(i);
                Home.this.overridePendingTransition(0, 0);
            }
        });
        if (null != mTitleTextView) {
            mTitleTextView.setText("Firebase Demo");
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
        //tasklist
        taskRecycler = (RecyclerView) findViewById(R.id.taskrecyclerview);
        taskRecycler.setLayoutManager(new LinearLayoutManager(this));
        taskRecycler.setHasFixedSize(true);

        // Set up FirebaseRecyclerAdapter with the Query
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("task_list");
        SharedPreferences t = getSharedPreferences("info", MODE_PRIVATE);
        String name = t.getString("name", "");
        list = new ArrayList<HashMap<String, String>>();
        Firebase.setAndroidContext(this);
        //reference = new Firebase("https://myfirebaseapp-d7ee6.firebaseio.com/messages/");
        reference1 = new Firebase("https://myfirebaseapp-d7ee6.firebaseio.com/task_list/" + name);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading....");
        progressDialog.show();
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String key = dataSnapshot.getKey();
                String date = map.get("date").toString();
                String time = map.get("time").toString();
                String task = map.get("desc").toString();
                String alarm = map.get("alarm").toString();
                HashMap<String, String> map1 = new HashMap<String, String>();
                map1.put(DATE, date);
                map1.put(TIME, time);
                map1.put(TITLE, task);
                map1.put(ALARM, alarm);

                map1.put(KEY, key);
                list.add(map1);
                TaskListAdapter mAdapter = new TaskListAdapter(list, Home.this);
                taskRecycler.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                taskRecycler.getLayoutManager().scrollToPosition(list.size() - 1);
                progressDialog.dismiss();
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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        int numColumns = 2;

        Drawable horizontalDivider = ContextCompat.getDrawable(this, R.drawable.line_divider);
        Drawable verticalDivider = ContextCompat.getDrawable(this, R.drawable.line_divider);
//        recyclerView.setLayoutManager(
//                new GridLayoutManager(recyclerView.getContext(), 2, GridLayoutManager.HORIZONTAL, false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new GridDividerItemDecoration(horizontalDivider, verticalDivider, numColumns));
        ArrayList<BaseItem> BaseItems = prepareData();
        DataAdapter adapter = new DataAdapter(getApplicationContext(), BaseItems);
        recyclerView.setAdapter(adapter);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");


                    Toast.makeText(getApplicationContext(), "notification: " + message, Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(Home.this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.app_name)
                            .setMessage("You have received new notification :" + message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //new DownloadJSON().execute();

                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                    // txtMessage.setText(message);
                }
            }
        };

        //FCM push notification method
        displayFirebaseRegId();
    }


    //FCM push notification method
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("Id", "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            //Toast.makeText(getApplicationContext(), "Push notification: " + regId, Toast.LENGTH_LONG).show();
            //txtRegId.setText("Firebase Reg Id: " + regId);
        } else {
            //Toast.makeText(getApplicationContext(), "Firebase Reg Id is not received yet!", Toast.LENGTH_LONG).show();
            //txtRegId.setText("Firebase Reg Id is not received yet!");
        }
    }


    private ArrayList<BaseItem> prepareData() {

        ArrayList<BaseItem> baselist = new ArrayList<>();
        for (int i = 0; i < Name.length; i++) {
            BaseItem BaseItem = new BaseItem();
            BaseItem.setname(Name[i]);
            BaseItem.setimage(Image[i]);
            baselist.add(BaseItem);

        }
        return baselist;
    }

    @Override
    public void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(Home.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(Home.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened

        //NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        LocalBroadcastManager.getInstance(Home.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Home.super.onBackPressed();
                        Intent i = new Intent(Home.this, SplashScreen.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
//
                    }
                }).create().show();

        //super.onBackPressed();
    }

    //admob


    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}