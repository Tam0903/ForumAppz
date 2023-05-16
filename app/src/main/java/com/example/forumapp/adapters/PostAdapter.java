package com.example.forumapp.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.text.InputType;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forumapp.R;
import com.example.forumapp.model.PostModel;
import com.example.forumapp.view.AddPostActivity;
import com.example.forumapp.view.DashboardActivity;
import com.example.forumapp.view.MainActivity;
import com.example.forumapp.view.PostDetailsActivity;
import com.example.forumapp.view.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyHolder>{
    Context context;
    List<PostModel> postList;
    String myUid;
    private boolean isImageVisible = true;

    public PostAdapter(Context context, List<PostModel> postList) {
        this.context = context;
        this.postList = postList;
        this.myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        // get data
        String uId =postList.get(i).getUid();
        String uEmail =postList.get(i).getuEmail();
        String uName =postList.get(i).getuName();
        String uDp =postList.get(i).getuDp();
        String pId =postList.get(i).getpId();
        String pTitle =postList.get(i).getpTitle();
        String pDescr =postList.get(i).getpDescr();
        String pImage =postList.get(i).getpImage();
        String pTimeStamp =postList.get(i).getpTime();

        // convert timestamp to dd/MM/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        // set data
        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescr);
        // set user dp
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_img).into(holder.uPictureIv);
        }catch (Exception e) {}
        // set post image
        if(pImage.equals("noImage")){
            // hide imageView
                holder.pImageIv.setVisibility(View.GONE);
                isImageVisible = false;
        }else{
            try{
                Picasso.get().load(pImage).into(holder.pImageIv);
                holder.pImageIv.setVisibility(View.VISIBLE);
                isImageVisible = false;
            }catch (Exception e) {}
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreOptions(holder.moreBtn, uId, myUid, pId, pImage,uEmail,uName,uDp,pDescr,pTitle);
            }
        });

        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uid", uId);
                context.startActivity(intent);
            }
        });
    }

    private void showMoreOptions(ImageButton moreBtn, String uId, String myUid, String pId, String uEmail, String uName,String uDp, String pTitle, String pDescr, String pImage) {
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);
        if(uId.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
        }else{
            popupMenu.getMenu().add(Menu.NONE, 2, 0, "Report");
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 0){
                    // delete is clicked
                    beginDelete(pId, pImage);
                } else if (id == 1) {
                    // edit is clicked
                    Intent intent = new Intent(context, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);
                }else if (id == 2) { showReportDialog(pId,uId);}
                return false;
            }
            
        });
        popupMenu.show();
    }
    private void showReportDialog(String pId, String reporterId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Why are you reporting this post?");

        // Create a list of predefined report reasons
        String[] reportReasons = {"Bài viết không liên quan", "Ngôn từ gây thù ghét", "Vi phạm bản quyền", "Lý do khác"};

        // Set the list of report reasons as items in the dialog
        builder.setItems(reportReasons, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                String reason;
                if (position == reportReasons.length - 1) {
                    showCustomReasonDialog(pId, reporterId);
                } else {
                    reason = reportReasons[position];
                    reportPost(pId, reason, myUid, reporterId);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }
    private void showCustomReasonDialog(String pId, String reporterId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Nhập lí do của bạn");

        EditText editText = new EditText(context);
        editText.setHint("Please enter your reason here...");
        builder.setView(editText);

        builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String reason = editText.getText().toString().trim();
                reportPost(pId, reason, myUid, reporterId);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private void reportPost(String pId, String reason,String myUid,String reporterId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reports").child(pId);
        String reportId = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("reportId", reportId);
        hashMap.put("pId", pId);
        hashMap.put("reason", reason);
        hashMap.put("reporterId", myUid);
        hashMap.put("reportedUserId", reporterId);

        reference.child(reportId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Reported successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void beginDelete(String pId, String pImage) {
        if(pImage.equals("noImage")){
            deleteWithoutImage(pId);
        }else{
            deleteWithImage(pId, pImage);
        }
    }

    private void deleteWithImage(String pId, String pImage) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()) {
                                    ds.getRef().removeValue();
                                }
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteWithoutImage(String pId) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");
        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // view holder class
    public class MyHolder extends RecyclerView.ViewHolder{
        // view from row_post.xml
        ImageView uPictureIv, pImageIv;
        TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv;
        ImageButton moreBtn;
        LinearLayout profileLayout;

        public  MyHolder(@NonNull View itemView) {
            super(itemView);
            // init views
            uPictureIv = itemView.findViewById(R.id.userPictureIv);
            uNameTv = itemView.findViewById(R.id.userNameTv);
            pImageIv = itemView.findViewById(R.id.postImageIv);
            pTimeTv = itemView.findViewById(R.id.postTimeTv);
            pTitleTv = itemView.findViewById(R.id.postTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.postDescriptionTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);
            // set the default visibility of postImageView
            if(isImageVisible) {
                pImageIv.setVisibility(View.VISIBLE);
            } else {
                pImageIv.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailsActivity.class);
                    int position = getAdapterPosition();
                    intent.putExtra("postTitle", postList.get(position).getpTitle());
                    intent.putExtra("postImage", postList.get(position).getpImage());
                    intent.putExtra("description", postList.get(position).getpDescr());
                    intent.putExtra("name", postList.get(position).getuName());
                    intent.putExtra("pictureName", postList.get(position).getuDp());
                    intent.putExtra("postId", postList.get(position).getpId());
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(postList.get(position).getpTime()));
                    String postTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                    intent.putExtra("postTime", postTime);
                    context.startActivity(intent);
                }
            });
        }
    }
}
