package com.example.forumapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forumapp.R;
import com.example.forumapp.adapters.CommentAdapter;
import com.example.forumapp.adapters.PostAdapter;
import com.example.forumapp.fragment.ProfileFragment;
import com.example.forumapp.model.CommentModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class PostDetailsActivity extends AppCompatActivity {
    // UI Views
    private ImageView postImageIv, imageUserIv, namePictureIv;
    private TextView nameTv, titleTv, descriptionTv, postDateTv;
    private EditText commentEt;
    private Button addCommentBtn;

    private String postId, commentId;
    private String comment_content, uid, uname, uimg = "";
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    // actionbar
    private ActionBar actionBar;
    RecyclerView RvComments;
    List<CommentModel> commentList;
    CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        // Init actionbar
        actionBar = getSupportActionBar();
        // add back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Init UI Views
        titleTv = findViewById(R.id.titleTv);
        postImageIv = findViewById(R.id.postImageIv);
        descriptionTv = findViewById(R.id.descriptionTv);
        nameTv = findViewById(R.id.nameTv);
        namePictureIv = findViewById(R.id.namePictureIv);
        postDateTv = findViewById(R.id.postDateTv);
        addCommentBtn = findViewById(R.id.addCommentBtn);
        commentEt = findViewById(R.id.commentEt);
        imageUserIv = findViewById(R.id.imageUserIv);
        RvComments = findViewById(R.id.commentsRv);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        String postImage = getIntent().getExtras().getString("postImage");
        String postTitle = getIntent().getExtras().getString("postTitle");
        String description = getIntent().getExtras().getString("description");
        String name = getIntent().getExtras().getString("name");
        String pictureName = getIntent().getExtras().getString("pictureName");
        String postTime = getIntent().getExtras().getString("postTime");
        postId = getIntent().getExtras().getString("postId");

        titleTv.setText(postTitle);
        nameTv.setText(name);
        try {
            Picasso.get().load(pictureName).placeholder(R.drawable.ic_default_img).into(namePictureIv);
        }catch (Exception e) {}
        postDateTv.setText(postTime);
        database.getReference("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("profile").getValue().toString();
                if (!TextUtils.isEmpty(image)) {
                    Picasso.get().load(image).into(imageUserIv);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        if(!postImage.equals("noImage")){
            postImageIv.setVisibility(View.VISIBLE);
            Picasso.get().load(postImage).into(postImageIv);
        }
        descriptionTv.setText(description);

        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCommentBtn.setVisibility(View.INVISIBLE);
                DatabaseReference commentRef = database.getReference("Comments").child(postId).push();
                comment_content = commentEt.getText().toString();
                uid = user.getUid();
                uname = user.getDisplayName();
                commentId = commentRef.getKey();
                if(TextUtils.isEmpty(comment_content)){
                    Toast.makeText(PostDetailsActivity.this, "Enter comment...", Toast.LENGTH_SHORT).show();
                    addCommentBtn.setVisibility(View.VISIBLE);
                    return;
                }
                // Sử dụng CompletableFuture để đợi cho giá trị của uimg được trả về từ cơ sở dữ liệu
                CompletableFuture<String> uimgFuture = new CompletableFuture<>();
                database.getReference("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String image = snapshot.child("profile").getValue().toString();
                        if (!TextUtils.isEmpty(image)) {
                            uimgFuture.complete(image);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        uimgFuture.completeExceptionally(error.toException());
                    }
                });

                // Sau khi giá trị của uimg được trả về, tạo đối tượng CommentModel
                uimgFuture.thenApply(uimg -> {
                    CommentModel comment = new CommentModel(comment_content, uname, uimg, uid, commentId);
                    commentRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(PostDetailsActivity.this, "comment added", Toast.LENGTH_SHORT).show();
                            commentEt.setText("");
                            addCommentBtn.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostDetailsActivity.this, "fail to add comment", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return null;
                });
            }
        });

        // init RecyclerView Comments;
        initRvComments();
    }
    private void initRvComments() {
        RvComments.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference commentRef = database.getReference("Comments").child(postId);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                commentList = new ArrayList<>();
                for(DataSnapshot ds: snapshot.getChildren()){
                    CommentModel comment = ds.getValue(CommentModel.class);
                    commentList.add(comment);
                    Collections.sort(commentList, new Comparator<CommentModel>() {
                        @Override
                        public int compare(CommentModel o1, CommentModel o2) {
                            return Long.compare((Long)o2.getTimestamp(), (Long)o1.getTimestamp());
                        }
                    });
                    commentAdapter = new CommentAdapter(getApplicationContext(), commentList, user.getUid(), postId);
                    RvComments.setAdapter(commentAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostDetailsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkUserStatus(){
        // get current user
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            // user is signed in here

        }else{
            // user not signed in, go to main activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed(); //go to previous activity
        return super.onSupportNavigateUp();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // get item id
        int id = item.getItemId();
        if (id == R.id.action_logout){
            auth.signOut();
            checkUserStatus();
        }
        if (id == R.id.action_add_post){
            startActivity(new Intent(this, AddPostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}