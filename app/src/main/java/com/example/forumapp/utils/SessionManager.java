package com.example.forumapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.forumapp.model.UserModel;

public class SessionManager {
    private static SessionManager sessionManager;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sessionEditor;
    private String SharedPrefName = "login session";
    private String SessionKey = "sessionId";


    private SessionManager(Context context){
        this.sharedPref = context.getSharedPreferences(SharedPrefName,Context.MODE_PRIVATE);
        this.sessionEditor = sharedPref.edit();
    }

    public static SessionManager getInstance(Context context){
        if(sessionManager == null) sessionManager = new SessionManager(context);
        return sessionManager;
    }

    public void saveLoginSession(UserModel user){
        String userId = user.getUserId();
        this.sessionEditor.putString(SessionKey,userId).commit();
    }

    public String getLoginSession(){
        return this.sharedPref.getString(SessionKey,null);
    }

    public void saveRegisterSession(boolean state){
        this.sessionEditor.putBoolean("registered", state).commit();
    }

    public boolean getRegisterSession(){
        return this.sharedPref.getBoolean("registered",false);
    }
}
