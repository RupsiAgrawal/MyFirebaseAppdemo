package com.rupsiagarwal.myfirbaseapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.rupsiagarwal.myfirbaseapp.R;
import com.rupsiagarwal.myfirbaseapp.models.Tasks;
import com.rupsiagarwal.myfirbaseapp.viewholder.TasksViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskList extends AppCompatActivity {


    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]
    ArrayList<HashMap<String, String>> list;
    TaskListAdapter mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    public static String DATE = "date";
    public static String TIME = "time";
    public static String TITLE = "title";
    public static String KEY = "keypath";
    public static String ALARM = "alarm";
    Firebase reference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasklist);
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
       // back.setVisibility(View.GONE);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                File sharedPreferenceFile = new File("/data/data/com.rupsiagarwal.myfirbaseapp/shared_prefs/");
                File[] listFiles = sharedPreferenceFile.listFiles();
                for (File filelist : listFiles) {
                    filelist.delete();
                }
                FirebaseAuth.getInstance().signOut();
                Intent logout = new Intent(TaskList.this, SignInActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                logout.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                TaskList.this.startActivity(logout);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(TaskList.this, Home.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                TaskList.this.startActivity(i);
                TaskList.this.overridePendingTransition(0, 0);
            }
        });
        if (null != mTitleTextView) {
            mTitleTextView.setText("Task List");
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) findViewById(R.id.recyclerview);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);

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
                String key=dataSnapshot.getKey();
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
                mAdapter = new TaskListAdapter(list, TaskList.this);
                mRecycler.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mRecycler.getLayoutManager().scrollToPosition(list.size() - 1);
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


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

//    public abstract Query getQuery(DatabaseReference databaseReference);

}
