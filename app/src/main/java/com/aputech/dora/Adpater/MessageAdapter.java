package com.aputech.dora.Adpater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Model.Message;
import com.aputech.dora.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MessageAdapter extends FirestoreRecyclerAdapter <Message, MessageAdapter.MessageHolder> {


    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull Message model) {
        holder.Title.setText(model.getTitle());
        holder.Description.setText(model.getDescription());
        holder.Priority.setText(String.valueOf(model.getPriority()));

    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,
                parent, false);
        return new MessageHolder(v);
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        TextView Title;
        TextView Description;
        TextView Priority;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.text_view_title);
            Description = itemView.findViewById(R.id.text_view_description);
            Priority = itemView.findViewById(R.id.text_view_priority);
        }
    }
}

