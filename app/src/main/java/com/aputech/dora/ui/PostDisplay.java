package com.aputech.dora.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Adpater.CommentAdapter;
import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.Post;
import com.aputech.dora.Model.User;
import com.aputech.dora.Model.Vote;
import com.aputech.dora.Model.notification;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.narayanacharya.waveview.WaveView;

import java.util.Date;

public class PostDisplay extends AppCompatActivity {
    static MediaPlayer mMediaPlayer;
    MaterialButton up, down;
    RelativeLayout noresult;
    int Commentnum;
    EventListener<DocumentSnapshot> eventListener;
    ImageView delete, edit;
    String TAG = "bigp";
    MaterialButton sendcom;
    NestedScrollView nestedScrollView;
    ImageView image;
    ListenerRegistration listenerRegistration;
    ImageView locate, ProfileImg;
    DocumentReference documentReference;
    TextView curTime;
    TextView totTime;
    FloatingActionButton playPause;
    SeekBar mSeekBar;
    RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private CommentAdapter adapter;
    private EditText editText;
    private TextView postText;
    private TextView userName, post_time;
    private Uri videoUri;
    private View audioView;
    private Uri audioUri;
    private WaveView sine;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            Log.i("handler ", "handler called");
            int current_position = msg.what;
            mSeekBar.setProgress(current_position);
            String cTime = createTimeLabel(current_position);
            curTime.setText(cTime);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);
        editText = findViewById(R.id.commenttext);
        noresult = findViewById(R.id.noresult);
        userName = findViewById(R.id.user_name);
        post_time = findViewById(R.id.time);
        image = findViewById(R.id.img);
        locate = findViewById(R.id.locate);
        up = findViewById(R.id.upbutton);
        delete = findViewById(R.id.delete);
        sendcom = findViewById(R.id.sendcomment);
        edit = findViewById(R.id.edit);
        down = findViewById(R.id.downbutton);
        postText = findViewById(R.id.text_view_description);
        ProfileImg = findViewById(R.id.poster_profile);
        mSeekBar = findViewById(R.id.mSeekBar);
        playPause = findViewById(R.id.playPause);
        curTime = findViewById(R.id.curTime);
        totTime = findViewById(R.id.totalTime);
        playerView = findViewById(R.id.videoDisplay);
        audioView = findViewById(R.id.audiocard);
        sine = findViewById(R.id.waveView);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(PostDisplay.this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                }
            }
        });
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        Intent intent = getIntent();
        String postID = intent.getStringExtra("post");
        db.collection("Posts").document(postID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Post post = documentSnapshot.toObject(Post.class);
                if (post.getType() == 2) {
                    if (post.getDescription().isEmpty()) {
                        postText.setVisibility(View.GONE);
                    }
                    image.setVisibility(View.VISIBLE);
                    Glide
                            .with(PostDisplay.this)
                            .load(post.getImageUrl())
                            .into(image);
                }
                if (post.getType() == 3) {
                    if (post.getDescription().isEmpty()) {
                        postText.setVisibility(View.GONE);
                    }
                    playerView.setVisibility(View.VISIBLE);
                    initializePlayer(Uri.parse(post.getVideoUrl()));
                }
                if (post.getType() == 4) {
                    if (post.getDescription().isEmpty()) {
                        postText.setVisibility(View.GONE);
                    }
                    audioView.setVisibility(View.VISIBLE);
                    audioUri = Uri.parse(post.getAudioUrl());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initPlayer(audioUri);
                        }
                    }, 500);
                }
                sendcom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendcomment(post);
                    }
                });

                if (post.getTimestamp() != null) {
                    Date date = post.getTimestamp();
                    String df = DateFormat.getDateFormat(PostDisplay.this).format(date).concat("  ").concat(DateFormat.getTimeFormat(PostDisplay.this).format(date));
                    post_time.setText(df);
                }
                postText.setText(post.getDescription());
                if (post.getLocation() != null) {
                    locate.setImageResource(R.drawable.ic_locationhappy);
                    locate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent1 = new Intent(PostDisplay.this, DispPostLocation.class);
                            intent1.putExtra("lat", post.getLocation().getLatitude());
                            intent1.putExtra("lng", post.getLocation().getLongitude());
                            startActivity(intent1);
                        }
                    });
                } else {
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
                                intent.putExtra("user", user);
                                startActivity(intent);
                            }
                        });
                        ProfileImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PostDisplay.this, ProfileDisplayActivity.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                            }
                        });
                        if (user.getProfileUrl() != null) {
                            Glide
                                    .with(PostDisplay.this)
                                    .load(user.getProfileUrl())
                                    .into(ProfileImg);
                        }


                        if (user.getUserid().equals(auth.getUid())) {
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
                                            DeletePost(post.getRefComments(), post.getType(), post.getAudioUrl(), post.getVideoUrl(), post.getImageUrl());
                                            finish();
                                            Toast.makeText(getApplicationContext(),
                                                    "PostDeleted", Toast.LENGTH_SHORT).show();

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
                                    final View customLayout = LayoutInflater.from(PostDisplay.this).inflate(R.layout.custom_alert, null);
                                    builder.setView(customLayout);
                                    final EditText editText = customLayout.findViewById(R.id.para);
                                    editText.setText(post.getDescription());
                                    builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (!editText.getText().toString().isEmpty()) {
                                                db.collection("Posts").document(post.getRefComments()).update("description", editText.getText().toString());
                                                postText.setText(editText.getText().toString());
                                                Toast.makeText(PostDisplay.this, "Post Updated", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(PostDisplay.this, "Unable to Make Changes Field Empty", Toast.LENGTH_LONG).show();
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

                documentReference = db.collection("Posts").document(post.getRefComments());
                eventListener = new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        Post n = documentSnapshot.toObject(Post.class);
                        up.setText(String.valueOf(n.getUpnum()));
                        down.setText(String.valueOf(n.getDownnum()));
                        db.collection("Posts").document(post.getRefComments()).collection("vote").document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    up.setIconTintResource(R.color.colorPrimary);
                                    down.setIconTintResource(R.color.colorPrimary);
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Vote vote = document.toObject(Vote.class);
                                        if (vote.isVotecheck()) {
                                            up.setIconTintResource(R.color.level2);
                                        } else {
                                            down.setIconTintResource(R.color.level2);
                                        }
                                    }
                                }

                            }
                        });
                    }
                };
                final DocumentReference postrefrence = db.collection("Posts").document(post.getRefComments());
                final DocumentReference Reference = db.collection("Posts").document(post.getRefComments()).collection("vote").document(auth.getUid());
                listenerRegistration = documentReference.addSnapshotListener(eventListener);
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
                                                    Reference.delete();
                                                    postrefrence.update("upnum", doc.getUpnum() - 1);
                                                    postrefrence.update("priority", (doc.getUpnum() - 1) * 0.4 + (doc.getDownnum()) * 0.2 + doc.getCommentnum() * 0.4);
                                                } else {
                                                    Reference.update("votecheck", true);
                                                    postrefrence.update("upnum", doc.getUpnum() + 1);
                                                    postrefrence.update("downnum", doc.getDownnum() - 1);
                                                    postrefrence.update("priority", (doc.getUpnum() + 1) * 0.4 + (doc.getDownnum() - 1) * 0.2 + doc.getCommentnum() * 0.4);
                                                }
                                            } else {
                                                Vote v = new Vote();
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
                                                    Reference.delete();
                                                    postrefrence.update("downnum", doc.getDownnum() - 1);
                                                    postrefrence.update("priority", (doc.getUpnum()) * 0.4 + (doc.getDownnum() - 1) * 0.2 + doc.getCommentnum() * 0.4);
                                                } else {
                                                    down.setIconTintResource(R.color.level2);
                                                    Reference.update("votecheck", false);
                                                    postrefrence.update("downnum", doc.getDownnum() + 1);
                                                    postrefrence.update("upnum", doc.getUpnum() - 1);
                                                    postrefrence.update("priority", (doc.getUpnum() - 1) * 0.4 + (doc.getDownnum() + 1) * 0.2 + doc.getCommentnum() * 0.4);
                                                }
                                            } else {
                                                Vote v = new Vote();
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
                adapter = new CommentAdapter(options, post.getRefComments(), PostDisplay.this);

                recyclerView.setAdapter(adapter);
                adapter.startListening();
                noresult.setVisibility(View.VISIBLE);
                adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        if (adapter.getItemCount() == 0) {
                            noresult.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        if (adapter.getItemCount() > 0) {
                            noresult.setVisibility(View.INVISIBLE);
                        } else {
                            noresult.setVisibility(View.VISIBLE);
                        }
                    }
                });


            }
        });


    }

    private void DeletePost(final String Postid, int type, String Audio, String Video, String Image) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        if (type == 2) {
            StorageReference ref = firebaseStorage.getReferenceFromUrl(Image);
            ref.delete();
        }
        if (type == 3) {
            StorageReference ref = firebaseStorage.getReferenceFromUrl(Video);
            ref.delete();
        }
        if (type == 4) {
            StorageReference ref = firebaseStorage.getReferenceFromUrl(Audio);
            ref.delete();
        }
        final WriteBatch writeBatch = db.batch();
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
                            deleteComment(documentSnapshot.getReference().getId(), Postid);
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

    public void sendcomment(final Post post) {

        String comenttext = editText.getText().toString();
        if (!comenttext.isEmpty()) {

            DocumentReference documentReference = db.collection("Posts").document(post.getRefComments());
            final CollectionReference col = db.collection("Posts").document(post.getRefComments()).collection("comments");
            documentReference.update("commentnum", Commentnum + 1);
            Comment comment = new Comment();
            comment.setUid(auth.getUid());
            comment.setCommentText(comenttext);
            comment.setUid(auth.getUid());
            col.add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    col.document(documentReference.getId()).update("commentid", documentReference.getId());
                }
            });
            editText.setText("");
            DocumentReference userinfo = db.collection("Users").document(auth.getUid());
            if (!auth.getUid().equals(post.getUserid())) {
                userinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User u = documentSnapshot.toObject(User.class);
                        notification noti = new notification();
                        noti.setDocument(post.getRefComments());
                        noti.setTyp(0);
                        noti.setUserid(u.getUserid());
                        noti.setText(u.getUserName() + "  Commented On Your Post");
                        CollectionReference notiref = db.collection("Users").document(post.getUserid()).collection("notify");
                        notiref.add(noti);
                    }
                });
            }
        } else {
            Toast.makeText(PostDisplay.this, "Nothing to comment", Toast.LENGTH_LONG).show();
        }


    }

    private void initPlayer(final Uri songResourceUri) {

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), songResourceUri); // create and load mediaplayer with song resources
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                String totalTime = createTimeLabel(mMediaPlayer.getDuration());
                totTime.setText(totalTime);
                mSeekBar.setMax(mMediaPlayer.getDuration());
                mMediaPlayer.start();
                playPause.setImageResource(R.drawable.ic_pause);

            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                initPlayer(audioUri);

            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    mSeekBar.setProgress(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null) {
                    try {
//                        Log.i("Thread ", "Thread Called");
                        // create new message to send to handler
                        if (mMediaPlayer.isPlaying()) {
                            Message msg = new Message();
                            msg.what = mMediaPlayer.getCurrentPosition();
                            handler.sendMessage(msg);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void play() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            sine.play();
            playPause.setImageResource(R.drawable.ic_pause);
        } else {
            pause();
        }

    }

    private void pause() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                sine.pause();
                playPause.setImageResource(R.drawable.ic_play);

            }
        }


    }

    public boolean isPlaying() {
        return player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady();
    }


    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;


    }

    private void initializePlayer(Uri uri) {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        MediaSource mediaSource = buildMediaSource(uri);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "Drop Chat");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null && adapter != null) {
            listenerRegistration.remove();
            adapter.stopListening();
        }
        if (Util.SDK_INT >= 24) {
            releasePlayer();
            pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listenerRegistration != null && adapter != null) {
            adapter.startListening();
        }
        if (player == null && videoUri != null) {
            initializePlayer(videoUri);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_in_top);
    }
}