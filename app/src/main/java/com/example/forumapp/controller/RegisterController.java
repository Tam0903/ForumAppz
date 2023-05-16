package com.example.forumapp.controller;

import android.content.Context;
import android.text.Editable;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.forumapp.model.UserModel;
import com.example.forumapp.utils.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterController {
    public static RegisterController registerController;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseAuth auth;

    private RegisterController(){

    }

    public void process(Context context, String username, String password, String email){
        // Toast.makeText(context,"You are registering !!!",Toast.LENGTH_SHORT).show();
        UserModel newuser = UserModel.getInstance();

        newuser.setEmail(email);
        newuser.setPassword(password);
        newuser.setUsername(username);

        addDataToCloud(context, newuser);
    }

    public static RegisterController getInstance(){
        if(registerController == null) registerController = new RegisterController();
        return registerController;
    }

    private void addDataToCloud(Context context, UserModel newuser){
        String emailChecked = newuser.getEmail();

        db.collection("Users")
        .whereEqualTo("email",emailChecked)
        .get()
        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.getDocuments().isEmpty()) {
                    Toast.makeText(context, "This email is already used by an another account !!!", Toast.LENGTH_SHORT).show();
                } else {
                    insertQuery(context, newuser);
                }
            }
        });
    }

    private void insertQuery(Context context, UserModel newuser){
        Map<String, String> userData = new HashMap<>();
        userData.put("email", newuser.getEmail());
        userData.put("password", newuser.getPassword());
        userData.put("username", newuser.getUsername());

        db
        .collection("Users")
        .add(userData)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(context, "New user created !!!", Toast.LENGTH_SHORT).show();
                SessionManager sessionManager = SessionManager.getInstance(context);
                sessionManager.saveRegisterSession(true);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Register failed !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
