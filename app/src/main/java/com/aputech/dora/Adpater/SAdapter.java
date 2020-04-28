package com.aputech.dora.Adpater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.aputech.dora.ui.ProfileDisplayActivity;
import com.aputech.dora.ui.ProfileSettings;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SAdapter extends RecyclerView.Adapter<SAdapter.UserViewHolder> {
    private ArrayList<User> UserList;
    private ArrayList<User> UserListFull;
    private Context mContext;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        MaterialButton followbutton;
        ImageView img;
        UserViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.nametitle);
            followbutton = itemView.findViewById(R.id.followss);
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
        if (currentItem.getUserid().equals(auth.getUid())){
            holder.followbutton.setText("Settings");
            holder.followbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProfileSettings.class);
                    mContext.startActivity(intent);
                }
            });

        }else{
//                holder.followbutton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (model.getFollowers()!=null){
//                            if (model.getFollowers().contains(auth.getUid())){
//                                DocumentReference documentRef= db.collection("Users").document(auth.getUid());
//                                DocumentReference documentReference= db.collection("Users").document(model.getUserid());
//                                documentReference.update("followers", FieldValue.arrayRemove(auth.getUid()));
//                                documentRef.update("following", FieldValue.arrayRemove(model.getUserid()));
//                                holder.followbutton.setText("FOLLOW");
//                            }else{
//                                DocumentReference documentRef= db.collection("Users").document(auth.getUid());
//                                DocumentReference documentReference= db.collection("Users").document(model.getUserid());
//                                documentReference.update("followers", FieldValue.arrayUnion(auth.getUid()));
//                                documentRef.update("following", FieldValue.arrayUnion(model.getUserid()));
//                                holder.followbutton.setText("UNFOLLOW");
//                            }
//                        }else{
//                            DocumentReference documentRef= db.collection("Users").document(auth.getUid());
//                            DocumentReference documentReference= db.collection("Users").document(model.getUserid());
//                            documentReference.update("followers", FieldValue.arrayUnion(auth.getUid()));
//                            documentRef.update("following", FieldValue.arrayUnion(model.getUserid()));
//                        }
//
//                    }
//                });

        }
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