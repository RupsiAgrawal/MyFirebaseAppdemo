package com.rupsiagarwal.myfirbaseapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    ArrayList<HashMap<String, String>> dataSet;
    HashMap<String, String> resultp = new HashMap<String, String>();
    Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        ImageView img;
        View view;
        View v1;
        RelativeLayout r1;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.txtName);
            this.r1 = (RelativeLayout) itemView.findViewById(R.id.r1);
            this.img = (ImageView) itemView.findViewById(R.id.imageView3);
            this.v1 = (View) itemView.findViewById(R.id.viewHeaderLine);
//
        }
    }

    public UserListAdapter(ArrayList<HashMap<String, String>> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userlist_item, parent, false);

        ///view.setOnClickListener(Dashboard1.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        resultp = dataSet.get(listPosition);
        TextView name = holder.name;
        RelativeLayout r = holder.r1;
        ImageView img = holder.img;
        View  v1 = holder.v1;
        final String ID = resultp.get(UsersList.ID);
        final String EMAIL = resultp.get(UsersList.EMAIL);
        final String NAME = resultp.get(UsersList.NAME);

        SharedPreferences t = context.getSharedPreferences("info", MODE_PRIVATE);
        final String myname = t.getString("name", "");
        if(NAME.equals(myname))
        {
            r.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            v1.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
        }
        else
        {
            name.setText(NAME);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ChatList.class);
                i.putExtra("uid",ID);
                i.putExtra("to_name",NAME);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
