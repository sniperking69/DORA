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

import com.aputech.dora.Model.Note;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.aputech.dora.ui.ProfileDisplayActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;

public class SearchAdapter extends FirestoreRecyclerAdapter<User, SearchAdapter.UserHolder> {
    private Context mContext;
    public SearchAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context mContext) {
        super(options);
        this.mContext=mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull final User model) {
            holder.img.setVisibility(View.VISIBLE);
            Glide
                    .with(mContext)
                    .load(model.getProfileUrl())
                    .into(holder.img);
            holder.textViewTitle.setText(model.getUserName());
            holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProfileDisplayActivity.class);
                    intent.putExtra("user_id",model.getUserid());
                    mContext.startActivity(intent);
                }
            });

    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search,
                parent, false);
        return new UserHolder(v);
    }

    class UserHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        MaterialButton followbutton;
        ImageView img;
        public UserHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.nametitle);
            followbutton = itemView.findViewById(R.id.followss);
            img =itemView.findViewById(R.id.profiledisplay);
        }
    }
}