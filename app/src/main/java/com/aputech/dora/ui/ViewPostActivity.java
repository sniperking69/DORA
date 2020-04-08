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
import com.aputech.dora.Adpater.HomeAdapter;
import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.Note;
import com.aputech.dora.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ViewPostActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef ;
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private CommentAdapter adapter;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        editText = findViewById(R.id.commenttext);
        Intent intent= getIntent();
        String collection = intent.getStringExtra("coll");
        String Document=intent.getStringExtra("doc");
        Log.d("doracheck", "onCreate: "+collection+Document);
        notebookRef = db.collection(collection).document(Document).collection("comments");
        Query query = notebookRef.orderBy("priority", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new CommentAdapter(options);
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
        notebookRef.add(comment);
        Toast.makeText(ViewPostActivity.this, "Note Added Successfully", Toast.LENGTH_LONG).show();

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


