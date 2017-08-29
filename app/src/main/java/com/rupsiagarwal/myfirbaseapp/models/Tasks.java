package com.rupsiagarwal.myfirbaseapp.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START Tasks_class]
@IgnoreExtraProperties
public class Tasks {

    public String id;
    public String uid;
    public String task;
    public String add_date;
    public String type;
    public String total_time;
    public String setalarm;
    //public Map<String, Boolean> stars = new HashMap<>();

    public Tasks() {
        // Default constructor required for calls to DataSnapshot.getValue(Tasks.class)
    }

    public Tasks(String uid, String task, String add_date, String type, String total_time,String setalarm) {
        this.uid = uid;
        this.id = id;
        this.task = task;
        this.add_date = add_date;
        this.type = type;
        this.total_time = total_time;
        this.setalarm = setalarm;
    }

    // [START Tasks_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("id", id);
        result.put("task", task);
        result.put("total_time", total_time);
        result.put("add_date", add_date);
        result.put("type", type);
        result.put("setalarm", setalarm);
        return result;
    }
    // [END Tasks_to_map]

}
// [END Tasks_class]
