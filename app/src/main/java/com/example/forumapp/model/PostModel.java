package com.example.forumapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class PostModel {
    static private FirebaseDatabase db = FirebaseDatabase.getInstance();
    static private DatabaseReference dbRef = db.getReference();

    private String pId, pTitle, pDescr, pImage, pTime, uid, uEmail, uDp, uName;
    public PostModel() {}
    public PostModel(String pId, String pTitle, String pDesc, String pImage, String pTime, String uid, String uEmail, String uDp, String uName) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescr = pDesc;
        this.pImage = pImage;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
    }

    public static FirebaseDatabase getDb() {
        return db;
    }
    public static DatabaseReference getDbRef() {
        return dbRef;
    }

    public String getpId() {
        return pId;
    }
    public String getpTitle() {
        return pTitle;
    }
    public String getpDescr() {
        return pDescr;
    }
    public String getpImage() {
        return pImage;
    }
    public String getpTime() {
        return pTime;
    }
    public String getUid() {
        return uid;
    }
    public String getuEmail() {
        return uEmail;
    }
    public String getuDp() {
        return uDp;
    }
    public String getuName() {
        return uName;
    }
    public void setpId(String pId) {this.pId = pId;}
    public void setpTitle(String pTitle) {this.pTitle = pTitle;}
    public void setpDescr(String pDescr) {this.pDescr = pDescr;}
    public void setpImage(String pImage) {this.pImage = pImage;}
    public void setpTime(String pTime) {this.pTime = pTime;}
    public void setUid(String uid) {this.uid = uid;}
    public void setuEmail(String uEmail) {this.uEmail = uEmail;}
    public void setuDp(String uDp) {this.uDp = uDp;}
    public void setuName(String uName) {this.uName = uName;}
}
