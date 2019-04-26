package com.example.inspirationalr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class LoginCredStorage {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public LoginCredStorage(Activity activity) {
        super();
        sharedPreferences = activity.getSharedPreferences("PREP_FILE", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setValue(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }
    public void setBooleanValue(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }
    public String getValue(String key) {
        return sharedPreferences.getString(key, "");
    }
    public Boolean getBooleanValue(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}


