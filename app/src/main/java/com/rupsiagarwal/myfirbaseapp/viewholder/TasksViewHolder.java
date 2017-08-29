package com.rupsiagarwal.myfirbaseapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rupsiagarwal.myfirbaseapp.R;
import com.rupsiagarwal.myfirbaseapp.models.Tasks;

/**
 * Created by Rupsi.Agarwal on 8/2/2017.
 */

public class TasksViewHolder extends RecyclerView.ViewHolder{

    public TextView date;
    public TextView time;
    public ImageView alarm;
    public TextView title;


    public TasksViewHolder(View itemView) {
        super(itemView);

        date = (TextView) itemView.findViewById(R.id.taskDate);
        time = (TextView) itemView.findViewById(R.id.taskTime);
        alarm = (ImageView) itemView.findViewById(R.id.alarm);
        title = (TextView) itemView.findViewById(R.id.taskDesc);
    }

    public void bindToPost(Tasks tasks, View.OnClickListener starClickListener) {
        date.setText(tasks.add_date);
        time.setText(tasks.total_time);
        title.setText(tasks.task);

        alarm.setOnClickListener(starClickListener);
    }
}
