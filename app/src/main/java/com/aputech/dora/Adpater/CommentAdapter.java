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

import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.User;
import com.aputech.dora.Model.Vote;
import com.aputech.dora.R;
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

import java.util.Date;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentHolder> {
    String Postid;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference notebookRef;

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, String postid, Context mContext) {
        super(options);
        this.mContext = mContext;
        this.Postid = postid;
    }

    @Override
    protected void onBindViewHolder(@NonNull final CommentHolder holder, final int position, @NonNull final Comment model) {
        holder.textViewDescription.setText(model.getCommentText());
        if (model.getTimestamp() != null) {
            Date date = model.getTimestamp();
            String df = DateFormat.getDateFormat(mContext).format(date).concat("  ").concat(DateFormat.getTimeFormat(mContext).format(date));
            holder.time.setText(df);
        }
        holder.down.setText(String.valueOf(model.getDownnum()));
        holder.up.setText(String.valueOf(model.getUpnum()));
        if (auth.getUid().equals(model.getUid())) {
            holder.edit.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);

        }

        if (model.getUid() != null) {
            notebookRef = db.collection("Users").document(model.getUid());
            notebookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    final User user = task.getResult().toObject(User.class);
                    holder.NameUser.setText(user.getUserName());
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
                    holder.NameUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ProfileDisplayActivity.class);
                            intent.putExtra("user", user);
                            mContext.startActivity(intent);
                        }
                    });
                }
            });
        }
        if (model.getCommentid() != null) {
            db.collection("Posts").document(Postid).collection("comments").document(model.getCommentid()).collection("vote").document(auth.getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                            } else {
                                holder.down.setIconTintResource(R.color.level2);
                            }
                        }
                    }

                }
            });
        }


    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_feed,
                parent, false);
        return new CommentHolder(v);
    }

    @Override
    public int getItemCount() {
        return getSnapshots().size();
    }

    private void deleteItem(int position) {
        final WriteBatch writeBatch = db.batch();
        if (position == 1) {
            position = 0;
        }
        final int finalPosition = position;
        getSnapshots().getSnapshot(position).getReference().collection("vote").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    writeBatch.delete(documentSnapshot.getReference());
                }
                writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getSnapshots().getSnapshot(finalPosition).getReference().delete();
                        Toast.makeText(mContext,
                                "Comment Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

    }

    class CommentHolder extends RecyclerView.ViewHolder {
        TextView textViewDescription;
        MaterialButton up, down;
        TextView NameUser, time;
        ImageView edit;
        ImageView level;
        ImageView delete;

        public CommentHolder(View itemView) {
            super(itemView);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            up = itemView.findViewById(R.id.upbutton);
            delete = itemView.findViewById(R.id.delete);
            level = itemView.findViewById(R.id.level);
            down = itemView.findViewById(R.id.downbutton);
            NameUser = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.time);
            edit = itemView.findViewById(R.id.edit);

            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DocumentReference postrefrence = db.collection("Posts").document(Postid).collection("comments").document(getSnapshots().get(getAdapterPosition()).getCommentid());
                    final DocumentReference Reference = db.collection("Posts").document(Postid).collection("comments").document(getSnapshots().get(getAdapterPosition()).getCommentid()).collection("vote").document(auth.getUid());
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
                                        postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getUpnum() - 1) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum()) * 0.2);
                                    } else {
                                        Reference.update("votecheck", true);
                                        postrefrence.update("upnum", getSnapshots().get(getAdapterPosition()).getUpnum() + 1);
                                        postrefrence.update("downnum", getSnapshots().get(getAdapterPosition()).getDownnum() - 1);
                                        postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getUpnum() + 1) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum() - 1) * 0.2);
                                    }
                                } else {
                                    up.setIconTintResource(R.color.level2);
                                    Vote v = new Vote();
                                    v.setVotecheck(true);
                                    Reference.set(v);
                                    postrefrence.update("upnum", getSnapshots().get(getAdapterPosition()).getUpnum() + 1);
                                    postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getUpnum() + 1) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum()) * 0.2);
                                }

                            }
                        }

                    });
                }
            });

            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DocumentReference postrefrence = db.collection("Posts").document(Postid).collection("comments").document(getSnapshots().get(getAdapterPosition()).getCommentid());
                    final DocumentReference Reference = db.collection("Posts").document(Postid).collection("comments").document(getSnapshots().get(getAdapterPosition()).getCommentid()).collection("vote").document(auth.getUid());
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
                                        postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getUpnum()) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum() - 1) * 0.2);
                                    } else {
                                        Reference.update("votecheck", false);
                                        postrefrence.update("downnum", getSnapshots().get(getAdapterPosition()).getDownnum() + 1);
                                        postrefrence.update("upnum", getSnapshots().get(getAdapterPosition()).getUpnum() - 1);
                                        postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getUpnum() - 1) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum() + 1) * 0.2);
                                    }
                                } else {
                                    Vote v = new Vote();
                                    v.setVotecheck(false);
                                    Reference.set(v);
                                    postrefrence.update("downnum", getSnapshots().get(getAdapterPosition()).getDownnum() + 1);
                                    postrefrence.update("priority", (getSnapshots().get(getAdapterPosition()).getDownnum()) * 0.4 + (getSnapshots().get(getAdapterPosition()).getDownnum() + 1) * 0.2);
                                }

                            }
                        }

                    });
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
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
                            deleteItem(getAdapterPosition());
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


            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Edit Post");

                    final View customLayout = LayoutInflater.from(mContext).inflate(R.layout.custom_alert, null);
                    builder.setView(customLayout);
                    final EditText editText = customLayout.findViewById(R.id.para);
                    editText.setText(getSnapshots().get(getAdapterPosition()).getCommentText());
                    builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!editText.getText().toString().isEmpty()) {
                                db.collection("Posts").document(Postid).collection("comments").document(getSnapshots().get(getAdapterPosition()).getCommentid()).update("commentText", editText.getText().toString());
                                Toast.makeText(mContext, "Post Updated", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(mContext, "Unable to Make Changes Field Empty", Toast.LENGTH_LONG).show();
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

        }
    }
}
