package com.aputech.dora.Adpater;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Model.Post;
import com.aputech.dora.R;
import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewHolder extends RecyclerView.ViewHolder {

    TextView user_name;
    TextView textViewDescription;
    TextView time;
    MaterialButton up,down;
    ImageView img;
    ImageView level;
    ImageView LocationIcon,delete,edit;
    CircleImageView profile;
    MaterialButton Commentbutton;
    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        up= itemView.findViewById(R.id.upbutton);
        down= itemView.findViewById(R.id.downbutton);
        edit =itemView.findViewById(R.id.edit);
        // playerView = itemView.findViewById(R.id.video_view);
        delete = itemView.findViewById(R.id.delete);
        user_name = itemView.findViewById(R.id.user_name);
        textViewDescription = itemView.findViewById(R.id.text_view_description);
        time = itemView.findViewById(R.id.time);
        level= itemView.findViewById(R.id.level);
        LocationIcon = itemView.findViewById(R.id.locate);
        profile=itemView.findViewById(R.id.poster_profile);
        img =itemView.findViewById(R.id.img);
        Commentbutton= itemView.findViewById(R.id.comment);
    }

    public void bind(Post post) {
       // authorView.setText(post.authorName);
       // messageView.setText(post.message);


    }

}