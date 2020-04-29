package com.aputech.dora.Adpater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.aputech.dora.ui.ProfileDisplayActivity;
import com.aputech.dora.ui.ProfileSettings;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class SAdapter extends RecyclerView.Adapter<SAdapter.UserViewHolder> {
    private ArrayList<User> UserList;
    private ArrayList<User> UserListFull;
    private Context mContext;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageView img;
        UserViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.nametitle);
            img =itemView.findViewById(R.id.profiledisplay);
        }
    }

    public SAdapter(ArrayList<User> UserList, Context mContext) {
        this.UserList = UserList;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search,
                parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User currentItem = UserList.get(position);
        holder.img.setVisibility(View.VISIBLE);
        Glide
                .with(mContext)
                .load(currentItem.getProfileUrl())
                .into(holder.img);
        holder.textViewTitle.setText(currentItem.getUserName());
        holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileDisplayActivity.class);
                intent.putExtra("user_id",currentItem.getUserid());
                if (currentItem.getUserid().equals(auth.getUid())){
                    intent.putExtra("act",0);
                }else{
                    intent.putExtra("act",1);
                }

                mContext.startActivity(intent);
            }
        });

        }



    @Override
    public int getItemCount() {
        return UserList.size();
    }

    public void filterList(ArrayList<User> filteredList) {
        UserList = filteredList;
        notifyDataSetChanged();
    }
}