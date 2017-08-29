package com.rupsiagarwal.myfirbaseapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rupsiagarwal.myfirbaseapp.models.Tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.MyViewHolder> {
    final public static String ONE_TIME = "onetime";
    ArrayList<HashMap<String, String>> dataSet;
    HashMap<String, String> resultp = new HashMap<String, String>();
    int mExpandedPosition = -1;
    Context context;
    Calendar cal;
    public static int broadcastCode = 0;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView date;
        public TextView time;
        public ImageView alarm;
        public TextView title;
        View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.date = (TextView) itemView.findViewById(R.id.taskDate);
            this.time = (TextView) itemView.findViewById(R.id.taskTime);
            this.alarm = (ImageView) itemView.findViewById(R.id.alarm);
            this.title = (TextView) itemView.findViewById(R.id.taskDesc);
            // this.imgAttach = (ImageView) itemView.findViewById(R.id.imgAttach);
//
        }
    }

    public TaskListAdapter(ArrayList<HashMap<String, String>> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);

        ///view.setOnClickListener(Dashboard1.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        resultp = dataSet.get(listPosition);
        TextView date = holder.date;
        final ImageView imageView = holder.alarm;
        final TextView time = holder.time;
        final TextView title = holder.title;
        String d = resultp.get(TaskList.DATE);
        String t = resultp.get(TaskList.TIME);
        final String task = resultp.get(TaskList.TITLE);
        final String path = resultp.get(TaskList.KEY);
        // String product_id = dataSet.get(listPosition).getPid();
        date.setText(t);
        title.setText(task);
        time.setText(d);
        String[] splitStrd = d.split("\\s+");
        String[] splitStr = t.split("\\s+");
        String da = splitStrd[0];
        String ta = splitStr[0];
        String[] items1 = da.split("/");
        String day = items1[0];
        String month = items1[1];
        String year = items1[2];
        final int d1 = Integer.parseInt(day);
        final int m = Integer.parseInt(month);
        final int y = Integer.parseInt(year);
        String[] separated = ta.split(":");
        String hour = separated[0];
        String min = separated[1];
        //String sec = separated[2];
        final int hr = Integer.parseInt(hour);
        final int mins = Integer.parseInt(min);
        final String alarm = resultp.get(TaskList.ALARM);
        if (alarm.equals("1")) {
            imageView.setImageResource(R.drawable.ic_on_alert);
        } else {
            imageView.setImageResource(R.drawable.ic_alert);
        }
        imageView.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (alarm.equals("1")) {
                    Toast.makeText(context, "Reminder already set!", Toast.LENGTH_LONG).show();
                } else {
                    imageView.setImageResource(R.drawable.ic_on_alert);
                    new AlertDialog.Builder(context)
                            .setIcon(R.drawable.logo)
                            .setTitle(R.string.app_name)
                            .setMessage("Task Reminder : " + task)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    File f = new File("/data/data/com.rupsiagarwal.myfirbaseapp/shared_prefs/broadcastcode.xml");
                                    if (f.exists()) {
                                        SharedPreferences bcode = context.getSharedPreferences("broadcastcode", MODE_PRIVATE);
                                        int code = bcode.getInt("broadcastCode",1);
                                        broadcastCode=code;
                                        broadcastCode++;
                                    }
                                    else
                                    {
                                        broadcastCode++;
                                    }
                                    Firebase.setAndroidContext(context);

                                    SharedPreferences t = context.getSharedPreferences("info", MODE_PRIVATE);
                                    String name = t.getString("name", "");
                                    //reference = new Firebase("https://myfirebaseapp-d7ee6.firebaseio.com/messages/");
                                    Firebase reference1 = new Firebase("https://myfirebaseapp-d7ee6.firebaseio.com/task_list/" + name + "/" + path + "/");
                                    HashMap<String, Object> result = new HashMap<>();
                                    result.put("alarm", "1");
                                    reference1.updateChildren(result);

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(System.currentTimeMillis());
                                    calendar.clear();
                                    calendar.set(y, m, d1, hr, mins);
                                    //Date e = new Date(cal.getTimeInMillis());
                                    String caltime = String.valueOf(calendar.getTimeInMillis());
                                    Intent intent = new Intent(context, AlarmReceiver.class);
                                    intent.putExtra("time", caltime);
                                    intent.putExtra("bcode", broadcastCode);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                                    SharedPreferences bcode = context.getSharedPreferences("broadcastcode", MODE_PRIVATE);
                                    SharedPreferences.Editor editchat = bcode.edit();
                                    editchat.putInt("broadcastCode", broadcastCode);
                                    editchat.commit();
                                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                    Toast.makeText(context, "Task Reminder set successfully!", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
