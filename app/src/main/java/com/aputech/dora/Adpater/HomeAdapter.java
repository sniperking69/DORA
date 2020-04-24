package com.aputech.dora.Adpater;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.aputech.dora.Model.Note;
import com.aputech.dora.ui.CommentActivity;
import com.aputech.dora.ui.DispPostLocation;
import com.aputech.dora.ui.ProfileDisplayActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends FirestoreRecyclerAdapter<Note, HomeAdapter.NoteHolder> {
    private Context mContext;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference notebookRef;
    public HomeAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context mContext) {
        super(options);
        this.mContext=mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, final int position, @NonNull final Note model) {
        holder.down.setText(String.valueOf(model.getDownnum()));
        if (model.getUserid().equals(auth.getUid())){
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(position);
                }
            });
            holder.edit.setVisibility(View.VISIBLE);
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if (model.getLocation()!=null){
            holder.LocationIcon.setImageResource(R.drawable.ic_locationhappy);
            holder.LocationIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,DispPostLocation.class);
                    intent.putExtra("lat",model.getLocation().getLatitude());
                    intent.putExtra("lng",model.getLocation().getLongitude());
                    mContext.startActivity(intent);
                }
            });
        }else{
            holder.LocationIcon.setImageResource(R.drawable.ic_locationsad);

        }
        holder.up.setText(String.valueOf(model.getUpnum()));

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileDisplayActivity.class);
                intent.putExtra("user_id",model.getUserid());
                mContext.startActivity(intent);
            }
        });
        holder.user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileDisplayActivity.class);
                intent.putExtra("user_id",model.getUserid());
                mContext.startActivity(intent);
            }
        });
        if ( model.getUserid() != null) {
            notebookRef = db.collection("Users").document(model.getUserid());
            notebookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    final User user = task.getResult().toObject(User.class);
                    holder.user_name.setText(user.getUserName());
                    Glide
                            .with(mContext)
                            .load(user.getProfileUrl())
                            .into(holder.profile);
                    if (user.getPosts() !=null){
                        if (user.getUserlevel()==0){
                            Glide
                                    .with(mContext)
                                    .load(R.drawable.ic_grade)
                                    .into(holder.level);
                        }
                        if (user.getUserlevel()==1){
                            Glide
                                    .with(mContext)
                                    .load(R.drawable.ic_grade1)
                                    .into(holder.level);
                        }
                        if (user.getUserlevel()==2){
                            Glide
                                    .with(mContext)
                                    .load(R.drawable.ic_grade2)
                                    .into(holder.level);
                        }
                    }
                    if( model.getType()==1){
                        holder.textViewDescription.setText(model.getDescription());
                        holder.time.setText(String.valueOf(model.getUptime()));
                        holder.Commentbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent =new Intent(mContext, CommentActivity.class);
                                intent.putExtra("coll",model.getRefComments().getParent().getPath());
                                intent.putExtra("doc",model.getRefComments().getId());
                                intent.putExtra("post",model);
                                intent.putExtra("user",user);
                                mContext.startActivity(intent);
                            }
                        });
                    }
                    if(model.getType()==2){
                        holder.img.setVisibility(View.VISIBLE);
                        Glide
                                .with(mContext)
                                .load(model.getImageUrl())
                                .into(holder.img);
                        holder.textViewDescription.setText(model.getDescription());
                        holder.time.setText(String.valueOf(model.getUptime()));


                    }
                }
            });

        }

        holder.up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean ifdown= model.getDownvote().contains(auth.getUid());
                boolean ifup= model.getUpvote().contains(auth.getUid());
                if (!ifup && !ifdown){
                    getSnapshots().getSnapshot(position).getReference().update("upvote", FieldValue.arrayUnion(auth.getUid()));
                    getSnapshots().getSnapshot(position).getReference().update("upnum", model.getUpnum()+1);
                    updatePriority(position,model.getUpnum()+1,model.getDownnum(),model.getCommentnum());

                }
                if (ifup && !ifdown){
                    getSnapshots().getSnapshot(position).getReference().update("upvote", FieldValue.arrayRemove(auth.getUid()));
                    getSnapshots().getSnapshot(position).getReference().update("upnum", model.getUpnum()-1);
                    updatePriority(position,model.getUpnum()-1,model.getDownnum(),model.getCommentnum());
                }
                if(!ifup && ifdown){
                    getSnapshots().getSnapshot(position).getReference().update("downvote", FieldValue.arrayRemove(auth.getUid()));
                    getSnapshots().getSnapshot(position).getReference().update("downnum", model.getDownnum()-1);
                    getSnapshots().getSnapshot(position).getReference().update("upvote", FieldValue.arrayUnion(auth.getUid()));
                    getSnapshots().getSnapshot(position).getReference().update("upnum", model.getUpnum()+1);
                    updatePriority(position,model.getUpnum()+1,model.getDownnum()-1,model.getCommentnum());
                }
            }
        });
        holder.down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ifdown= model.getDownvote().contains(auth.getUid());
                boolean ifup= model.getUpvote().contains(auth.getUid());
                if (!ifup && !ifdown){
                    getSnapshots().getSnapshot(position).getReference().update("downvote", FieldValue.arrayUnion(auth.getUid()));
                    getSnapshots().getSnapshot(position).getReference().update("downnum", model.getDownnum()+1);
                    updatePriority(position,model.getUpnum(),model.getDownnum()+1,model.getCommentnum());
                }
                if (ifdown && !ifup){
                    getSnapshots().getSnapshot(position).getReference().update("downvote", FieldValue.arrayRemove(auth.getUid()));
                    getSnapshots().getSnapshot(position).getReference().update("downnum", model.getDownnum()-1);
                    updatePriority(position,model.getUpnum(),model.getDownnum()-1,model.getCommentnum());
                }if(!ifdown && ifup){
                    getSnapshots().getSnapshot(position).getReference().update("upvote", FieldValue.arrayRemove(auth.getUid()));
                    getSnapshots().getSnapshot(position).getReference().update("upnum", model.getUpnum()-1);
                    getSnapshots().getSnapshot(position).getReference().update("downvote", FieldValue.arrayUnion(auth.getUid()));
                    getSnapshots().getSnapshot(position).getReference().update("downnum", model.getDownnum()+1);
                    updatePriority(position,model.getUpnum()-1,model.getDownnum()+1,model.getCommentnum());
                }

            }
        });
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_feed,
                parent, false);
        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView user_name;
        TextView textViewDescription;
        TextView time;
        MaterialButton up,down;
        ImageView img;
        ImageView level;
        ImageView LocationIcon,delete,edit;
        CircleImageView profile;
        MaterialButton Commentbutton;
        public NoteHolder(View itemView) {
            super(itemView);
            up= itemView.findViewById(R.id.upbutton);
            down= itemView.findViewById(R.id.downbutton);
            edit =itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            user_name = itemView.findViewById(R.id.user_name);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            time = itemView.findViewById(R.id.time);
            level= itemView.findViewById(R.id.level);
            LocationIcon = itemView.findViewById(R.id.locate);
            profile=itemView.findViewById(R.id.poster_profile);
            img =itemView.findViewById(R.id.img);
            Commentbutton= itemView.findViewById(R.id.comment);
        }
    }
    private void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }
    private void updatePriority(int position,int up,int down,int commentnum){
        getSnapshots().getSnapshot(position).getReference().update("priority",up*0.4+down*0.2+commentnum*0.4);

    }
}
