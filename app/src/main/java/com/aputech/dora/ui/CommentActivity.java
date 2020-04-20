package com.aputech.dora.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aputech.dora.Adpater.CommentAdapter;
import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.notification;
import com.aputech.dora.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommentActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef ;
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private CommentAdapter adapter;
    MaterialButton up,down;
    private EditText editText;
    String Document;
    String Userid,user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        editText = findViewById(R.id.commenttext);
        Intent intent= getIntent();
        String collection = intent.getStringExtra("coll");
         Document=intent.getStringExtra("doc");
         Userid= intent.getStringExtra("user_id");
         user_name = intent.getStringExtra("user_name");
        up=findViewById(R.id.up);
        down=findViewById(R.id.down);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean ifdown= model.getDownvote().contains(auth.getUid());
//                boolean ifup= model.getUpvote().contains(auth.getUid());
//                DocumentReference documentReference= db.collection(model.getRefComments().getParent().getPath()).document(model.getRefComments().getId());
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
        down.setOnClickListener(new View.OnClickListener() {
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
        notebookRef = db.collection(collection).document(Document).collection("comments");
        Query query = notebookRef.orderBy("priority", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new CommentAdapter(options,getApplicationContext());
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    public void sendcomment(View view) {
        String comenttext= editText.getText().toString();
        Comment comment = new Comment();
        comment.setCommentText(comenttext);
        comment.setUid(auth.getUid());
        comment.setPriority(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm aa");
        final String date =dateFormat.format(Calendar.getInstance().getTime());
        comment.setTime(date);

        notebookRef.add(comment);
        notification noti = new notification();
        noti.setDocument(Document);
        noti.setUserid(Userid);
        noti.setTime(date);
        noti.setText(user_name + "Comment On Your Post");
        CollectionReference  notiref= db.collection("Users").document(Userid).collection("notify");
        notiref.add(noti);
        Toast.makeText(CommentActivity.this, "Note Added Successfully", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStart() {
        super.onStart();
      adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
       adapter.stopListening();
    }
}


