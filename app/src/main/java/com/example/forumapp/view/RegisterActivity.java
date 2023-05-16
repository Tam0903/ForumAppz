package com.example.forumapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forumapp.R;
import com.example.forumapp.controller.RegisterController;
import com.example.forumapp.model.UserModel;
import com.example.forumapp.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

public class RegisterActivity extends AppCompatActivity {
    // Views
    TextView signInBtn;
    EditText usernameEdt, emailEdt, passwordEdt, confirmPasswordEdt;
    ImageView passwordIcon, confirmPasswordIcon;
    String name, email, password, confirmPassword;
    Button signUpBtn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Init views
        signInBtn = findViewById(R.id.signInBtn);
        signUpBtn = findViewById(R.id.signUpBtn);
        usernameEdt = findViewById(R.id.usernameEdt);
        emailEdt = findViewById(R.id.emailEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        confirmPasswordEdt = findViewById(R.id.confirmPasswordEdt);
        passwordIcon = findViewById(R.id.passwordIcon);
        confirmPasswordIcon = findViewById(R.id.confirmPasswordIcon);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        pd = new ProgressDialog(this);
        // handle SignInTextView click
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        // handle visible password click
        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordEdt.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    // If password is visible then Hide it
                    passwordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // change Icon
                    passwordIcon.setImageResource(R.drawable.baseline_visibility_off_24);
                }else {
                    passwordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordIcon.setImageResource(R.drawable.baseline_visibility_24);
                }
            }
        });
        confirmPasswordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(confirmPasswordEdt.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    // If password is visible then Hide it
                    confirmPasswordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // change Icon
                    confirmPasswordIcon.setImageResource(R.drawable.baseline_visibility_off_24);
                }else {
                    confirmPasswordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmPasswordIcon.setImageResource(R.drawable.baseline_visibility_24);
                }
            }
        });

        // handle SignUpBtn click
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = usernameEdt.getText().toString().trim();
                password = passwordEdt.getText().toString().trim();
                confirmPassword = confirmPasswordEdt.getText().toString().trim();
                email = emailEdt.getText().toString().trim();

                UserModel newUser = UserModel.getInstance();
                if (!newUser.setUsername(name)) {
                    Toast.makeText(RegisterActivity.this, "Username required !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(name.length() > 20) {
                    Toast.makeText(RegisterActivity.this, "Name is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newUser.setEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "Email must be typed in the right format !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newUser.setPassword(password)) {
                    Toast.makeText(RegisterActivity.this, "Password must have at least 8 characters !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length() > 30) {
                    Toast.makeText(RegisterActivity.this, "Password is too long!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password is not confirmed !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                pd.setMessage("Creating account...");
                pd.show();

                registerUser(name, email, password);
            }
        });
    }
    private void registerUser(String name, String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    UserModel users = new UserModel();
                    users.setUserId(user.getUid());
                    users.setEmail(email);
                    users.setPassword(password);
                    // Setup userDisplayName
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    user.updateProfile(profileUpdates);
                    users.setUsername(name);
                    database.getReference().child("Users").child(user.getUid()).setValue(users);
                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RegisterActivity.this, "Please verify your email to login", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                }else{
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}