package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.Note;
import com.aputech.dora.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Post extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText editText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");
    private ImageView imageView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar postTool = findViewById(R.id.post_toolbar);
        setSupportActionBar(postTool);
        editText=findViewById(R.id.para);
        imageView = findViewById(R.id.dispimg);
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
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

        ArrayList<String> emp=new ArrayList<String>();
        List<String> empty= Collections.<String>emptyList();
        post.setUpvote(empty);
        post.setDownvote(empty);
        post.setUserid(auth.getUid());
        final Date currentTime = Calendar.getInstance().getTime();

        notebookRef.add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                post.setRefComments(documentReference);
                post.setUptime(currentTime.toString());
                documentReference.set(post);

            }
        });
        Toast.makeText(Post.this, "Note Added Successfully", Toast.LENGTH_LONG).show();
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
}
