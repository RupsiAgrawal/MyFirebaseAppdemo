package com.rupsiagarwal.myfirbaseapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<BaseItem> android;
    private Context context;
    View view;

    public DataAdapter(Context context, ArrayList<BaseItem> android) {
        this.android = android;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.tv_android.setText(android.get(i).getname());
        viewHolder.img_android.setImageResource(android.get(i).getimage());
        viewHolder.badges.setVisibility(View.INVISIBLE);
        if (i == 5) {
            viewHolder.badges.setVisibility(View.VISIBLE);
        }
        // Picasso.with(context).load(android.get(i).getImage()).resize(240, 120).into(viewHolder.img_android);
    }

    @Override
    public int getItemCount() {
        return android.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_android;
        private TextView badges;
        private ImageView img_android;

        public ViewHolder(View view) {
            super(view);

            tv_android = (TextView) view.findViewById(R.id.text_name);
            badges = (TextView) view.findViewById(R.id.badge_notification_1);
            img_android = (ImageView) view.findViewById(R.id.img);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getPosition()) {
//
                        case 0:

                            Intent addtask = new Intent(context, AddtaskActivity.class);
                            addtask.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            addtask.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(addtask);
                            break;
                        case 1:

                            Intent tasklist = new Intent(context, TaskList.class);
                            tasklist.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            tasklist.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(tasklist);
                            break;
                        case 2:
                            Intent chat = new Intent(context, UsersList.class);
                            chat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            chat.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(chat);
                            break;
                        case 3:
                            File sharedPreferenceFile = new File("/data/data/com.rupsiagarwal.myfirbaseapp/shared_prefs/");
                            File[] listFiles = sharedPreferenceFile.listFiles();
                            for (File filelist : listFiles) {
                                filelist.delete();
                            }
                            FirebaseAuth.getInstance().signOut();
                            Intent logout = new Intent(context, SignInActivity.class);
                            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            logout.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(logout);
                            break;
                        case 4:

                            break;
                        default:
                            break;
                    }

                    // Log.d("RecyclerView", "onClick：" + getPosition());
                }
            });
        }
    }

}