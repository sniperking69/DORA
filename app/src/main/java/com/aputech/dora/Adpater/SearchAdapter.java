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
import com.aputech.dora.ui.ProfileSettings;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class SearchAdapter extends FirestoreRecyclerAdapter<User, SearchAdapter.UserHolder> {
    private Context mContext;
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private FirestoreRecyclerOptions<User> op;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    public SearchAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context mContext) {
        super(options);
        this.mContext=mContext;
        this.op=options;
    }

    @Override
    protected void onBindViewHolder(@NonNull final UserHolder holder, int position, @NonNull final User model) {
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
                    if (model.getUserid().equals(auth.getUid())){
                        intent.putExtra("act",0);
                    }else{
                        intent.putExtra("act",1);
                    }

                    mContext.startActivity(intent);
                }
            });
//            if (model.getFollowers()!=null){
//                if (model.getFollowers().contains(auth.getUid())){
//                    holder.followbutton.setText("UNFOLLOW");
//                }
//            }
            if (model.getUserid().equals(auth.getUid())){
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