package com.example.forumapp.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.forumapp.R;
import com.example.forumapp.adapters.UserAdapter;
import com.example.forumapp.databinding.ActivityAdminHomeDashBoardBinding;
import com.example.forumapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class adminHomeDashBoard extends AppCompatActivity {
    private DatabaseReference userDatabase;
    private UserAdapter userAdapter;
    private SearchView searchView;
    ActivityAdminHomeDashBoardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_dash_board);
        binding = ActivityAdminHomeDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.myToolbar);
        userAdapter = new UserAdapter(this);
        binding.userRecycler.setAdapter(userAdapter);
        binding.userRecycler.setLayoutManager(new LinearLayoutManager(this));

        binding.navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(adminHomeDashBoard.this, DashboardActivity.class));
            }
        });
        getUsers();
    }

    private void getUsers() {
        userDatabase = FirebaseDatabase.getInstance().getReference();

        userDatabase.child("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(adminHomeDashBoard.this, "Data loaded failed !!!", Toast.LENGTH_SHORT).show();
                } else {
                    DataSnapshot users = task.getResult();
                    for(DataSnapshot user: users.getChildren()) {
                        /*
                        ImageView userImage = findViewById(R.id.row_username_image);
                        if(!users.child("profile").equals("")){
                            Picasso.get().load(users.child("profile").toString()).into(userImage);
                        } */

                        userAdapter.addUser(user.getValue(UserModel.class));
                    }
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_bar, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) menu.findItem(R.id.admin_search_bar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            public boolean onQueryTextSubmit(String query){
                userAdapter.getFilter().filter(query);
                //userAdapter.clearUserArray();
                return false;
            }

            public boolean onQueryTextChange(String newText){
                userAdapter.getFilter().filter(newText);
                //userAdapter.clearUserArray();
                return false;
            }
        });

        return true;
    }

        /*
        FirebaseFirestore.getInstance()
        .collection("user")
        .get()
        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                  List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                  for (DocumentSnapshot ds : dsList) {
                      userAdapter.addUser(ds.toObject(UserModel.class));
                  }
              }
          }
        );
    } */
}