package com.rupsiagarwal.myfirbaseapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rupsiagarwal.myfirbaseapp.models.LoginUser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class UsersList extends AppCompatActivity {
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    ArrayList<HashMap<String, String>> list;
    public static String NAME = "name";
    public static String EMAIL = "email";
    public static String ID = "uid";
    private DatabaseReference mDatabase;
    // [END define_database_reference
    private RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
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
                Intent logout = new Intent(UsersList.this, SignInActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                logout.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                UsersList.this.startActivity(logout);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(UsersList.this, Home.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                UsersList.this.startActivity(i);
                UsersList.this.overridePendingTransition(0, 0);
            }
        });
        if (null != mTitleTextView) {
            mTitleTextView.setText("User List");
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
        mRecycler = (RecyclerView) findViewById(R.id.recyclerview);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        noUsersText = (TextView) findViewById(R.id.noUsersText);

        pd = new ProgressDialog(UsersList.this);
        pd.setMessage("Loading...");
        pd.show();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("users");
        list = new ArrayList<HashMap<String, String>>();
//        Query TaskssQuery = ref.("uid");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    LoginUser user = singleSnapshot.getValue(LoginUser.class);
                    String key = singleSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(NAME, user.name);
                    map.put(EMAIL, user.email);
                    map.put(ID, key);
                    list.add(map);

                }
                UserListAdapter mAdapter = new UserListAdapter(list, UsersList.this);
                mRecycler.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pd.dismiss();
                //Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


    }
}