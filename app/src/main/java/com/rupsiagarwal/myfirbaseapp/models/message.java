package com.rupsiagarwal.myfirbaseapp.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START message_class]
@IgnoreExtraProperties
public class message {

    public static String to;
    public static String from;
    public static String message;
    public static String datetime;
    public static String name;
    public String uid_name;

    public message() {
        // Default constructor required for calls to DataSnapshot.getValue(message.class)
    }

    public message(String from, String to, String date, String message) {
        this.from = from;
        this.to = to;
        this.datetime = date;
        this.message = message;
        this.name = name;
    }

    // [START message_to_map]
//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("from", from);
//        result.put("to", to);
//        result.put("message", message);
//        result.put("name", name);
//        result.put("datetime", datetime);
//        return result;
//    }
    // [END message_to_map]

}
// [END message_class]
