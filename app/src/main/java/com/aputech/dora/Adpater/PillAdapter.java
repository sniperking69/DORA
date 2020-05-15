package com.aputech.dora.Adpater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.aputech.dora.ui.ProfileDisplayActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PillAdapter extends RecyclerView.Adapter<PillAdapter.UserViewHolder> {
    private ArrayList<User> UserList;
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageView remove;
        UserViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.contactname);
            remove =itemView.findViewById(R.id.remove);


        }
    }

    public PillAdapter(ArrayList<User> UserList) {
        this.UserList = UserList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_pills,
                parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        final User currentItem = UserList.get(position);
        holder.textViewTitle.setText(currentItem.getUserName());
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserList.remove(position);
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return UserList.size();
    }

    public User getItem(int position) {
        return UserList.get(position);
    }

    public ArrayList<User> getUserList(){
        return UserList;
    }


}
