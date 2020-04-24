package com.aputech.dora.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aputech.dora.Adpater.CommentAdapter;
import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.Note;
import com.aputech.dora.Model.User;
import com.aputech.dora.Model.notification;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    RelativeLayout noresult;
    String Document;
    String Userid,user_name;
    int Commentnum;
    private String collection;
    private TextView postText,userName,post_time;
    ImageView locate,ProfileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
       editText = findViewById(R.id.commenttext);
       noresult= findViewById(R.id.noresult);
       userName = findViewById(R.id.user_name);
       post_time= findViewById(R.id.time);
       locate =findViewById(R.id.locate);
       postText = findViewById(R.id.text_view_description);
       ProfileImg = findViewById(R.id.poster_profile);
        Intent intent= getIntent();
      collection = intent.getStringExtra("coll");
         Document=intent.getStringExtra("doc");
         Note note= intent.getParcelableExtra("post");
         User user = intent.getParcelableExtra("user");
         int Type = note.getType();
        postText.setText(note.getDescription());
        userName.setText(user.getUserName());
        post_time.setText(note.getUptime());
        Glide
                .with(CommentActivity.this)
                .load(user.getProfileUrl())
                .into(ProfileImg);
        if (note.getLocation()!=null){
            locate.setImageResource(R.drawable.ic_locationhappy);
            locate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1= new Intent(CommentActivity.this,DispPostLocation.class);
                    startActivity(intent1);
                }
            });

        }else{
            locate.setImageResource(R.drawable.ic_locationsad);
        }
         if (Type==1){




         }if (Type==2){


        }

        notebookRef = db.collection(collection).document(Document).collection("comments");
        Query query = notebookRef.orderBy("priority", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new CommentAdapter(options,getApplicationContext());
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int totalNumberOfItems = adapter.getItemCount();
                if(totalNumberOfItems > 0) {
                    noresult.setVisibility(View.GONE);
                }else{
                    noresult.setVisibility(View.VISIBLE);
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    public void sendcomment(View view) {
        DocumentReference documentReference = db.collection(collection).document(Document);
        documentReference.update("commentnum",Commentnum+1);
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
        noti.setText(user_name + " Comment On Your Post");
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
    //   adapter.stopListening();
    }
}


