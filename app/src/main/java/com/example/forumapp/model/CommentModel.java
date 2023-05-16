package com.example.forumapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Date;

public class CommentModel {
    private String uId, name, profileImage, comment, commentId;
    private Object timestamp;

    /* Constructor */
    public CommentModel() {}
    public CommentModel(String comment, String name, String profileImage, String uId, Object timestamp, String commentId) {
        this.uId = uId;
        this.name = name;
        this.profileImage = profileImage;
        this.comment = comment;
        this.commentId = commentId;
        this.timestamp = timestamp;
    }
    public CommentModel(String comment, String name, String profileImage, String uId, String commentId) {
        this.uId = uId;
        this.name = name;
        this.profileImage = profileImage;
        this.comment = comment;
        this.timestamp = ServerValue.TIMESTAMP;
        this.commentId = commentId;
    }
    /* Getter & Setter */
    public String getUId() {return uId;}
    public void setUId(String id) {this.uId = id;}
    public String getUName() {return name;}
    public void setUName(String name) {this.name = name;}
    public String getProfileImage() {return profileImage;}
    public void setProfileImage(String profileImage) {this.profileImage = profileImage;}
    public String getComment() {return comment;}
    public void setComment(String comment) {this.comment = comment;}
    public Object getTimestamp() {return timestamp;}
    public void setTimestamp(Object timestamp) {this.timestamp = timestamp;}
    public String getCommentId() {return commentId;}
    public void setCommentId(String commentId) {this.commentId = commentId;}
}
