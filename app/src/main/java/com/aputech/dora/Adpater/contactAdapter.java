package com.aputech.dora.Adpater;


import android.content.Context;
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
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class contactAdapter extends RecyclerView.Adapter<contactAdapter.UserViewHolder> {
    ContactInterface contactInterface;
    private ArrayList<User> UserList;
    private Context mContext;

    public contactAdapter(ArrayList<User> UserList, Context mContext, ContactInterface contactInterface) {
        this.UserList = UserList;
        this.mContext = mContext;
        this.contactInterface = contactInterface;
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
        if (currentItem.getProfileUrl() != null) {
            Glide
                    .with(mContext)
                    .load(currentItem.getProfileUrl())
                    .into(holder.img);
        }

        holder.textViewTitle.setText(currentItem.getUserName());
        holder.user_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactInterface.onClick(currentItem);
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

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageView img;
        CardView user_card;

        UserViewHolder(View itemView) {
            super(itemView);
            user_card = itemView.findViewById(R.id.user_card);
            textViewTitle = itemView.findViewById(R.id.nametitle);
            img = itemView.findViewById(R.id.profiledisplay);
        }
    }
}