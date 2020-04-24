package com.aputech.dora.Adpater;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.Note;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
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

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentHolder> {
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference notebookRef;
    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, Context mContext) {
        super(options);
        this.mContext=mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final CommentHolder holder, int position, @NonNull final Comment model) {
         holder.textViewDescription.setText(model.getCommentText());
        holder.time.setText(String.valueOf(model.getTime()));
        if (auth.getUid().equals(model.getUid())){
            holder.edit.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        if ( model.getUid() != null) {
            notebookRef = db.collection("Users").document(model.getUid());
            notebookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = task.getResult().toObject(User.class);
                    holder.NameUser.setText(user.getUserName());
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
            });
        }
        holder.upbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean ifdown= model.getDownvote().contains(auth.getUid());
//                boolean ifup= model.getUpvote().contains(auth.getUid());
//                DocumentReference documentReference= db.collection(model.getParent().getPath()).document(model.getRefComments().getId());
//                if (!ifup && !ifdown){
//                    documentReference.update("upvote", FieldValue.arrayUnion(auth.getUid()));
//                }
//                if (ifup && !ifdown){
//                    documentReference.update("upvote", FieldValue.arrayRemove(auth.getUid()));
//                }if(!ifup && ifdown){
//                    documentReference.update("downvote", FieldValue.arrayRemove(auth.getUid()));
//                    documentReference.update("upvote", FieldValue.arrayUnion(auth.getUid()));
//                }

            }
        });
        holder.downbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean ifdown= model.getDownvote().contains(auth.getUid());
//                boolean ifup= model.getUpvote().contains(auth.getUid());
//                DocumentReference documentReference= db.collection(model.getRefComments().getParent().getPath()).document(model.getRefComments().getId());
//                if (!ifup && !ifdown){
//                    documentReference.update("downvote", FieldValue.arrayUnion(auth.getUid()));
//                }
//                if (ifdown && !ifup){
//                    documentReference.update("downvote", FieldValue.arrayRemove(auth.getUid()));
//                }if(!ifdown && ifup){
//                    documentReference.update("upvote", FieldValue.arrayRemove(auth.getUid()));
//                    documentReference.update("downvote", FieldValue.arrayUnion(auth.getUid()));
//                }
            }
        });
        Log.d("bigpp", "onItemRangeInserted: "+model.getUid());


    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_feed,
                parent, false);
        return new CommentHolder(v);
    }

    class CommentHolder extends RecyclerView.ViewHolder {
        TextView textViewDescription;
        MaterialButton upbutton,downbutton;
        TextView NameUser,time;
        ImageView edit;
        ImageView level;
        ImageView delete;

        public CommentHolder(View itemView) {
            super(itemView);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            upbutton = itemView.findViewById(R.id.upbutton);
            delete = itemView.findViewById(R.id.delete);
            level= itemView.findViewById(R.id.level);
            downbutton = itemView.findViewById(R.id.downbutton);
            NameUser = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.time);
            edit = itemView.findViewById(R.id.edit);
        }
    }
}
