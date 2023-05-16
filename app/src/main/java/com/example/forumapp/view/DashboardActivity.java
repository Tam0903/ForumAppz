package com.example.forumapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.browser.trusted.Token;
import androidx.browser.trusted.Token;
import androidx.fragment.app.FragmentTransaction;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.forumapp.R;
import com.example.forumapp.fragment.HomeFragment;
import com.example.forumapp.fragment.ProfileFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.checkerframework.checker.units.qual.A;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth auth;
    String mUId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        auth = FirebaseAuth.getInstance();

        // home fragment transaction (default, on start)
        actionBar.setTitle("Home");
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, homeFragment, "");
        ft1.commit();

        // Bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // handle item clicks
                switch(item.getItemId()) {
                    case R.id.nav_home:
                        // home fragment transaction
                        actionBar.setTitle("Home");
                        HomeFragment homeFragment = new HomeFragment();
                        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                        ft1.replace(R.id.content, homeFragment, "");
                        ft1.commit();
                        return true;
                    case R.id.nav_profile:
                        // profile fragment transaction
                        actionBar.setTitle("Profile");
                        ProfileFragment profileFragment = new ProfileFragment();
                        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                        ft2.replace(R.id.content, profileFragment, "");
                        ft2.commit();
                        return true;
                }
                return false;
            }
        });

        checkUserStatus();

    }
    private void checkUserStatus(){
        // get current user
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            // user is signed in stay here
            mUId = user.getUid();

            // save UId of currently signed in user in shared preferences
//            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString("Current_USERID", mUId);
//            editor.apply();

            // update Token
            updateToken(FirebaseMessaging.getInstance().getToken().toString());
        }else{
            // user is not signed in, go to main activity
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        // check on start of app
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token mToken = new Token(token);
//        ref.child(mUId).setValue(mToken);
    }

    // Inflate options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout){
            auth.signOut();
            // Logout Google
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
            // Remove information Google login when user logs out
            googleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Take actions after signing out of Google
                            checkUserStatus();
                        }
                    });
        }
        else if (id == R.id.action_add_post){
            startActivity(new Intent(DashboardActivity.this, AddPostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}