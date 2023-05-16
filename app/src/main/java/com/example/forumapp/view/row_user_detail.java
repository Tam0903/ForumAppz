package com.example.forumapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.forumapp.R;
import com.example.forumapp.databinding.ActivityRowUserDetailBinding;
import com.example.forumapp.model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class row_user_detail extends AppCompatActivity {
    ActivityRowUserDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_user_detail);
        binding = ActivityRowUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        UserModel userModel = (UserModel) intent.getSerializableExtra("model");
        binding.rowUsernameText.setText(userModel.getUsername());
        binding.rowUserEmail.setText(userModel.getEmail());

        if(!userModel.getProfile().equals("")) Glide.with(binding.getRoot()).load(userModel.getProfile()).into(binding.rowUserImage);
        binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(userModel.getUserId());
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(row_user_detail.this, adminHomeDashBoard.class));
            }
        });
    }

    private void deleteUser(String userid){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Deleting user");
        progressDialog.setMessage("Loading");
        progressDialog.show();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference();
        userDatabase.child("Users").child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Toast.makeText(row_user_detail.this, "User deleted !!!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(row_user_detail.this, adminHomeDashBoard.class));
            }
        });
    }

    private void loadProducts(String userid){
        DatabaseReference productDatabase = FirebaseDatabase.getInstance().getReference();
        productDatabase.child("Products").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

            }
        });
    }
}