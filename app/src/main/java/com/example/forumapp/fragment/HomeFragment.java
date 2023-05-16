package com.example.forumapp.fragment;

import static androidx.core.app.ActivityCompat.invalidateOptionsMenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.forumapp.R;
import com.example.forumapp.adapters.PostAdapter;
import com.example.forumapp.model.PostModel;
import com.example.forumapp.view.AddPostActivity;
import com.example.forumapp.view.LoginActivity;
import com.example.forumapp.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private Menu menu;
    private SearchView searchView;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView postRecyclerView;
    List<PostModel> postList;
    PostAdapter postAdapter;
    public HomeFragment(){
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // init
        auth = FirebaseAuth.getInstance();
        SearchView search = view.findViewById(R.id.search_post);

        // recycler view and its properties
        postRecyclerView = view.findViewById(R.id.postsRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        // set layout to recycleView
        postRecyclerView.setLayoutManager(layoutManager);
        loadPosts();

        // searchView to search posts by post title/ description
        // search listener
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // called when user press search button
                if(!TextUtils.isEmpty(s)){
                    searchPosts(s);
                }else{
                    loadPosts();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // called as and when user press any letters
                if(!TextUtils.isEmpty(s)){
                    searchPosts(s);
                }else{
                    loadPosts();
                }
                return false;
            }
        });
        return view;
    }

    private void loadPosts(){
        databaseReference = firebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()){
                    PostModel post = ds.getValue(PostModel.class);
                    postList.add(post);
                }
                postAdapter = new PostAdapter(getActivity(), postList);
                postRecyclerView.setAdapter(postAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void searchPosts(String searchQuery){
        databaseReference = firebaseDatabase.getInstance().getReference("Posts");
        // get all data from this ref
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    PostModel postModel = ds.getValue(PostModel.class);

                    if(postModel.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) || postModel.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(postModel);
                    }

                    // adapter and set adapter to recyclerview
                    postAdapter = new PostAdapter(getActivity(), postList);
                    postRecyclerView.setAdapter(postAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // in case of error
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // inflate options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        // inflating menu
        this.menu = menu;
        menuInflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }
    // handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // get item id
        int id = item.getItemId();
        if(id == R.id.action_logout){
            auth.signOut();
            checkUserStatus();
        }
        if(id == R.id.action_add_post){
            startActivity(new Intent(getActivity(), AddPostActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
    public Menu getMenu() {
        return menu;
    }


    private void checkUserStatus(){
        // get current user
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            // user is signed in here
        }else{
            // user not signed in, go to main activity
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
    }
}