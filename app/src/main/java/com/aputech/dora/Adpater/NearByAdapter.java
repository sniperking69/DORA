package com.aputech.dora.Adpater;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Model.Post;
import com.aputech.dora.Model.User;
import com.aputech.dora.Model.Vote;
import com.aputech.dora.R;
import com.aputech.dora.ui.DispPostLocation;
import com.aputech.dora.ui.PostDisplay;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NearByAdapter extends FirestoreRecyclerAdapter<Post, NearByAdapter.NoteHolder> {
    private Context mContext;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> FollowingID;

    public NearByAdapter(@NonNull FirestoreRecyclerOptions<Post> options, Context mContext, ArrayList<String> FollowingID) {
        super(options);
        this.FollowingID=FollowingID;
        this.mContext=mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, final int position, @NonNull final Post model) {
        if (FollowingID.contains(model.getRefComments())){
            holder.down.setText(String.valueOf(model.getDownnum()));
            holder.up.setText(String.valueOf(model.getUpnum()));
            holder.textViewDescription.setText(model.getDescription());
            if (model.getTimestamp() != null) {
                Date date = model.getTimestamp();
                String df = DateFormat.getDateFormat(mContext).format(date).concat("  ").concat(DateFormat.getTimeFormat(mContext).format(date));
                holder.time.setText(df);
            }
            if (model.getUserid().equals(auth.getUid())){
                holder.delete.setVisibility(View.VISIBLE);
                holder.edit.setVisibility(View.VISIBLE);
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
            }

            if ( model.getUserid() != null) {
                DocumentReference notebookRef = db.collection("Users").document(model.getUserid());
                notebookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        final User user = task.getResult().toObject(User.class);
                        holder.user_name.setText(user.getUserName());
                        if (user.getProfileUrl()!=null){
                            Glide
                                    .with(mContext)
                                    .load(user.getProfileUrl())
                                    .into(holder.profile);

                        }

                        if (user.getPostnum() < 100) {
                            Glide
                                    .with(mContext)
                                    .load(R.drawable.ic_grade)
                                    .into(holder.level);
                        }
                        if (user.getPostnum() > 100 && user.getPostnum() < 500) {
                            Glide
                                    .with(mContext)
                                    .load(R.drawable.ic_grade1)
                                    .into(holder.level);
                        }
                        if (user.getPostnum() > 500) {
                            Glide
                                    .with(mContext)
                                    .load(R.drawable.ic_grade2)
                                    .into(holder.level);
                        }
                        holder.profile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, ProfileDisplayActivity.class);
                                intent.putExtra("user",user);
                                mContext.startActivity(intent);
                            }
                        });
                        holder.user_name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, ProfileDisplayActivity.class);
                                intent.putExtra("user",user);
                                mContext.startActivity(intent);
                            }
                        });

                    }
                });

            }
            if (model.getType()==1){
                holder.thumbnail.setVisibility(View.GONE);
            }
            if(model.getType()==2){
                holder.thumbnail.setVisibility(View.VISIBLE);
                holder.playButton.setVisibility(View.GONE);
                Glide
                        .with(mContext)
                        .load(model.getImageUrl())
                        .into(holder.img);
            }
            if(model.getType()==3){
                holder.thumbnail.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(model.getVideoUrl()).into(holder.img);
            }
            if(model.getType()==4){
                holder.thumbnail.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(R.drawable.ic_sound).into(holder.img);
            }
            db.collection("Posts").document(model.getRefComments()).collection("vote").document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        holder.up.setIconTintResource(R.color.colorPrimary);
                        holder.down.setIconTintResource(R.color.colorPrimary);
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Vote vote = document.toObject(Vote.class);
                            if (vote.isVotecheck()) {
                                holder.up.setIconTintResource(R.color.level2);
                            }else{
                                holder.down.setIconTintResource(R.color.level2);
                            }
                        }
                    }

                }
            });
        }else{
            holder.card.setVisibility(View.GONE);
            holder.card.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_feed,
                parent, false);
        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView user_name;
        TextView textViewDescription;
        TextView time;
        MaterialButton up,down;
        ImageView img;
        ImageView level;
        ImageView LocationIcon,delete,edit;
        CircleImageView profile;
        View audioview;
        ImageView playButton;
        CardView thumbnail;
        MaterialButton Commentbutton;
        public NoteHolder(View itemView) {
            super(itemView);
            up= itemView.findViewById(R.id.upbutton);
            down= itemView.findViewById(R.id.downbutton);
            edit =itemView.findViewById(R.id.edit);
            playButton=itemView.findViewById(R.id.playbutton);
            thumbnail=itemView.findViewById(R.id.thumbnail);
            audioview =itemView.findViewById(R.id.audiocard);
            delete = itemView.findViewById(R.id.delete);
            user_name = itemView.findViewById(R.id.user_name);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            time = itemView.findViewById(R.id.time);
            level= itemView.findViewById(R.id.level);
            LocationIcon = itemView.findViewById(R.id.locate);
            profile=itemView.findViewById(R.id.poster_profile);
            img =itemView.findViewById(R.id.img);
            Commentbutton= itemView.findViewById(R.id.comment);

            Commentbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(mContext, PostDisplay.class);
                    intent.putExtra("post",getSnapshots().get(getAdapterPosition()).getRefComments());
                    mContext.startActivity(intent);
                }
            });
            textViewDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(mContext, PostDisplay.class);
                    intent.putExtra("post",getSnapshots().get(getAdapterPosition()).getRefComments());
                    mContext.startActivity(intent);
                }
            });
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(mContext, PostDisplay.class);
                    intent.putExtra("post",getSnapshots().get(getAdapterPosition()).getRefComments());
                    mContext.startActivity(intent);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Delete Post");
                    builder.setMessage("Are You Sure You Want to Delete the Post?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DeletePost(getSnapshots().get(getAdapterPosition()).getRefComments());
                            Toast.makeText(mContext,  "Post Deleted",Toast.LENGTH_LONG).show();

                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DocumentReference postrefrence = db.collection("Posts").document(getSnapshots().get(getAdapterPosition()).getRefComments());
                    final DocumentReference Reference = db.collection("Posts").document(getSnapshots().get(getAdapterPosition()).getRefComments()).collection("vote").document(auth.getUid());
                    Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Vote vote = document.toObject(Vote.class);
                                    if (vote.isVotecheck()) {
                                        Reference.delete();
                                        postrefrence.update("upnum", getSnapshots().get(getAdapterPosition()).getUpnum() - 1);
                                        postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getUpnum() - 1) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum()) * 0.2 + getSnapshots().get(getAdapterPosition()).getCommentnum() * 0.4);
                                    }else{
                                        Reference.update("votecheck",true);
                                        postrefrence.update("upnum", getSnapshots().get(getAdapterPosition()).getUpnum() + 1);
                                        postrefrence.update("downnum", getSnapshots().get(getAdapterPosition()).getDownnum() - 1);
                                        postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getUpnum() + 1) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum()-1) * 0.2 + getSnapshots().get(getAdapterPosition()).getCommentnum() * 0.4);
                                    }
                                } else {
                                    Vote v= new Vote();
                                    v.setVotecheck(true);
                                    Reference.set(v);
                                    postrefrence.update("upnum", getSnapshots().get(getAdapterPosition()).getUpnum() + 1);
                                    postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getUpnum() + 1) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum()) * 0.2 + getSnapshots().get(getAdapterPosition()).getCommentnum() * 0.4);
                                }

                            }
                        }

                    });
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Edit Post");

                    final View customLayout =  LayoutInflater.from(mContext).inflate(R.layout.custom_alert, null);
                    builder.setView(customLayout);
                    final EditText editText = customLayout.findViewById(R.id.para);
                    editText.setText(getSnapshots().get(getAdapterPosition()).getDescription());
                    builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!editText.getText().toString().isEmpty()){
                                db.collection("Posts").document(getSnapshots().get(getAdapterPosition()).getRefComments()).update("description",editText.getText().toString());
                                Toast.makeText(mContext,"Post Updated",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(mContext,"Unable to Make Changes Field Empty",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Pass
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DocumentReference postrefrence = db.collection("Posts").document(getSnapshots().get(getAdapterPosition()).getRefComments());
                    final DocumentReference Reference = db.collection("Posts").document(getSnapshots().get(getAdapterPosition()).getRefComments()).collection("vote").document(auth.getUid());
                    Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Vote vote = document.toObject(Vote.class);
                                    if (!vote.isVotecheck()) {
                                        Reference.delete();
                                        postrefrence.update("downnum", getSnapshots().get(getAdapterPosition()).getDownnum() - 1);
                                        postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getUpnum()) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum() -1) * 0.2 + getSnapshots().get(getAdapterPosition()).getCommentnum() * 0.4);
                                    }else{
                                        Reference.update("votecheck",false);
                                        postrefrence.update("downnum", getSnapshots().get(getAdapterPosition()).getDownnum() + 1);
                                        postrefrence.update("upnum", getSnapshots().get(getAdapterPosition()).getUpnum() - 1);
                                        postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getUpnum() - 1) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum()+1) * 0.2 + getSnapshots().get(getAdapterPosition()).getCommentnum() * 0.4);
                                    }
                                } else {
                                    Vote v= new Vote();
                                    v.setVotecheck(false);
                                    Reference.set(v);
                                    postrefrence.update("downnum", getSnapshots().get(getAdapterPosition()).getDownnum() + 1);
                                    postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getDownnum()) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum() + 1) * 0.2 + getSnapshots().get(getAdapterPosition()).getCommentnum() * 0.4);
                                }

                            }
                        }

                    });
                }
            });
        }
    }
    public int ActualSize(){
        int actualSize = 0;
        for (int x=0;x < getSnapshots().size();x++){
            if (FollowingID.contains(getSnapshots().get(x).getRefComments())){
                actualSize +=1;
            }
        }
        return actualSize;
    }
    private void DeletePost(final String Postid) {
        final WriteBatch writeBatch = db.batch();
        //Posts->vote->
        //      comment->vote->
        db.collection("Posts").document(Postid).collection("vote").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    writeBatch.delete(documentSnapshot.getReference());
                }
                db.collection("Posts").document(Postid).collection("comments").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            writeBatch.delete(documentSnapshot.getReference());
                            deleteComment(documentSnapshot.getReference().getId(),Postid);
                        }
                        writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.collection("Posts").document(Postid).delete();
                            }
                        });
                    }
                });

            }

        });
    }
    private void deleteComment(String ref, String post) {
        final WriteBatch writeBatch = db.batch();
        db.collection("Posts").document(post).collection("comments").document(ref).collection("vote").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    writeBatch.delete(documentSnapshot.getReference());
                }
                writeBatch.commit();
            }

        });

    }
}