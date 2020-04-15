package com.aputech.dora.Adpater;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.aputech.dora.ui.ViewPostActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends FirestoreRecyclerAdapter<Note, HomeAdapter.NoteHolder> {
    Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference notebookRef;
    public HomeAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context mContext) {
        super(options);
        this.mContext=mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, int position, @NonNull final Note model) {
        if ( model.getUserid() != null) {
            notebookRef = db.collection("Users").document(model.getUserid());
            notebookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = task.getResult().toObject(User.class);
                    holder.user_name.setText(user.getUserName());
                    Glide
                            .with(mContext)
                            .load(user.getProfileUrl())
                            .into(holder.profile);
                }
            });
        }

        if(model.getType()==1){
            holder.textViewDescription.setText(model.getDescription());
            holder.time.setText(String.valueOf(model.getUptime()));
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
        holder.Commentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, ViewPostActivity.class);
                intent.putExtra("coll",model.getRefComments().getParent().getPath());
                intent.putExtra("doc",model.getRefComments().getId());
                mContext.startActivity(intent);
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
        ImageView img;
        CircleImageView profile;
        View Commentbutton;
        public NoteHolder(View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            time = itemView.findViewById(R.id.time);
            profile=itemView.findViewById(R.id.poster_profile);
            img =itemView.findViewById(R.id.img);
            Commentbutton= itemView.findViewById(R.id.comment);
        }
    }
}
