package com.aputech.dora.Adpater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Model.User;
import com.aputech.dora.Model.notification;
import com.aputech.dora.R;
import com.aputech.dora.Model.Note;
import com.aputech.dora.ui.ProfileDisplayActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotiAdapter extends FirestoreRecyclerAdapter<notification, NotiAdapter.notificationHolder> {
    private Context mContext;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public NotiAdapter(@NonNull FirestoreRecyclerOptions<notification> options, Context mContext) {
        super(options);
        this.mContext=mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull notificationHolder holder, final int position, @NonNull final notification model) {

        holder.notitime.setText(model.getTime());
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
            }
        });
        holder.notidesc.setText(model.getText());
    //    DocumentReference documentReference = db.
//        Glide
//                .with(mContext)
//                .load(model.getDocument())
//                .into(holder.img);
//        holder.textViewTitle.setText(model.getnotificationName());
//        holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, ProfileDisplayActivity.class);
//                intent.putExtra("notification_id",model.getnotificationid());
//                mContext.startActivity(intent);
//            }
//        });

    }

    @NonNull
    @Override
    public notificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notify,
                parent, false);
        return new notificationHolder(v);
    }

    class notificationHolder extends RecyclerView.ViewHolder {
        TextView notidesc,notitime;
        MaterialButton Delete;
        ImageView img;
        public notificationHolder(View itemView) {
            super(itemView);
            Delete=itemView.findViewById(R.id.delete);
            notidesc = itemView.findViewById(R.id.notidesc);
            notitime = itemView.findViewById(R.id.notitime);
            img =itemView.findViewById(R.id.profiledisplay);
        }
    }
    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }
}