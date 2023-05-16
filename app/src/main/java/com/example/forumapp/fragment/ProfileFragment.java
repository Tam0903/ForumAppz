package com.example.forumapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forumapp.R;
import com.example.forumapp.adapters.PostAdapter;
import com.example.forumapp.model.PostModel;
import com.example.forumapp.view.EditProfile;
import com.example.forumapp.view.MainActivity;
import com.example.forumapp.view.adminHomeDashBoard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    // Views
    ImageView avatarIv;
    TextView nameTv, emailTv;
    private CircleImageView profieImageView;
    RecyclerView postsRecyclerView;
    List<PostModel> postsList;
    PostAdapter postAdapter;
    String uId;
    LinearLayout btn1,btn2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // init firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        // init views
        avatarIv = view.findViewById(R.id.avatarIv);
        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.emailTv);
        profieImageView = view.findViewById(R.id.avatarIv);
        btn1 = view.findViewById(R.id.editProfileLn);
        btn2 = view.findViewById(R.id.activityLn);
        postsRecyclerView = view.findViewById(R.id.recyclerview_post);
        if(user == null){
            Toast.makeText(getContext(), "Something went wrong! User's detail are not available at the moment", Toast.LENGTH_SHORT).show();
        } else{
            showUserProfile(user);
        }
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });

        // status admin
        if(!user.getEmail().equals("admin@gmail.com")) btn2.setVisibility(View.INVISIBLE);
        if(user.getEmail().equals("admin@gmail.com")) {
            //btn2.setVisibility(0);
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), adminHomeDashBoard.class));
                }
            });
        }
        postsList = new ArrayList<>();

        checkUserStatus();
        loadMyPosts();
        return view;
    }
    private void loadMyPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postsRecyclerView.setLayoutManager(layoutManager);
        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("uid").equalTo(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList = new ArrayList<>();
                for(DataSnapshot ds: snapshot.getChildren()){
                    PostModel postModel = ds.getValue(PostModel.class);

                    postsList.add(postModel);

                    postAdapter = new PostAdapter(getActivity(), postsList);
                    // set this adapter to recyclerView
                    postsRecyclerView.setAdapter(postAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showUserProfile(FirebaseUser user){
        String userId = user.getUid();
        databaseReference.child(userId).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.getValue().toString();
                emailTv.setText(email);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child(userId).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue().toString();
                nameTv.setText(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(user.getPhotoUrl() != null){
//                    Picasso.get().load(user.getPhotoUrl().toString()).into(avatarIv);
//                }
                String image = snapshot.child("profile").getValue().toString();
                if (!TextUtils.isEmpty(image)) {
                    Picasso.get().load(image).into(avatarIv);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus(){
        // Get current user
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            uId = user.getUid();
        }else{
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
}