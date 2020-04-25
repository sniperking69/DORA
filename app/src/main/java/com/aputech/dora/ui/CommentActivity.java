package com.aputech.dora.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
    ImageView delete,edit;
    Note note;
    private TextView postText,userName,post_time;
    ImageView locate,ProfileImg;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
       editText = findViewById(R.id.commenttext);
       noresult= findViewById(R.id.noresult);
       userName = findViewById(R.id.user_name);
       post_time= findViewById(R.id.time);
       locate =findViewById(R.id.locate);
       up = findViewById(R.id.upbutton);
       delete = findViewById(R.id.delete);
       edit = findViewById(R.id.edit);
        down = findViewById(R.id.downbutton);
       postText = findViewById(R.id.text_view_description);
       ProfileImg = findViewById(R.id.poster_profile);
        Intent intent= getIntent();
        collection = intent.getStringExtra("coll");
        Document=intent.getStringExtra("doc");
          note= intent.getParcelableExtra("post");
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
        if (user.getUserid().equals(auth.getUid())){
           delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Build an AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);

                    // Set a title for alert dialog
                    builder.setTitle("Delete Post");

                    // Ask the final question
                    builder.setMessage("Are you sure to Delete This Post?");

                    // Set the alert dialog yes button click listener
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when user clicked the Yes button
                            // Set the TextView visibility GONE
                            deleteItem();
                            finish();
                            Toast.makeText(getApplicationContext(),
                                    "PostDeleted",Toast.LENGTH_SHORT).show();

                        }
                    });

                    // Set the alert dialog no button click listener
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when No button clicked
                        }
                    });

                    AlertDialog dialog = builder.create();
                    // Display the alert dialog on interface
                    dialog.show();
                }
            });
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("bigpp", "onClick: EditPost");
                }
            });
        }

        documentReference = db.collection(collection).document(Document);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Note model =  documentSnapshot.toObject(Note.class);
               up.setText(String.valueOf(model.getUpnum()));
                down.setText(String.valueOf(model.getDownnum()));
                up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean ifdown= model.getDownvote().contains(auth.getUid());
                        boolean ifup= model.getUpvote().contains(auth.getUid());
                        if (!ifup && !ifdown){
                            documentReference.update("upvote", FieldValue.arrayUnion(auth.getUid()));
                            documentReference.update("upnum", model.getUpnum()+1);
                            updatePriority(model.getUpnum()+1,model.getDownnum(),model.getCommentnum());


                        }
                        if (ifup && !ifdown){
                            documentReference.update("upvote", FieldValue.arrayRemove(auth.getUid()));
                            documentReference.update("upnum", model.getUpnum()-1);
                            updatePriority(model.getUpnum()-1,model.getDownnum(),model.getCommentnum());
                        }
                        if(!ifup && ifdown){
                            documentReference.update("downvote", FieldValue.arrayRemove(auth.getUid()));
                            documentReference.update("downnum", model.getDownnum()-1);
                            documentReference.update("upvote", FieldValue.arrayUnion(auth.getUid()));
                            documentReference.update("upnum", model.getUpnum()+1);
                            updatePriority(model.getUpnum()+1,model.getDownnum()-1,model.getCommentnum());
                        }
                    }
                });
                down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean ifdown= model.getDownvote().contains(auth.getUid());
                        boolean ifup= model.getUpvote().contains(auth.getUid());
                        if (!ifup && !ifdown){
                            documentReference.update("downvote", FieldValue.arrayUnion(auth.getUid()));
                            documentReference.update("downnum", model.getDownnum()+1);
                            updatePriority(model.getUpnum(),model.getDownnum()+1,model.getCommentnum());
                        }
                        if (ifdown && !ifup){
                            documentReference.update("downvote", FieldValue.arrayRemove(auth.getUid()));
                            documentReference.update("downnum", model.getDownnum()-1);
                            updatePriority(model.getUpnum(),model.getDownnum()-1,model.getCommentnum());
                        }if(!ifdown && ifup){
                            documentReference.update("upvote", FieldValue.arrayRemove(auth.getUid()));
                            documentReference.update("upnum", model.getUpnum()-1);
                            documentReference.update("downvote", FieldValue.arrayUnion(auth.getUid()));
                            documentReference.update("downnum", model.getDownnum()+1);
                            updatePriority(model.getUpnum()-1,model.getDownnum()+1,model.getCommentnum());
                        }

                    }
                });
            }
        });

         if (Type==1){




         }if (Type==2){


        }
        notebookRef =db.collection("comments");
        Query query = notebookRef.whereEqualTo("docId",note.getRefComments()).orderBy("priority", Query.Direction.ASCENDING);

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

    private void deleteItem() {
        documentReference.delete();
    }

    private void updatePriority(int up, int down, int commentnum) {
        documentReference.update("priority",up*0.4+down*0.2+commentnum*0.4);

    }

    public void sendcomment(View view) {
        DocumentReference documentReference = db.collection(collection).document(Document);
        documentReference.update("commentnum",Commentnum+1);
        String comenttext= editText.getText().toString();
        Comment comment = new Comment();
        comment.setDocId(note.getRefComments());
        comment.setCommentText(comenttext);
        comment.setUid(auth.getUid());
        comment.setPriority(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm aa");
        final String date =dateFormat.format(Calendar.getInstance().getTime());
        comment.setTime(date);
//
        notebookRef.add(comment);
//        notification noti = new notification();
//        noti.setDocument(Document);
//        noti.setUserid(Userid);
//        noti.setTime(date);
//        noti.setText(user_name + " Comment On Your Post");
//        CollectionReference  notiref= db.collection("Users").document(Userid).collection("notify");
//        notiref.add(noti);
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


