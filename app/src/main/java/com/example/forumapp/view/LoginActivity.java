package com.example.forumapp.view;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forumapp.model.UserModel;
import com.example.forumapp.R;
import com.facebook.AccessTokenTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser user;

    // Views
    TextView signUpBtn, forgotPasswordBtn;
    EditText emailEdt, passwordEdt;
    Button logInBtn;
    ImageView passwordIcon;
    LinearLayout signInGoogleBtn;
    LinearLayout signInFacebookBtn;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    CallbackManager callbackManager;
    private Editable email,password;
    ProgressDialog pd;
    int RC_SIGN_IN_GOOGLE = 40;
    int RC_SIGN_IN_FACEBOOK = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init views
        signUpBtn = findViewById(R.id.signUpBtn);
        emailEdt = findViewById(R.id.emailEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        logInBtn = findViewById(R.id.logInBtn);
        passwordIcon = findViewById(R.id.passwordIcon);
        forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);
        signInGoogleBtn = findViewById(R.id.googleBtn);
        signInFacebookBtn = findViewById(R.id.facebookBtn);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        pd = new ProgressDialog(this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());

        // handle signInBtn click
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailEdt.getText();
                password = passwordEdt.getText();

                UserModel loginUser = UserModel.getInstance();
                if (!loginUser.setEmail(email.toString().trim())) {
                    Toast.makeText(LoginActivity.this, "Email must be typed in the right format !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!loginUser.setPassword(password.toString().trim())) {
                    Toast.makeText(LoginActivity.this, "Password must have at least 8 characters !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginUser(email.toString().trim(), password.toString().trim());
            }
        });

        // handle signUp TextView click
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        // handle forgotPasswordBtn click
        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();
                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            Toast.makeText(LoginActivity.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
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

        // handle Google login Btn click
        signInGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = gsc.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN_GOOGLE);
            }
        });
        /* handle Facebook login Btn click */
        signInFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {

                                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                boolean isLoggedIn = (accessToken != null && !accessToken.isExpired());
                                if(isLoggedIn){
                                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                    finish();
                                }
                                // handleFacebookAccessToken(accessToken.getToken());
                                handleFacebookAccessToken(loginResult.getAccessToken().getToken());
                                // App code
                                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        // Application code
                                        try{
//                                            user = auth.getCurrentUser();
//                                            UserModel users = new UserModel();
//                                            users.setUserId(object.optString("id", ""));
//                                            users.setUsername(object.optString("name", ""));
//                                            users.setEmail(object.optString("email", ""));
//                                            users.setProfile(object.optJSONObject("picture").optJSONObject("data").optString("url", ""));
//                                            database.getReference().child("Users").child(user.getUid()).setValue(users);
//                                            Toast.makeText(LoginActivity.this, "success facebook", Toast.LENGTH_SHORT).show();
//                                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
//                                            finish();
                                        } catch (FacebookException e) {
                                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields","id,name,email,picture");
                                request.setParameters(parameters);
                                request.executeAsync();
                            }

                            @Override
                            public void onCancel() {
                                // App code
                                Toast.makeText(LoginActivity.this, "cancel login", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                // App code
                                Toast.makeText(LoginActivity.this, "error login", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
    private void loginUser(String email, String password){
        pd.setMessage("Please wait...");
        pd.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(email.equals("admin@gmail.com") && password.equals("anon1111")) {
                                pd.dismiss();
                                startActivity(new Intent(LoginActivity.this, adminHomeDashBoard.class));
                                finish();
                            } else {
                                if(auth.getCurrentUser().isEmailVerified()){
                                    pd.dismiss();
                                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                    finish();
                                }else{
                                    pd.dismiss();
                                    Toast.makeText(LoginActivity.this, "Please verify email to login", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Get the results returned after switching to another activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // After logging in, it passes the data to the new activity
        if(requestCode == RC_SIGN_IN_GOOGLE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                pd.setMessage("Please wait...");
                pd.show();
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void firebaseAuth(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            UserModel users = new UserModel();
                            users.setUserId(user.getUid());
                            users.setUsername(user.getDisplayName());
                            users.setProfile(user.getPhotoUrl().toString());
                            users.setEmail(user.getEmail());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        }else{
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void handleFacebookAccessToken(String idToken){
        FirebaseUser user = auth.getCurrentUser();
        AuthCredential credential = FacebookAuthProvider.getCredential(idToken);
        if(user != null){
            user.linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Liên kết tài khoản thành công
                        Log.d(TAG, "linkWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();
                    } else {
                        // Liên kết tài khoản thất bại
                        Log.w(TAG, "linkWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        auth.signInWithCredential(credential)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        UserModel users = new UserModel();
                        users.setUserId(user.getUid());
                        users.setUsername(user.getDisplayName());
                        users.setProfile(user.getPhotoUrl().toString());
                        users.setEmail(user.getEmail());
                        database.getReference().child("Users").child(user.getUid()).setValue(users);
                        Toast.makeText(LoginActivity.this, "successful with facebook", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "handled failed", Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "signInWithCredential:failure", task.getException());
                    }
                }
            });
    }
}