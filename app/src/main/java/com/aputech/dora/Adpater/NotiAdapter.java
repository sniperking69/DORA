package com.aputech.dora.Adpater;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Model.User;
import com.aputech.dora.Model.notification;
import com.aputech.dora.R;
import com.aputech.dora.Model.Note;
import com.aputech.dora.ui.PostDisplay;
import com.aputech.dora.ui.ProfileDisplayActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotiAdapter extends FirestoreRecyclerAdapter<notification, NotiAdapter.notificationHolder> {
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public NotiAdapter(@NonNull FirestoreRecyclerOptions<notification> options, Context mContext) {
        super(options);
        this.mContext=mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final notificationHolder holder, final int position, @NonNull final notification model) {
        if (model.getTimestamp() != null){
            Date date= model.getTimestamp();
            String df = DateFormat.getDateFormat(mContext).format(date).concat("  ").concat(DateFormat.getTimeFormat(mContext).format(date));
            holder.notitime.setText(df);
        }

        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
            }
        });
        holder.notidesc.setText(model.getText());
        holder.notidoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostDisplay.class);
                intent.putExtra("docid",model.getDocument());
                mContext.startActivity(intent);
            }
        });
        DocumentReference documentReference = db.collection("Users").document(model.getUserid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user.getProfileUrl()!=null){
                    Glide
                            .with(mContext)
                            .load(user.getProfileUrl())
                            .into(holder.img);
                }

            }
        });
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
        CardView notidoc;
        public notificationHolder(View itemView) {
            super(itemView);
            Delete=itemView.findViewById(R.id.delete);
            notidesc = itemView.findViewById(R.id.notidesc);
            notitime = itemView.findViewById(R.id.notitime);
            img =itemView.findViewById(R.id.profiledisplay);
            notidoc= itemView.findViewById(R.id.notidoc);
        }
    }
    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }
    // Add this

}