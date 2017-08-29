package com.rupsiagarwal.myfirbaseapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Rupsi
 */

@IgnoreExtraProperties
public class LoginUser {

    public String name;
    public String email;
    public String password;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User1.class)
    public LoginUser() {
    }

    public LoginUser(String email, String password,String name) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
