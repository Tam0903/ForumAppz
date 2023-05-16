package com.example.forumapp.model;

import android.text.Editable;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserModel implements Serializable {
    private static UserModel user;

    private String emailPattern = "^([a-zA-Z.0-9]+)@gmail\\.com$";
    private Pattern pattern;
    private Matcher matcher;

    private String userId, username, email;
    private String profile = "";
    private String password = "";
    public UserModel(){}
    public UserModel(String userId, String name, String email, String profile, String password){
        this.userId = userId;
        this.username = name;
        this.profile = profile;
        this.email = email;
        this.password = password;
    }
    public UserModel(String userId, String name, String email, String profile){
        this.userId = userId;
        this.username = name;
        this.profile = profile;
        this.email = email;
        this.password = "";
    }

    public static UserModel getInstance(){
        if(user == null) user = new UserModel();
        return user;
    }

    public boolean setUsername(String username) {
        this.username = username;
        if(username == null) return false;
        return true;
    }
    public boolean setEmail(String email){
        pattern = Pattern.compile(emailPattern);
        matcher = pattern.matcher(email);

        if(matcher.find()) {
            this.email = email;
            return true;
        }
        return false;
    }

    public void setUserId(String id){
        this.userId = id;
    }
    public String getUserId(){
        return this.userId;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPassword(){return this.password;}
    public String getUsername(){
        return this.username;
    }
    public String getProfile() {
        return profile;
    }
    public void setProfile(String profile) {
        if(profile == ""){
            this.profile = "";
            return;
        }
        this.profile = profile;
    }


    public boolean setPassword(String password){
        if(password.length() < 8) return false;
        this.password = md5(password);
        return true;
    }

    private String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
