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
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Model.User;
import com.aputech.dora.Model.Vote;
import com.aputech.dora.R;
import com.aputech.dora.Model.Post;
import com.aputech.dora.ui.DispPostLocation;
import com.aputech.dora.ui.PostDisplay;
import com.aputech.dora.ui.ProfileDisplayActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class FireAdapter extends FirestoreRecyclerAdapter<Post, FireAdapter.NoteHolder> {
    private Context mContext;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference notebookRef;
    public FireAdapter(@NonNull FirestoreRecyclerOptions<Post> options, Context mContext) {
        super(options);
        this.mContext=mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, final int position, @NonNull final Post model) {
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
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Delete Post");
                    builder.setMessage("Delete This Post?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //deleteItem(position);

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
            holder.edit.setVisibility(View.VISIBLE);
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Name");
                    final View customLayout =  LayoutInflater.from(mContext).inflate(R.layout.custom_alert, null);
                    builder.setView(customLayout);

                    builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = customLayout.findViewById(R.id.editText);
                            Toast.makeText(mContext,  editText.getText().toString(),Toast.LENGTH_LONG).show();
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

                    if (user.getPostnum() < 100) {
                        Glide
                                .with(mContext)
                                .load(R.drawable.ic_grade)
                                .into(holder.level);
                    }
                    if (user.getPostnum() < 100 && user.getPostnum() > 500) {
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
                    holder.Commentbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent(mContext, PostDisplay.class);
                            intent.putExtra("post",model);
                            intent.putExtra("user",user);
                            mContext.startActivity(intent);
                        }
                    });
                    holder.textViewDescription.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent(mContext, PostDisplay.class);
                            intent.putExtra("post",model);
                            intent.putExtra("user",user);
                            mContext.startActivity(intent);
                        }
                    });
                }
            });

        }
        if(model.getType()==2){
            holder.img.setVisibility(View.VISIBLE);
            Glide
                    .with(mContext)
                    .load(model.getImageUrl())
                    .into(holder.img);
        }
        if(model.getType()==3){

            holder.img.setVisibility(View.VISIBLE);
            Glide
                    .with(mContext)
                    .load(model.getImageUrl())
                    .into(holder.img);
        }
        final DocumentReference postrefrence = db.collection("Posts").document(model.getRefComments());
        final DocumentReference Reference = db.collection("Posts").document(model.getRefComments()).collection("vote").document(auth.getUid());
        holder.up.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             holder.down.setIconTintResource(R.color.colorPrimary);
                                             holder.up.setIconTintResource(R.color.colorPrimary);
                                             Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                     if (task.isSuccessful()) {
                                                         DocumentSnapshot document = task.getResult();
                                                         if (document.exists()) {
                                                             Vote vote = document.toObject(Vote.class);
                                                             if (vote.isVotecheck()) {
                                                                 holder.down.setIconTintResource(R.color.colorPrimary);
                                                                 holder.up.setIconTintResource(R.color.colorPrimary);
                                                                 Reference.delete();
                                                                 postrefrence.update("upnum", model.getUpnum() - 1);
                                                                 postrefrence.update("priority", (model.getUpnum() - 1) * 0.4 + (model.getDownnum()) * 0.2 + model.getCommentnum() * 0.4);
                                                             }else{
                                                                 holder.up.setIconTintResource(R.color.level2);
                                                                 holder.down.setIconTintResource(R.color.colorPrimary);
                                                                 Reference.update("votecheck",true);
                                                                 postrefrence.update("upnum", model.getUpnum() + 1);
                                                                 postrefrence.update("downnum", model.getDownnum() - 1);
                                                                 postrefrence.update("priority", (model.getUpnum() + 1) * 0.4 + (model.getDownnum()-1) * 0.2 + model.getCommentnum() * 0.4);
                                                             }
                                                         } else {
                                                             holder.up.setIconTintResource(R.color.level2);
                                                             holder.down.setIconTintResource(R.color.colorPrimary);
                                                             Vote v= new Vote();
                                                             v.setVotecheck(true);
                                                             Reference.set(v);
                                                             postrefrence.update("upnum", model.getUpnum() + 1);
                                                             postrefrence.update("priority", (model.getUpnum() + 1) * 0.4 + (model.getDownnum()) * 0.2 + model.getCommentnum() * 0.4);
                                                         }

                                                     }
                                                 }

                                             });
                                         }
                                     });
       Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
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
        holder.down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.down.setIconTintResource(R.color.colorPrimary);
                holder.up.setIconTintResource(R.color.colorPrimary);
                Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Vote vote = document.toObject(Vote.class);
                                if (!vote.isVotecheck()) {
                                    holder.down.setIconTintResource(R.color.colorPrimary);
                                    holder.up.setIconTintResource(R.color.colorPrimary);
                                    Reference.delete();
                                    postrefrence.update("downnum", model.getDownnum() - 1);
                                    postrefrence.update("priority", (model.getUpnum()) * 0.4 + (model.getDownnum() -1) * 0.2 + model.getCommentnum() * 0.4);
                                }else{
                                    holder.up.setIconTintResource(R.color.colorPrimary);
                                    holder.down.setIconTintResource(R.color.level2);

                                    Reference.update("votecheck",false);
                                    postrefrence.update("downnum", model.getDownnum() + 1);
                                    postrefrence.update("upnum", model.getUpnum() - 1);
                                    postrefrence.update("priority", (model.getUpnum() - 1) * 0.4 + (model.getDownnum()+1) * 0.2 + model.getCommentnum() * 0.4);
                                }
                            } else {
                                holder.up.setIconTintResource(R.color.colorPrimary);
                                holder.down.setIconTintResource(R.color.level2);
                                Vote v= new Vote();
                                v.setVotecheck(false);
                                Reference.set(v);
                                postrefrence.update("downnum", model.getDownnum() + 1);
                                postrefrence.update("priority", (model.getDownnum()) * 0.4 + (model.getDownnum() + 1) * 0.2 + model.getCommentnum() * 0.4);
                            }

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
     //   SimpleExoPlayerView playerView;
        MaterialButton Commentbutton;
        public NoteHolder(View itemView) {
            super(itemView);
            up= itemView.findViewById(R.id.upbutton);
            down= itemView.findViewById(R.id.downbutton);
            edit =itemView.findViewById(R.id.edit);
           // playerView = itemView.findViewById(R.id.video_view);
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
    private void initializePlayer(SimpleExoPlayer player) {
     //   player = ExoPlayerFactory.newSimpleInstance(this);
       // playerView.setPlayer(player);
    }

    private void updatePriority(int position,int up,int down,int commentnum){
        getSnapshots().getSnapshot(position).getReference().update("priority",up*0.4+down*0.2+commentnum*0.4);

    }
}
