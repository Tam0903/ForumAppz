package com.example.forumapp.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forumapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

public class EditProfile extends AppCompatActivity {

    private CircleImageView profieImageView;
    private TextView profileChangeBtn,btnclose,btnsave;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicaRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageProfilePicaRef = FirebaseStorage.getInstance().getReference().child("Profile Pic");

        profieImageView = findViewById(R.id.profile_image);

        btnclose= findViewById(R.id.btnClose);
        btnsave = findViewById(R.id.btnSave);
        profileChangeBtn = findViewById(R.id.change_profile_btn);

        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this, DashboardActivity.class));
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
                uploadProfileImage();
           }
        });

        profieImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(EditProfile.this);
            }
        });
        profileChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị dialog để nhập tên mới
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
                builder.setTitle("Change Name");
                final EditText input = new EditText(EditProfile.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Lấy tên mới từ input
                        String newName = input.getText().toString();

                        // Cập nhật tên mới lên Firebase
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                            userRef.child("username").setValue(newName);
                            // Setup userDisplayName
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(newName)
                                    .build();
                            user.updateProfile(profileUpdates);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        getUserinfo();
    }
    private void getUserinfo(){
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                    String image = dataSnapshot.child("profile").getValue().toString();
//                    if (!TextUtils.isEmpty(image)) {
//                        Picasso.get().load(image).into(profieImageView);
//                    }
//                    if(dataSnapshot.hasChild("username"))
//                    {
//                        String ChangeName = dataSnapshot.child("username").getValue().toString();
//                        profileChangeBtn.setText(ChangeName);
//                    }
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                    if(dataSnapshot.hasChild("profile"))
                    {
                        String image = dataSnapshot.child("profile").getValue().toString();
                        if(!TextUtils.isEmpty(image)){
                            Picasso.get().load(image).into(profieImageView);
                        }
                    }
                    if(dataSnapshot.hasChild("username"))
                    {
                        String ChangeName = dataSnapshot.child("username").getValue().toString();
                        profileChangeBtn.setText(ChangeName);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProfile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profieImageView.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Erro, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImage()   {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("set your profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        if(imageUri != null)
        {
            final StorageReference fileRef = storageProfilePicaRef
                    .child(mAuth.getCurrentUser().getUid()+".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>(){
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Uri downloadURL =task.getResult();
                    myUri = downloadURL.toString();
                    databaseReference.child(mAuth.getCurrentUser().getUid()).child("profile").setValue(myUri);
                    progressDialog.dismiss();
                }
            }
            });
        }
        else {
            progressDialog.dismiss();
            Toast.makeText(this, "Image not select", Toast.LENGTH_SHORT).show();
        }
    }
}