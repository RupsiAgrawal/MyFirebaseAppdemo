package com.rupsiagarwal.myfirbaseapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {
    int MID=0;
    private static int MODE_PRIVATE=0;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        //long when = System.currentTimeMillis();
//
//        SharedPreferences alarmtime = context.getSharedPreferences("alarmtime", MODE_PRIVATE);
//        String cal=alarmtime.getString("alarmtime", "");

//        long when=Long.parseLong(cal);
        //Log.d("timer",cal);
        String time=intent.getStringExtra("time");
        long when=Long.parseLong(time);
        String bcode=intent.getStringExtra("bcode");
        int code=Integer.valueOf(bcode);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, TaskList.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(context, alarmSound);
        mp.start();
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.logo)
                .setContentTitle("Alarm")
                .setContentText("Task Reminder").setSound(alarmSound)
                 .setWhen(when)
                 //.setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(MID, mNotifyBuilder.build());
        MID++;

    }

}