package com.example.forumapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.forumapp.R;
import com.example.forumapp.databinding.ActivityAdminHomeDashBoardBinding;
import com.example.forumapp.model.UserModel;
import com.example.forumapp.view.row_user_detail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder>{
    private Context context;
    private List<UserModel> userModelList;
    private List<UserModel> userModelOldList;

    public UserAdapter(Context context){
        this.context = context;
        userModelList = new ArrayList<>();
        this.userModelOldList = this.userModelList;
    }

    public void addUser(UserModel userModel){
        userModelList.add(userModel);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        holder.username.setText(userModel.getUsername());
        holder.email.setText(userModel.getEmail());
        if(!userModel.getProfile().equals("")) Glide.with(context).load(userModel.getProfile()).into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, row_user_detail.class);
                intent.putExtra("model", userModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if(strSearch.isEmpty()) {
                    userModelList = userModelOldList;
                } else {
                    List<UserModel> list = new ArrayList<>();

                    for(UserModel user: userModelOldList) {
                        if(user.getUsername().toLowerCase().contains(strSearch.toString())) list.add(user);
                    }
                    userModelList = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = userModelList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(filterResults != null && filterResults.values != null) {
                    userModelList = (List<UserModel>) filterResults.values;
                    notifyDataSetChanged();
                }
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView username,email;
        private ImageView img;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            username=itemView.findViewById(R.id.row_username_text);
            email=itemView.findViewById(R.id.row_user_email);
            img=itemView.findViewById(R.id.row_user_image);
        }
    }
}
