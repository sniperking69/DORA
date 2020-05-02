package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.aputech.dora.Model.Vote;
import com.aputech.dora.Model.notification;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

public class PostDisplay extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef ;
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private CommentAdapter adapter;
    MaterialButton up,down;
    private EditText editText;
    RelativeLayout noresult;
    String Userid,user_name;
    int Commentnum;
    EventListener<DocumentSnapshot> eventListener;
    ImageView delete,edit;
    String TAG="bigp";
    Note note;
    ListenerRegistration listenerRegistration;
    private TextView postText,userName,post_time;
    ImageView locate,ProfileImg;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);
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
        note= intent.getParcelableExtra("post");
        User user = intent.getParcelableExtra("user");
        int Type = note.getType();
        postText.setText(note.getDescription());
        userName.setText(user.getUserName());
        //post_time.setText(note.getUptime());
        Glide
                .with(PostDisplay.this)
                .load(user.getProfileUrl())
                .into(ProfileImg);
        if (note.getLocation()!=null){
            locate.setImageResource(R.drawable.ic_locationhappy);
            locate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1= new Intent(PostDisplay.this,DispPostLocation.class);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostDisplay.this);

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
                            //     deleteItem();
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
        if (Type==2){

        }
        documentReference = db.collection("Posts").document(note.getRefComments());
         eventListener =new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
              Note n = documentSnapshot.toObject(Note.class);
              up.setText(String.valueOf(n.getUpnum()));
                down.setText(String.valueOf(n.getDownnum()));
            }
        };
        final DocumentReference postrefrence = db.collection("Posts").document(note.getRefComments());
        final DocumentReference Reference = db.collection("Posts").document(note.getRefComments()).collection("vote").document(auth.getUid());
        listenerRegistration=documentReference.addSnapshotListener(eventListener);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postrefrence.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final Note doc = documentSnapshot.toObject(Note.class);
                        Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Vote vote = document.toObject(Vote.class);
                                        if (vote.isVotecheck()) {
                                            up.setIconTintResource(R.color.colorPrimary);
                                            Reference.delete();
                                            postrefrence.update("upnum", doc.getUpnum() - 1);
                                            postrefrence.update("priority", (doc.getUpnum() - 1) * 0.4 + (doc.getDownnum()) * 0.2 + doc.getCommentnum() * 0.4);
                                        }else{
                                            up.setIconTintResource(R.color.level2);
                                            Reference.update("votecheck",true);
                                            postrefrence.update("upnum", doc.getUpnum() + 1);
                                            postrefrence.update("downnum", doc.getDownnum() - 1);
                                            postrefrence.update("priority", (doc.getUpnum() + 1) * 0.4 + (doc.getDownnum()-1) * 0.2 + doc.getCommentnum() * 0.4);
                                        }
                                    } else {
                                        up.setIconTintResource(R.color.level2);
                                        Vote v= new Vote();
                                        v.setVotecheck(true);
                                        Reference.set(v);
                                        postrefrence.update("upnum", doc.getUpnum() + 1);
                                        postrefrence.update("priority", (doc.getUpnum() + 1) * 0.4 + (doc.getDownnum()) * 0.2 + doc.getCommentnum() * 0.4);
                                    }

                                }
                            }

                        });
                    }
                });

            }
        });
        Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Vote vote = document.toObject(Vote.class);
                        if (vote.isVotecheck()) {
                            up.setIconTintResource(R.color.level2);
                        }else{
                            down.setIconTintResource(R.color.level2);
                        }
                    }
                }

            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postrefrence.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final Note doc = documentSnapshot.toObject(Note.class);
                        Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Vote vote = document.toObject(Vote.class);
                                        if (!vote.isVotecheck()) {
                                            down.setIconTintResource(R.color.colorPrimary);
                                            Reference.delete();
                                            postrefrence.update("downnum", doc.getDownnum() - 1);
                                            postrefrence.update("priority", (doc.getUpnum()) * 0.4 + (doc.getDownnum() -1) * 0.2 + doc.getCommentnum() * 0.4);
                                        }else{
                                            down.setIconTintResource(R.color.level2);
                                            Reference.update("votecheck",false);
                                            postrefrence.update("downnum", doc.getDownnum() + 1);
                                            postrefrence.update("upnum", doc.getUpnum() - 1);
                                            postrefrence.update("priority", (doc.getUpnum() - 1) * 0.4 + (doc.getDownnum()+1) * 0.2 + doc.getCommentnum() * 0.4);
                                        }
                                    } else {
                                        down.setIconTintResource(R.color.level2);
                                        Vote v= new Vote();
                                        v.setVotecheck(false);
                                        Reference.set(v);
                                        postrefrence.update("downnum", doc.getDownnum() + 1);
                                        postrefrence.update("priority", (doc.getDownnum()) * 0.4 + (doc.getDownnum() + 1) * 0.2 + doc.getCommentnum() * 0.4);
                                    }

                                }
                            }

                        });
                    }
                });

            }
        });
        notebookRef =db.collection("Posts").document(note.getRefComments()).collection("comments");
        Query query = notebookRef.orderBy("priority", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();
        adapter = new CommentAdapter(options,note.getRefComments(),PostDisplay.this);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        noresult.setVisibility(View.VISIBLE);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (adapter.getItemCount()==0){
                    noresult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (adapter.getItemCount() >0){
                    noresult.setVisibility(View.INVISIBLE);
                }else{
                    noresult.setVisibility(View.VISIBLE);
                }
            }
        });


    }
    public void sendcomment(View view) {
        String comenttext= editText.getText().toString();
        if (!comenttext.isEmpty()){
            DocumentReference documentReference = db.collection("Posts").document(note.getRefComments());
            final CollectionReference col=db.collection("Posts").document(note.getRefComments()).collection("comments");
            documentReference.update("commentnum",Commentnum+1);
            Comment comment = new Comment();
            comment.setUid(auth.getUid());
            comment.setCommentText(comenttext);
            comment.setUid(auth.getUid());
            col.add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    col.document(documentReference.getId()).update("commentid",documentReference.getId());
                }
            });
        }else{
            Toast.makeText(PostDisplay.this, "Nothing to comment", Toast.LENGTH_LONG).show();
        }

////
//        notebookRef.add(comment);
//        notification noti = new notification();
//        noti.setDocument(Document);
//        noti.setUserid(Userid);
//        noti.setTime(date);
//        noti.setText(user_name + " Comment On Your Post");
//        CollectionReference  notiref= db.collection("Users").document(Userid).collection("notify");
//        notiref.add(noti);



    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration!=null){
            listenerRegistration.remove();
            adapter.stopListening();
        }

    }
}