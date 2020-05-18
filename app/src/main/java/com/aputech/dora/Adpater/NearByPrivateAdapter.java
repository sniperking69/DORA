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

import com.aputech.dora.Model.User;
import com.aputech.dora.Model.message;
import com.aputech.dora.R;
import com.aputech.dora.ui.DispPostLocation;
import com.aputech.dora.ui.PrivatePostDisplay;
import com.aputech.dora.ui.ProfileDisplayActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NearByPrivateAdapter extends FirestoreRecyclerAdapter<message, NearByPrivateAdapter.NoteHolder> {
    private Context mContext;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> UserMsgID;

    public NearByPrivateAdapter(@NonNull FirestoreRecyclerOptions<message> options, Context mContext, ArrayList<String> UserMsgID) {
        super(options);
        this.UserMsgID = UserMsgID;
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, final int position, @NonNull final message model) {
        if (UserMsgID.contains(model.getRefmsg())) {
            holder.textViewDescription.setText(model.getDescription());
            if (model.getTimestamp() != null) {
                Date date = model.getTimestamp();
                String df = DateFormat.getDateFormat(mContext).format(date).concat("  ").concat(DateFormat.getTimeFormat(mContext).format(date));
                holder.time.setText(df);
            }
            if (model.getLocation() != null) {
                holder.LocationIcon.setImageResource(R.drawable.ic_locationhappy);
                holder.LocationIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, DispPostLocation.class);
                        intent.putExtra("lat", model.getLocation().getLatitude());
                        intent.putExtra("lng", model.getLocation().getLongitude());
                        mContext.startActivity(intent);
                    }
                });
            }

            if (model.getSender() != null) {
                if (model.getSender().equals(auth.getUid())) {
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.edit.setVisibility(View.VISIBLE);
                }
                DocumentReference notebookRef = db.collection("Users").document(model.getSender());
                notebookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        final User user = task.getResult().toObject(User.class);
                        holder.user_name.setText(user.getUserName());
                        if (user.getProfileUrl() != null) {
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
                                intent.putExtra("user", user);
                                mContext.startActivity(intent);
                            }
                        });
                        holder.user_name.setOnClickListener(new View.OnClickListener() {
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

            if (model.getType() == 1) {
                holder.thumbnail.setVisibility(View.GONE);
            }
            if (model.getType() == 2) {
                holder.thumbnail.setVisibility(View.VISIBLE);
                holder.playButton.setVisibility(View.GONE);
                Glide
                        .with(mContext)
                        .load(model.getImageUrl())
                        .into(holder.img);
            }
            if (model.getType() == 3) {
                holder.thumbnail.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(model.getVideoUrl()).into(holder.img);
            }
            if (model.getType() == 4) {
                holder.thumbnail.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(R.drawable.ic_sound).into(holder.img);
            }
        } else {
            holder.card.setVisibility(View.GONE);
            holder.card.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_private_post,
                parent, false);
        return new NoteHolder(v);
    }

    public int ActualSize() {
        int actualSize = 0;
        for (int x = 0; x < getSnapshots().size(); x++) {
            if (UserMsgID.contains(getSnapshots().get(x).getRefmsg())) {
                actualSize += 1;
            }
        }
        return actualSize;
    }

    private void DeletePost(final String Postid) {
        db.collection("Inbox").document(Postid).delete();
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView user_name;
        TextView textViewDescription;
        TextView time;
        ImageView img;
        ImageView level;
        ImageView LocationIcon, delete, edit;
        CircleImageView profile;
        View audioview;
        ImageView playButton;
        CardView thumbnail;

        public NoteHolder(View itemView) {
            super(itemView);
            edit = itemView.findViewById(R.id.edit);
            playButton = itemView.findViewById(R.id.playbutton);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            audioview = itemView.findViewById(R.id.audiocard);
            delete = itemView.findViewById(R.id.delete);
            card = itemView.findViewById(R.id.card);
            user_name = itemView.findViewById(R.id.user_name);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            time = itemView.findViewById(R.id.time);
            level = itemView.findViewById(R.id.level);
            LocationIcon = itemView.findViewById(R.id.locate);
            profile = itemView.findViewById(R.id.poster_profile);
            img = itemView.findViewById(R.id.img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PrivatePostDisplay.class);
                    intent.putExtra("post", getSnapshots().get(getAdapterPosition()).getRefmsg());
                    mContext.startActivity(intent);
                }
            });
            textViewDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PrivatePostDisplay.class);
                    intent.putExtra("post", getSnapshots().get(getAdapterPosition()).getRefmsg());
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
                            DeletePost(getSnapshots().get(getAdapterPosition()).getRefmsg());
                            Toast.makeText(mContext, "Post Deleted", Toast.LENGTH_LONG).show();

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

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Edit Post");
                    final View customLayout = LayoutInflater.from(mContext).inflate(R.layout.custom_alert, null);
                    builder.setView(customLayout);
                    final EditText editText = customLayout.findViewById(R.id.para);
                    editText.setText(getSnapshots().get(getAdapterPosition()).getDescription());
                    builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!editText.getText().toString().isEmpty()) {
                                db.collection("Inbox").document(getSnapshots().get(getAdapterPosition()).getRefmsg()).update("description", editText.getText().toString());
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