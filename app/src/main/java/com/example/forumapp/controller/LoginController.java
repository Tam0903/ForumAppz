package com.example.forumapp.controller;


import android.content.Context;
import android.text.Editable;
import android.widget.Toast;

import com.example.forumapp.model.UserModel;
import com.example.forumapp.utils.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginController {
//    private static LoginController loginController;
//    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    private LoginController(){
//
//    }
//
//    public static LoginController getInstance(){
//        if(loginController == null) loginController = new LoginController();
//        return loginController;
//    }
//
//    public void process(Context context, String email, String password){
//        UserModel loginUser = UserModel.getInstance();
//
//        loginUser.setEmail(email);
//        loginUser.setPassword(password);
//
//        login(context,loginUser);
//    }
//
//    private void login(Context context, UserModel loginuser){
//        if(this.accountVerified(loginuser)) {
//            SessionManager sessionManager = SessionManager.getInstance(context);
//            sessionManager.saveLoginSession(loginuser);
//        } else {
//            Toast.makeText(context, "Login failed !!!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private boolean accountVerified(UserModel loginuser){
//        String emailChecked = loginuser.getEmail();
//        String passwordChecked = loginuser.getPassword();
//        UserModel loginUser = loginuser;
//
//        db.collection("Users")
//        .whereEqualTo("email",emailChecked)
//        .whereEqualTo("password",passwordChecked)
//        .get()
//        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                if(!queryDocumentSnapshots.getDocuments().isEmpty()) {
//                    loginUser.setUserId(queryDocumentSnapshots.getDocuments().get(0).getId());
//                    //UserModel.setLoginState(loginUser,true);
//                }
//            }
//        });
        //return loginUser.getLoginState();
//    }
}
