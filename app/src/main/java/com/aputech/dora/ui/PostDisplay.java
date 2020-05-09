package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.aputech.dora.Adpater.CommentAdapter;
import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.Post;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;

public class PostDisplay extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private CommentAdapter adapter;
    MaterialButton up,down;
    private EditText editText;
    RelativeLayout noresult;
    int Commentnum;
    EventListener<DocumentSnapshot> eventListener;
    ImageView delete,edit;
    String TAG="bigp";
    Post post;
    VideoView playerView;
    ImageView image;
    ListenerRegistration listenerRegistration;
    private TextView postText;
    private TextView userName,post_time;
    ImageView locate,ProfileImg;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);
        editText = findViewById(R.id.commenttext);
        noresult= findViewById(R.id.noresult);
        userName = findViewById(R.id.user_name);
        post_time = findViewById(R.id.time);
        image=findViewById(R.id.img);
         playerView = findViewById(R.id.video_view);
        locate =findViewById(R.id.locate);
        up = findViewById(R.id.upbutton);
        delete = findViewById(R.id.delete);
        edit = findViewById(R.id.edit);
        down = findViewById(R.id.downbutton);
        postText = findViewById(R.id.text_view_description);
        ProfileImg = findViewById(R.id.poster_profile);
        Intent intent= getIntent();
        post = intent.getParcelableExtra("post");
        if (post ==null){
            Toast.makeText(PostDisplay.this, "Post Has Been Removed Refresh Page", Toast.LENGTH_SHORT).show();
            finish();
        }
        int Type = post.getType();
        if (post.getTimestamp() != null) {
            Date date = post.getTimestamp();
            String df = DateFormat.getDateFormat(PostDisplay.this).format(date).concat("  ").concat(DateFormat.getTimeFormat(PostDisplay.this).format(date));
            post_time.setText(df);
        }
        postText.setText(post.getDescription());
        Log.d(TAG, "onCreate: "+post.getLocation());
        if (post.getLocation()!=null){
            locate.setImageResource(R.drawable.ic_locationhappy);
            locate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1= new Intent(PostDisplay.this,DispPostLocation.class);
                    intent1.putExtra("lat",post.getLocation().getLatitude());
                    intent1.putExtra("lng",post.getLocation().getLongitude());
                    startActivity(intent1);
                }
            });
        }else{
            locate.setImageResource(R.drawable.ic_locationsad);
        }
        db.collection("Users").document(post.getUserid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
              final User user = documentSnapshot.toObject(User.class);
        userName.setText(user.getUserName());
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDisplay.this, ProfileDisplayActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
        ProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDisplay.this, ProfileDisplayActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
        if (user.getProfileUrl()!=null){
            Glide
                    .with(PostDisplay.this)
                    .load(user.getProfileUrl())
                    .into(ProfileImg);
        }


        if (user.getUserid().equals(auth.getUid())){
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostDisplay.this);
                    builder.setTitle("Delete Post");
                    builder.setMessage("Are you sure to Delete This Post?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DeletePost(post.getRefComments());
                            finish();
                            Toast.makeText(getApplicationContext(),
                                    "PostDeleted",Toast.LENGTH_SHORT).show();

                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when No button clicked
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostDisplay.this);
                    builder.setTitle("Edit Post");
                    final View customLayout =  LayoutInflater.from(PostDisplay.this).inflate(R.layout.custom_alert, null);
                    builder.setView(customLayout);
                    final EditText editText = customLayout.findViewById(R.id.para);
                    editText.setText(post.getDescription());
                    builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!editText.getText().toString().isEmpty()){
                                db.collection("Posts").document(post.getRefComments()).update("description",editText.getText().toString());
                                postText.setText(editText.getText().toString());
                                Toast.makeText(PostDisplay.this,"Post Updated",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(PostDisplay.this,"Unable to Make Changes Field Empty",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Pass
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }




            }
        });
        if (Type==2){
            image.setVisibility(View.VISIBLE);
            Glide
                    .with(PostDisplay.this)
                    .load(post.getImageUrl())
                    .into(image);
        }
        if (Type==3){
//            videoView.setVisibility(View.VISIBLE);
//           // image.setVisibility(View.VISIBLE);
            playerView.setVisibility(View.VISIBLE);
            String link = post.getVideoUrl();
////            long thumb = position*1000;
////            RequestOptions options = new RequestOptions().frame(thumb);
//          //  Glide.with(PostDisplay.this).load(link).into(image);
            MediaController mediaController = new MediaController(PostDisplay.this);
            mediaController.setAnchorView(playerView);
            Uri video = Uri.parse(link);
            playerView.setMediaController(mediaController);
            playerView.setVideoURI(video);
            playerView.start();
//            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//            TrackSelector trackSelector= new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
//            exoPlayer= ExoPlayerFactory.newSimpleInstance(PostDisplay.this,trackSelector);
//            extractorsFactory = new DefaultExtractorsFactory();
//            PlayVideo();
            


        }
        documentReference = db.collection("Posts").document(post.getRefComments());
         eventListener =new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
              Post n = documentSnapshot.toObject(Post.class);
              up.setText(String.valueOf(n.getUpnum()));
                down.setText(String.valueOf(n.getDownnum()));
            }
        };
        final DocumentReference postrefrence = db.collection("Posts").document(post.getRefComments());
        final DocumentReference Reference = db.collection("Posts").document(post.getRefComments()).collection("vote").document(auth.getUid());
        listenerRegistration=documentReference.addSnapshotListener(eventListener);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postrefrence.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final Post doc = documentSnapshot.toObject(Post.class);
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
                        final Post doc = documentSnapshot.toObject(Post.class);
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
        CollectionReference notebookRef = db.collection("Posts").document(post.getRefComments()).collection("comments");
        Query query = notebookRef.orderBy("priority", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();
        adapter = new CommentAdapter(options, post.getRefComments(),PostDisplay.this);
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



    private void DeletePost(final String Postid) {
        final WriteBatch writeBatch = db.batch();
        //Posts->vote->
        //      comment->vote->
        db.collection("Posts").document(Postid).collection("vote").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    writeBatch.delete(documentSnapshot.getReference());
                }
                db.collection("Posts").document(Postid).collection("comments").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            writeBatch.delete(documentSnapshot.getReference());
                            deleteComment(documentSnapshot.getReference().getId(),Postid);
                        }
                        writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.collection("Posts").document(Postid).delete();
                            }
                        });
                    }
                });

            }

        });
    }
    private void deleteComment(String ref, String post) {
       final WriteBatch writeBatch = db.batch();
          db.collection("Posts").document(post).collection("comments").document(ref).collection("vote").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    writeBatch.delete(documentSnapshot.getReference());
                }
                writeBatch.commit();
            }

        });

    }
    public void sendcomment(View view) {

        String comenttext= editText.getText().toString();
        if (!comenttext.isEmpty()){
            DocumentReference documentReference = db.collection("Posts").document(post.getRefComments());
            final CollectionReference col=db.collection("Posts").document(post.getRefComments()).collection("comments");
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
            editText.setText("");
            DocumentReference userinfo = db.collection("Users").document(auth.getUid());
            if (!auth.getUid().equals(post.getUserid())){
                userinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User u = documentSnapshot.toObject(User.class);
                        notification noti = new notification();
                        noti.setDocument(post.getRefComments());
                        noti.setUserid(post.getUserid());
                        noti.setText(u.getUserName() + "  Comment On Your Post");
                        CollectionReference  notiref= db.collection("Users").document(post.getUserid()).collection("notify");
                        notiref.add(noti);
                    }
                });
            }
        }else{
            Toast.makeText(PostDisplay.this, "Nothing to comment", Toast.LENGTH_LONG).show();
        }





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
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top);
    }
}