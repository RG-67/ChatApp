package com.project.chatapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    String userID = "";
    SharedPreferences sharedPreferences;
    Context context;

    public Session(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("login details", Context.MODE_PRIVATE);
    }

    public String getUserID() {
        userID = sharedPreferences.getString("userID", "");
        return userID;
    }

    public void setUserID(String userID) {
        sharedPreferences.edit().putString("userID", userID).commit();
        this.userID = userID;
    }
}
