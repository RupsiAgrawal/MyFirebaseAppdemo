package com.rupsiagarwal.myfirbaseapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    // private ArrayList<DataModel_Dashboard> dataSet;
    //List<DataModel_Comments> dataSet;
    ArrayList<HashMap<String, String>> dataSet;
    int mExpandedPosition = -1;
    Context context;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textMessage, textdate, txtDateimg;
        ImageView imageViewIcon, imgExpand;
        RelativeLayout r1;
        public LinearLayout content, contentimg;
        public LinearLayout contentWithBG, contentWithBGimg;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            //this.textName = (TextView) itemView.findViewById(R.id.txtName);
            this.textMessage = (TextView) itemView.findViewById(R.id.txtMessage);
            this.textdate = (TextView) itemView.findViewById(R.id.txtDate);
            this.txtDateimg = (TextView) itemView.findViewById(R.id.txtDateimg);
            this.image = (ImageView) itemView.findViewById(R.id.chatImage);
            content = (LinearLayout) itemView.findViewById(R.id.content);
            contentWithBG = (LinearLayout) itemView.findViewById(R.id.contentWithBackground);
            contentimg = (LinearLayout) itemView.findViewById(R.id.contentimg);
            contentWithBGimg = (LinearLayout) itemView.findViewById(R.id.contentWithBackgroundimg);
        }
    }

    public ChatAdapter(ArrayList<HashMap<String, String>> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);

        ///view.setOnClickListener(Dashboard1.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        resultp = dataSet.get(listPosition);
        //TextView textViewName = holder.textName;
        TextView textMesaage = holder.textMessage;
        TextView textDate = holder.textdate;
        TextView textDateimg = holder.txtDateimg;
        ImageView Chatimg = holder.image;
        LinearLayout content = holder.content;
        LinearLayout contentWithBG = holder.contentWithBG;
        LinearLayout contentimg = holder.contentimg;
        LinearLayout contentWithBGimg = holder.contentWithBGimg;
        String date = resultp.get(ChatList.DATE);
        String id = resultp.get(ChatList.FID);
        String comment = resultp.get(ChatList.MESSAGE);
        String img = resultp.get(ChatList.IMAGE);
        byte[] imageAsBytes = Base64.decode(img.getBytes(), Base64.DEFAULT);

        BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        if (img.equals("")) {
            Chatimg.setVisibility(View.GONE);
            textDateimg.setVisibility(View.GONE);
            contentimg.setVisibility(View.GONE);
            contentWithBGimg.setVisibility(View.GONE);

        } else {
            Chatimg.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            textDateimg.setText(date);
            textMesaage.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
            contentWithBG.setVisibility(View.GONE);
            SharedPreferences userinfo = context.getSharedPreferences("info", MODE_PRIVATE);
            String uid = userinfo.getString("name", "");

            if (!id.equals(uid)) {
//                android.view.ViewGroup.LayoutParams layoutParams = Chatimg.getLayoutParams();
//                layoutParams.width = 300;
//                layoutParams.height = 300;
//                Chatimg.setLayoutParams(layoutParams);

                LinearLayout.LayoutParams layoutParamsimg = (LinearLayout.LayoutParams) contentWithBGimg.getLayoutParams();
                layoutParamsimg.gravity = Gravity.LEFT;
                contentWithBGimg.setLayoutParams(layoutParamsimg);

                RelativeLayout.LayoutParams lpimg = (RelativeLayout.LayoutParams) contentimg.getLayoutParams();
                lpimg.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lpimg.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                contentimg.setLayoutParams(lpimg);
                layoutParamsimg = (LinearLayout.LayoutParams) Chatimg.getLayoutParams();
                layoutParamsimg.gravity = Gravity.LEFT;
                Chatimg.setLayoutParams(layoutParamsimg);

                layoutParamsimg = (LinearLayout.LayoutParams) textDateimg.getLayoutParams();
                layoutParamsimg.gravity = Gravity.LEFT;
                textDateimg.setLayoutParams(layoutParamsimg);
            } else {
                LinearLayout.LayoutParams layoutParamsimg = (LinearLayout.LayoutParams) contentWithBGimg.getLayoutParams();
                layoutParamsimg.gravity = Gravity.RIGHT;
                contentWithBGimg.setLayoutParams(layoutParamsimg);

                RelativeLayout.LayoutParams lpimg = (RelativeLayout.LayoutParams) contentimg.getLayoutParams();
                lpimg.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                lpimg.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                contentimg.setLayoutParams(lpimg);
                layoutParamsimg = (LinearLayout.LayoutParams) Chatimg.getLayoutParams();
                layoutParamsimg.gravity = Gravity.RIGHT;
                Chatimg.setLayoutParams(layoutParamsimg);

                layoutParamsimg = (LinearLayout.LayoutParams) textDateimg.getLayoutParams();
                layoutParamsimg.gravity = Gravity.RIGHT;
                textDateimg.setLayoutParams(layoutParamsimg);
            }
        }
        if (comment.equals("")) {

        } else {

            SharedPreferences userinfo = context.getSharedPreferences("info", MODE_PRIVATE);
            String uid = userinfo.getString("name", "");

            if (!id.equals(uid)) {

                //contentWithBG.setBackgroundResource(R.drawable.in_message_bg);
                //textViewName.setText("Buyer: " + uname);
                textMesaage.setText(comment);
                textDate.setText(date);
                contentWithBG.setBackgroundResource(R.drawable.out_message_bg);
                //r1.setBackgroundResource(R.color.white);
                //textViewName.setTextColor(ContextCompat.getColor(context, R.color.white));
                textMesaage.setTextColor(ContextCompat.getColor(context, R.color.white));
                textDate.setTextColor(ContextCompat.getColor(context, R.color.chatdatewhite));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) contentWithBG.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                contentWithBG.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) content.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                content.setLayoutParams(lp);
                layoutParams = (LinearLayout.LayoutParams) textMesaage.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                textMesaage.setLayoutParams(layoutParams);

                layoutParams = (LinearLayout.LayoutParams) textDate.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                textDate.setLayoutParams(layoutParams);

            } else {
                //contentWithBG.setBackgroundResource(R.drawable.out_message_bg);
                //textViewName.setText("Admin");
                textMesaage.setText(comment);
                textDate.setText(date);
                //r1.setBackgroundResource(R.color.dot_light_screen2);
                //textViewName.setTextColor(ContextCompat.getColor(context, R.color.commentlist1));
                textMesaage.setTextColor(ContextCompat.getColor(context, R.color.colorfill));
                textDate.setTextColor(ContextCompat.getColor(context, R.color.chatdatewhite));
                contentWithBG.setBackgroundResource(R.drawable.in_message_bg);


                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) contentWithBG.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                contentWithBG.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) content.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                content.setLayoutParams(lp);
                layoutParams = (LinearLayout.LayoutParams) textMesaage.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                textMesaage.setLayoutParams(layoutParams);

                layoutParams = (LinearLayout.LayoutParams) textDate.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                textDate.setLayoutParams(layoutParams);


            }

        }

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
