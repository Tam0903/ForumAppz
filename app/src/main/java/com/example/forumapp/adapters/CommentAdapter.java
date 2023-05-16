package com.example.forumapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forumapp.R;
import com.example.forumapp.model.CommentModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    Context context;
    List<CommentModel> comments;
    String myId, postId;

    public CommentAdapter(Context context, List<CommentModel> comments) {
        this.context = context;
        this.comments = comments;
    }

    public CommentAdapter(Context context, List<CommentModel> comments, String myId, String postId) {
        this.context = context;
        this.comments = comments;
        this.myId = myId;
        this.postId = postId;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int i) {
        // get data
        String name = comments.get(i).getUName();
        String comment = comments.get(i).getComment();
        String profileImage = comments.get(i).getProfileImage();
        String uId = comments.get(i).getUId();
        Object time = comments.get(i).getTimestamp();
        String commentId = comments.get(i).getCommentId();

        holder.nameTv.setText(name);
        holder.commentTv.setText(comment);
        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_default_img).into(holder.profileIv);
        }catch (Exception e) {}

        // convert timestamp to dd/MM/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis((Long) time);
        String commentTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.dateTv.setText(commentTime);

        // comment clicked listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myId.equals(uId)){
                    // my comment
                    // Show delete dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    builder.setTitle("Delete Comment");
                    builder.setMessage("Are you sure you want to delete this comment?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // delete comment
                            DeleteComment(commentId);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                }else{
                    // not my comment
                }
            }
        });
    }
    private void DeleteComment(String commentId){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(postId);
        ref.child(commentId).removeValue();
    }
    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        // view from row_comment.xml
        ImageView profileIv;
        TextView nameTv, commentTv, dateTv;
        public CommentViewHolder(View itemView){
            super(itemView);
            profileIv = itemView.findViewById(R.id.profileCommentIv);
            nameTv = itemView.findViewById(R.id.nameCommentTv);
            commentTv = itemView.findViewById(R.id.commentTv);
            dateTv = itemView.findViewById(R.id.dateCommentTv);
        }
    }

}
