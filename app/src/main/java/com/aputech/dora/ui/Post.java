package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Notification;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.Note;
import com.aputech.dora.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Post extends AppCompatActivity {

    private EditText editText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar postTool = findViewById(R.id.post_toolbar);
        setSupportActionBar(postTool);
        editText=findViewById(R.id.para);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                revealFAB();
            }
        });


    }

    private void revealFAB() {
        final View view = findViewById(R.id.post_toolbar);
        int cx = view.getWidth() /2;
        int cy = view.getHeight()/2 ;

        float finalRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);

            }
        });
        anim.start();

    }

    public void locationadd(View view) {
    }

    public void cameraopen(View view) {
    }

    public void gallery(View view) {
    }

    public void AudioGallery(View view) {
    }

    public void Done(View view) {
        String text= editText.getText().toString();
        final Note post = new Note();
        post.setDescription(text);
        post.setType(1);
        notebookRef.add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                post.setRefComments(documentReference);
                documentReference.set(post);

            }
        });
        Toast.makeText(Post.this, "Note Added Successfully", Toast.LENGTH_LONG).show();
    }
}
