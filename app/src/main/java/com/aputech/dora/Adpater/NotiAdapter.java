package com.aputech.dora.Adpater;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Model.Post;
import com.aputech.dora.Model.User;
import com.aputech.dora.Model.message;
import com.aputech.dora.Model.notification;
import com.aputech.dora.R;
import com.aputech.dora.ui.MapView;
import com.aputech.dora.ui.PostDisplay;
import com.aputech.dora.ui.PrivatePost;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class NotiAdapter extends FirestoreRecyclerAdapter<notification, NotiAdapter.notificationHolder> {
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public NotiAdapter(@NonNull FirestoreRecyclerOptions<notification> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final notificationHolder holder, final int position, @NonNull final notification model) {
        if (model.getTimestamp() != null) {
            Date date = model.getTimestamp();
            String df = DateFormat.getDateFormat(mContext).format(date).concat("  ").concat(DateFormat.getTimeFormat(mContext).format(date));
            holder.notitime.setText(df);
        }

        holder.notidesc.setText(model.getText());
        if (!model.getUserid().equals("nearby")){
            DocumentReference documentReference = db.collection("Users").document(model.getUserid());
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user.getProfileUrl() != null) {
                        Glide
                                .with(mContext)
                                .load(user.getProfileUrl())
                                .into(holder.img);
                    }

                }
            });
        }else{
            Glide
                    .with(mContext)
                    .load(R.drawable.ic_locationhappy)
                    .into(holder.img);
        }

    }

    @NonNull
    @Override
    public notificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notify,
                parent, false);
        return new notificationHolder(v);
    }

    class notificationHolder extends RecyclerView.ViewHolder {
        TextView notidesc, notitime;
        MaterialButton Delete;
        ImageView img;
        CardView notidoc;

        public notificationHolder(View itemView) {
            super(itemView);
            Delete = itemView.findViewById(R.id.delete);
            notidesc = itemView.findViewById(R.id.notidesc);
            notitime = itemView.findViewById(R.id.notitime);
            img = itemView.findViewById(R.id.profiledisplay);
            notidoc = itemView.findViewById(R.id.notidoc);
            Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(getAdapterPosition());
                }
            });
            notidoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getSnapshots().get(getAdapterPosition()).getTyp()==1){
                        Intent intent = new Intent(mContext, PrivatePost.class);
                        intent.putExtra("typ",1);
                        intent.putExtra("post",getSnapshots().get(getAdapterPosition()).getDocument());
                        mContext.startActivity(intent);
                    }if (getSnapshots().get(getAdapterPosition()).getTyp()==2){
                        Intent intent = new Intent(mContext, MapView.class);
                        intent.putExtra("typ",2);
                        intent.putExtra("post",getSnapshots().get(getAdapterPosition()).getDocument());
                        mContext.startActivity(intent);
                    }else{
                        Intent intent = new Intent(mContext, PostDisplay.class);
                        intent.putExtra("typ",0);
                        intent.putExtra("post",getSnapshots().get(getAdapterPosition()).getDocument());
                        mContext.startActivity(intent);
                    }

                }
            });
        }
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @Override
    public int getItemCount() {
        return getSnapshots().size();
    }
}