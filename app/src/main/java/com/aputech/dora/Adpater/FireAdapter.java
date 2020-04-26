package com.aputech.dora.Adpater;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class FireAdapter extends FirestoreRecyclerAdapter<Note, FireAdapter.NoteHolder> {
    private Context mContext;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference notebookRef;
    public FireAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context mContext) {
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
                    // Build an AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    // Set a title for alert dialog
                    builder.setTitle("Delete Post");

                    // Ask the final question
                    builder.setMessage("Are you sure to Delete This Post?");

                    // Set the alert dialog yes button click listener
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when user clicked the Yes button
                            // Set the TextView visibility GONE
                            deleteItem(position);
                            Toast.makeText(mContext,
                                    "PostDeleted",Toast.LENGTH_SHORT).show();

                        }
                    });

                    // Set the alert dialog no button click listener
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when No button clicked
                        }
                    });

                    AlertDialog dialog = builder.create();
                    // Display the alert dialog on interface
                    dialog.show();
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
                    if( model.getType()==1){
                        holder.textViewDescription.setText(model.getDescription());
                        holder.time.setText(String.valueOf(model.getUptime()));
                        holder.Commentbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent =new Intent(mContext, CommentActivity.class);
                                intent.putExtra("coll",getSnapshots().getSnapshot(position).getReference().getParent().getPath());
                                intent.putExtra("doc",getSnapshots().getSnapshot(position).getReference().getId());
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
                final DocumentReference upReference =db.collection("Posts").document(model.getRefComments()).collection("upvote").document(auth.getUid());
                final DocumentReference downReference =db.collection("Posts").document(model.getRefComments()).collection("downvote").document(auth.getUid());
               upReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if (task.isSuccessful()) {
                           DocumentSnapshot document = task.getResult();
                           if (document.exists()) {
                               upReference.delete();
                               getSnapshots().getSnapshot(position).getReference().update("upnum", model.getUpnum()-1);
                               updatePriority(position,model.getUpnum()-1,model.getDownnum(),model.getCommentnum());
                           } else {
                              // dum temp = new dum();
                              // upReference.set(temp);
                               upReference.update("exists","yes");
                               getSnapshots().getSnapshot(position).getReference().update("upnum", model.getUpnum()+1);
                               downReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {

                                   }
                               })
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()){
                                                   getSnapshots().getSnapshot(position).getReference().update("downnum", model.getDownnum()-1);
                                                   updatePriority(position,model.getUpnum()+1,model.getDownnum()-1,model.getCommentnum());
                                               }else{
                                                   updatePriority(position,model.getUpnum()+1,model.getDownnum(),model.getCommentnum());
                                               }

                                           }
                                       });
                           }
                       } else {
                           Log.d("DROP CHAT", "Failed with: ", task.getException());
                       }
                   }
               });
            }
        });
        holder.down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference upReference =db.collection("Posts").document(model.getRefComments()).collection("upvote").document(auth.getUid());
                final DocumentReference downReference =db.collection("Posts").document(model.getRefComments()).collection("downvote").document(auth.getUid());
                downReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                downReference.delete();
                                getSnapshots().getSnapshot(position).getReference().update("downnum", model.getDownnum()-1);
                                updatePriority(position,model.getUpnum(),model.getDownnum()-1,model.getCommentnum());
                            } else {
                              //  dum temp = new dum();
                               // downReference.set(temp);
                                getSnapshots().getSnapshot(position).getReference().update("downnum", model.getDownnum()+1);
                                upReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            getSnapshots().getSnapshot(position).getReference().update("upnum", model.getUpnum()-1);
                                            updatePriority(position,model.getUpnum()-1,model.getDownnum()+1,model.getCommentnum());

                                        }else{
                                            updatePriority(position,model.getUpnum(),model.getDownnum()+1,model.getCommentnum());
                                        }
                                    }
                                });

                            }
                        } else {
                            Log.d("DROP CHAT", "Failed with: ", task.getException());
                        }
                    }
                });
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
        // getSnapshots().getSnapshot(position).getReference().collection("comments");
    }
    private void updatePriority(int position,int up,int down,int commentnum){
        getSnapshots().getSnapshot(position).getReference().update("priority",up*0.4+down*0.2+commentnum*0.4);

    }
}
