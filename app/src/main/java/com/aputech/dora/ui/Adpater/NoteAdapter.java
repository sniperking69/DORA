package com.aputech.dora.ui.Adpater;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.R;
import com.aputech.dora.ui.Model.Note;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder> {

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull Note model) {
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
        public NoteHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);
            img =itemView.findViewById(R.id.img);
        }
    }
}