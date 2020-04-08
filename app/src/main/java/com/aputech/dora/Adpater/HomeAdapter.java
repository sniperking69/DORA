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

import com.aputech.dora.R;
import com.aputech.dora.Model.Note;
import com.aputech.dora.ui.ViewPostActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class HomeAdapter extends FirestoreRecyclerAdapter<Note, HomeAdapter.NoteHolder> {
    Context mContext;
    public HomeAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context mContext) {
        super(options);
        this.mContext=mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, int position, @NonNull final Note model) {
        Log.d("doracheck", "onClick: "+model.getrefComments().getParent().getPath());
        if(model.getType()==1){
            holder.textViewTitle.setText(model.getTitle());
            holder.textViewDescription.setText(model.getDescription());
            holder.textViewPriority.setText(String.valueOf(model.getPriority()));
        }
        if(model.getType()==2){
            holder.img.setVisibility(View.VISIBLE);
            Glide
                    .with(holder.img.getContext())
                    .load(model.getImageUrl())
                    .into(holder.img);
            holder.textViewDescription.setText(model.getDescription());
            holder.textViewPriority.setText(String.valueOf(model.getPriority()));
        }
        holder.Commentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, ViewPostActivity.class);

                intent.putExtra("coll",model.getrefComments().getParent().getPath());
                intent.putExtra("doc",model.getrefComments().getId());
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
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewPriority;
        ImageView img;
        View Commentbutton;
        public NoteHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);
            img =itemView.findViewById(R.id.img);
            Commentbutton= itemView.findViewById(R.id.comment);
        }
    }
}
