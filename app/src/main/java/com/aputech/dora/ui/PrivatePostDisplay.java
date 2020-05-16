package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aputech.dora.Adpater.CommentAdapter;
import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.Post;
import com.aputech.dora.Model.User;
import com.aputech.dora.Model.Vote;
import com.aputech.dora.Model.message;
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
import com.google.android.gms.maps.model.LatLng;
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

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class PrivatePostDisplay extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    ImageView delete,edit;
    ImageView image;
    private TextView postText;
    private TextView userName,post_time;
    ImageView locate,ProfileImg;
    private Uri videoUri;
    private View audioView;
    private Uri audioUri;
    private WaveView sine;
    TextView curTime;
    TextView totTime;
    TextView GeoLocation;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    static MediaPlayer mMediaPlayer;
    FloatingActionButton playPause;
    SeekBar mSeekBar;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_post_display);
        userName = findViewById(R.id.user_name);
        post_time = findViewById(R.id.time);
        image=findViewById(R.id.img);
        locate =findViewById(R.id.locate);
        delete = findViewById(R.id.delete);
        edit = findViewById(R.id.edit);
        postText = findViewById(R.id.text_view_description);
        ProfileImg = findViewById(R.id.poster_profile);
        mSeekBar = findViewById(R.id.mSeekBar);
        playPause=findViewById(R.id.playPause);
        curTime = findViewById(R.id.curTime);
        GeoLocation=findViewById(R.id.GeoCode);
        totTime = findViewById(R.id.totalTime);
        playerView=findViewById(R.id.videoDisplay);
        audioView= findViewById(R.id.audiocard);
        sine = findViewById(R.id.waveView);
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
        String msg= intent.getStringExtra("post");

        db.collection("Inbox").document(msg).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final message pst = documentSnapshot.toObject(message.class);
                if (pst.getType() == 2) {
                    if (postText.getText().toString().isEmpty()) {
                        postText.setVisibility(View.GONE);
                    }
                    image.setVisibility(View.VISIBLE);
                    Glide
                            .with(PrivatePostDisplay.this)
                            .load(pst.getImageUrl())
                            .into(image);
                }
                if (pst.getType() == 3) {
                    if (postText.getText().toString().isEmpty()) {
                        postText.setVisibility(View.GONE);
                    }
                    playerView.setVisibility(View.VISIBLE);
                    initializePlayer(Uri.parse(pst.getVideoUrl()));
                }
                if (pst.getType() == 4) {
                    if (postText.getText().toString().isEmpty()) {
                        postText.setVisibility(View.GONE);
                    }
                    audioView.setVisibility(View.VISIBLE);
                    audioUri = Uri.parse(pst.getAudioUrl());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initPlayer(audioUri);
                        }
                    }, 500);
                }

                if (pst.getTimestamp() != null) {
                    Date date = pst.getTimestamp();
                    String df = DateFormat.getDateFormat(PrivatePostDisplay.this).format(date).concat("  ").concat(DateFormat.getTimeFormat(PrivatePostDisplay.this).format(date));
                    post_time.setText(df);
                }
                postText.setText(pst.getDescription());
                Geocodeget(pst.getLocation().getLatitude(),pst.getLocation().getLongitude());
                db.collection("Users").document(pst.getSender()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final User user = documentSnapshot.toObject(User.class);
                        userName.setText(user.getUserName());
                        userName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PrivatePostDisplay.this, ProfileDisplayActivity.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                            }
                        });
                        ProfileImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PrivatePostDisplay.this, ProfileDisplayActivity.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                            }
                        });
                        if (user.getProfileUrl() != null) {
                            Glide
                                    .with(PrivatePostDisplay.this)
                                    .load(user.getProfileUrl())
                                    .into(ProfileImg);
                        }


                        if (user.getUserid().equals(auth.getUid())) {
                            delete.setVisibility(View.VISIBLE);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PrivatePostDisplay.this);
                                    builder.setTitle("Delete Post");
                                    builder.setMessage("Are you sure to Delete This Post?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DeletePrivatePost(pst.getRefmsg(),pst.getType(),pst.getAudioUrl(),pst.getVideoUrl(),pst.getImageUrl());
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PrivatePostDisplay.this);
                                    builder.setTitle("Edit Post");
                                    final View customLayout = LayoutInflater.from(PrivatePostDisplay.this).inflate(R.layout.custom_alert, null);
                                    builder.setView(customLayout);
                                    final EditText editText = customLayout.findViewById(R.id.para);
                                    editText.setText(pst.getDescription());
                                    builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (!editText.getText().toString().isEmpty()) {
                                                db.collection("Inbox").document(pst.getSender()).update("description", editText.getText().toString());
                                                postText.setText(editText.getText().toString());
                                                Toast.makeText(PrivatePostDisplay.this, "Post Updated", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(PrivatePostDisplay.this, "Unable to Make Changes Field Empty", Toast.LENGTH_LONG).show();
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


            }});
    }



    private void DeletePrivatePost(final String Postid,int type,String Audio,String Video,String Image) {
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
        if (type==2){
            StorageReference ref = firebaseStorage.getReferenceFromUrl(Image);
            ref.delete();
        }if (type==3){
            StorageReference ref = firebaseStorage.getReferenceFromUrl(Video);
            ref.delete();
        }if (type==4){
            StorageReference ref = firebaseStorage.getReferenceFromUrl(Audio);
            ref.delete();
        }
        db.collection("Inbox").document(Postid).delete();
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
        if (mMediaPlayer!=null){
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
                new DefaultDataSourceFactory(PrivatePostDisplay.this, "Drop Chat");
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

        if (Util.SDK_INT >= 24) {
            releasePlayer();
            pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player == null && videoUri !=null) {
            initializePlayer(videoUri);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top);
    }
    private void Geocodeget(double lat, double lng){

        LatLng latLng= new LatLng(lat,lng);
        Geocoder geocoder = new Geocoder(PrivatePostDisplay.this);
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                String locality = addressList.get(0).getAddressLine(0);
                String country = addressList.get(0).getCountryName();
                String geocode = null;
                if (!locality.isEmpty() && !country.isEmpty())
                    geocode=locality ;
                GeoLocation.setText(geocode);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
