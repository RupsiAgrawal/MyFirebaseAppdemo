package com.rupsiagarwal.myfirbaseapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rupsiagarwal.myfirbaseapp.models.Tasks;


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddtaskActivity extends BaseActivity {

    private static final String TAG = "AddtaskActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText mTitleField;
    private EditText mBodyField;
    private Button mSubmitButton;
    EditText datePicker;
    EditText timePicker;
    Spinner typetask;
    String tasktype;
    ArrayList<String> list;
    private TextInputLayout inputLayoutTitle, inputLayoutDesc, inputLayoutDate, inputLayoutTime;
    Firebase reference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);
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
                Intent logout = new Intent(AddtaskActivity.this, SignInActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                logout.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                AddtaskActivity.this.startActivity(logout);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(AddtaskActivity.this, Home.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                AddtaskActivity.this.startActivity(i);
                AddtaskActivity.this.overridePendingTransition(0, 0);
            }
        });
        if (null != mTitleTextView) {
            mTitleTextView.setText("Add Task");
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        inputLayoutTitle = (TextInputLayout) findViewById(R.id.input_layout_title);
        inputLayoutDesc = (TextInputLayout) findViewById(R.id.input_layout_desc);
        inputLayoutDate = (TextInputLayout) findViewById(R.id.input_layout_date);
        inputLayoutTime = (TextInputLayout) findViewById(R.id.input_layout_time);
        typetask = (Spinner) findViewById(R.id.spinner);
        mTitleField = (EditText) findViewById(R.id.edittxtTitle);
        mBodyField = (EditText) findViewById(R.id.edittxtDesc);
        mSubmitButton = (Button) findViewById(R.id.btnSave);
        datePicker = (EditText) findViewById(R.id.edittxtDate);
        timePicker = (EditText) findViewById(R.id.edittxtTime);
        mTitleField.addTextChangedListener(new MyTextWatcher(mTitleField));
        mBodyField.addTextChangedListener(new MyTextWatcher(mBodyField));
        datePicker.addTextChangedListener(new MyTextWatcher(datePicker));
        timePicker.addTextChangedListener(new MyTextWatcher(timePicker));
        //Uncomment the below line of code for 24 hour view
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        list = new ArrayList<String>();
        list.add("Daily");
        list.add("Weekly");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddtaskActivity.this, android.R.layout.simple_spinner_item, list);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typetask.setAdapter(dataAdapter);
        typetask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @SuppressWarnings("unused")
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int postion, long arg3) {
                tasktype = parent.getItemAtPosition(postion).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        datePicker.setShowSoftInputOnFocus(false);
        timePicker.setShowSoftInputOnFocus(false);
        datePicker.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddtaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datePicker.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                // calender class's instance and get current date , month and year from calender
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddtaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker1, int selectedHour, int selectedMinute) {
                        timePicker.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        SharedPreferences t = getSharedPreferences("info", MODE_PRIVATE);
        final String myname = t.getString("name", "");
        Firebase.setAndroidContext(this);
        //reference = new Firebase("https://myfirebaseapp-d7ee6.firebaseio.com/messages/");
        reference1 = new Firebase("https://myfirebaseapp-d7ee6.firebaseio.com/task_list/" + myname);

    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
//                case R.id.input_name:
//                    break;
            }
        }
    }

    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();
        final String date = datePicker.getText().toString();
        final String time = timePicker.getText().toString();
        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Adding task...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AddtaskActivity.this, Home.class));
        finish();
        try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", title);
            map.put("desc", body);
            map.put("time", time);
            map.put("date", date);
            map.put("tasktype", tasktype);
            map.put("alarm", "0");
            reference1.push().setValue(map);
//            String[] separated = time.split(":");
//            String hour = separated[0];
//            String min = separated[1];
//            final int hr = Integer.parseInt(hour);
//            final int mins = Integer.parseInt(min);
//            Intent myIntent = new Intent(AddtaskActivity.this, AlarmReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getService(AddtaskActivity.this, 0, myIntent, 0);
//            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            calendar.set(Calendar.MONTH, 8);
//            calendar.set(Calendar.YEAR, 2017);
//            calendar.set(Calendar.DATE, 8);
//            calendar.set(Calendar.HOUR_OF_DAY, 18);
//            calendar.set(Calendar.MINUTE, 37);
//            calendar.set(Calendar.SECOND, 0);
//            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } catch (Exception e) {

        }
        // [START single_value_read]
//        final String userId = getUid();
//        SharedPreferences t = getSharedPreferences("info", MODE_PRIVATE);
//        String name=t.getString("name","");
//        mDatabase.child("users").child(name).addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // Get user value
//                        LoginUser user = dataSnapshot.getValue(LoginUser.class);
//
//                        // [START_EXCLUDE]
//                        if (user == null) {
//                            // User1 is null, error out
//                            Log.e(TAG, "User1 " + userId + " is unexpectedly null");
//                            Toast.makeText(AddtaskActivity.this,
//                                    "Error: could not fetch user.",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Write new post
//                            SharedPreferences t = getSharedPreferences("info", MODE_PRIVATE);
//                            String name=t.getString("name","");
//                            writeNewPost(name, body, time, tasktype, date);
//                        }
//
//                        // Finish this Activity, back to the stream
//                        setEditingEnabled(true);
//                        finish();
//                        // [END_EXCLUDE]
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
//                        // [START_EXCLUDE]
//                        setEditingEnabled(true);
//                        // [END_EXCLUDE]
//                    }
//                });
        // [END single_value_read]
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String name, String task, String total_time, String type, String add_date) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        Tasks tasks = new Tasks(name, task, add_date, type, total_time, "0");
        mDatabase.child("task_list").child(name).setValue(tasks);
        String[] separated = total_time.split(":");
        String hour = separated[0];
        String min = separated[1];
        final int hr = Integer.parseInt(hour);
        final int mins = Integer.parseInt(min);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hr);
        calendar.set(Calendar.MINUTE, mins);
        String caltime = String.valueOf(calendar.getTimeInMillis());

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(AddtaskActivity.this, AlarmReceiver.class);
        intent.putExtra("time", caltime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//        Map<String, Object> postValues = tasks.toMap();
//        mDatabase.child("task_list").child(name).setValue(tasks);
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/task_list/" + key, postValues);
//        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]


}
