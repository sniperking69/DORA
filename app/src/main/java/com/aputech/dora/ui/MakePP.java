package com.aputech.dora.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Adpater.PillAdapter;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.narayanacharya.waveview.WaveView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MakePP extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 8;
    private static final int REQUEST_LOCATION = 9;
    private static final int REQUEST_VIDEO = 10;
    private static final int REQUEST_AUDIO = 11;
    private static final int GALLERY = 12;
    MaterialButton camera, gallery, audio;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    TextView curTime;
    TextView totTime;
    FloatingActionButton playPause;
    SeekBar mSeekBar;
    ArrayList<User> sendto = new ArrayList<>();
    PillAdapter pillAdapter;
    RecyclerView contact_view;
    User user;
    private EditText editText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView imageView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private int type = 1;
    private TextView user_name;
    private TextView time;
    private MaterialButton remover;
    private ImageView level;
    private Uri videoUri;
    private CircleImageView profile;
    private View audioView;
    private Uri imgUri;
    private Uri audioUri;
    private WaveView sine;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private MediaPlayer mMediaPlayer;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private int GET_USERS = 15;
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
        setContentView(R.layout.activity_make_p_p);
        editText = findViewById(R.id.para);
        time = findViewById(R.id.time);
        locationCheck();
        level = findViewById(R.id.level);
        MaterialButton button = findViewById(R.id.AddUser);
        contact_view = findViewById(R.id.contact_view);
        contact_view.setHasFixedSize(true);
        contact_view.setLayoutManager(new LinearLayoutManager(MakePP.this, LinearLayoutManager.HORIZONTAL, false));
        pillAdapter = new PillAdapter(sendto);
        contact_view.setAdapter(pillAdapter);
        profile = findViewById(R.id.poster_profile);
        gallery = findViewById(R.id.Gallery);
        camera = findViewById(R.id.Camera);
        imageView = findViewById(R.id.dispimg);
        audio = findViewById(R.id.Audio);
        audioView = findViewById(R.id.audiolayout);
        firebaseStorage = FirebaseStorage.getInstance();
        user_name = findViewById(R.id.user_name);
        time = findViewById(R.id.time);
        remover = findViewById(R.id.remover);
        level = findViewById(R.id.level);
        mSeekBar = findViewById(R.id.mSeekBar);
        playPause = findViewById(R.id.playPause);
        curTime = findViewById(R.id.curTime);
        totTime = findViewById(R.id.totalTime);
        playerView = findViewById(R.id.videoDisplay);
        gallery = findViewById(R.id.Gallery);
        sine = findViewById(R.id.waveView);
        camera = findViewById(R.id.Camera);
        profile = findViewById(R.id.poster_profile);
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        remover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
                videoUri = null;
                imgUri = null;
                audioUri = null;
                type = 1;
                if (player != null) {
                    if (isPlaying()) {
                        player.stop();
                        player.release();
                    }
                }
                playerView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                audioView.setVisibility(View.GONE);
                remover.setVisibility(View.INVISIBLE);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakePP.this, SelectUser.class);
                intent.putExtra("sendto", pillAdapter.getUserList());
                startActivityForResult(intent, GET_USERS);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
        DocumentReference documentReference = db.collection("Users").document(auth.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                user_name.setText(user.getUserName());
                String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                time.setText(currentDateTimeString);
                if (user.getPostnum() < 100) {
                    Glide
                            .with(MakePP.this)
                            .load(R.drawable.ic_grade)
                            .into(level);
                }
                if (user.getPostnum() < 100 && user.getPostnum() > 500) {
                    Glide
                            .with(MakePP.this)
                            .load(R.drawable.ic_grade1)
                            .into(level);
                }
                if (user.getPostnum() > 500) {
                    Glide
                            .with(MakePP.this)
                            .load(R.drawable.ic_grade2)
                            .into(level);
                }
                if (user.getProfileUrl() != null) {
                    Glide
                            .with(MakePP.this)
                            .load(user.getProfileUrl())
                            .into(profile);
                }

            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent audiointent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(audiointent, REQUEST_AUDIO);
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


    }

    public void Done(View view) {
        if (!editText.getText().toString().isEmpty()) {
            if (!pillAdapter.getUserList().isEmpty()) {
                Intent intent = new Intent(MakePP.this, SelectPrivateLocation.class);
                intent.putExtra("type", type);
                intent.putExtra("currentuser", user);
                intent.putExtra("sendto", sendto);
                intent.putExtra("Desc", editText.getText().toString());
                intent.putExtra("user_id", auth.getUid());
                if (type == 2) {
                    intent.putExtra("Uri", imgUri.toString());
                    intent.putExtra("ext", getfileExt(imgUri));
                }
                if (type == 3) {
                    intent.putExtra("Uri", videoUri.toString());
                    intent.putExtra("ext", getfileExt(videoUri));
                }
                if (type == 4) {
                    intent.putExtra("Uri", audioUri.toString());
                    intent.putExtra("ext", getfileExt(audioUri));
                }
                startActivityForResult(intent, REQUEST_LOCATION);
            } else {
                Toast.makeText(MakePP.this, "Pick A User to Send The Message", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(MakePP.this, "Write Something To Post", Toast.LENGTH_SHORT).show();
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "ProfileImage", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            initializePlayer(videoUri);
            remover.setVisibility(View.VISIBLE);
            playerView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            if (player != null) {
                if (isPlaying()) {
                    player.stop();
                    player.release();
                }
            }
            audioView.setVisibility(View.GONE);
            audioUri = null;
            imgUri = null;
            type = 3;
        }
        if (requestCode == REQUEST_AUDIO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            audioUri = data.getData();
            initPlayer(audioUri);
            remover.setVisibility(View.VISIBLE);
            playerView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            if (player != null) {
                if (isPlaying()) {
                    player.stop();
                    player.release();
                }
            }
            audioView.setVisibility(View.VISIBLE);
            videoUri = null;
            imgUri = null;
            type = 4;
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            imgUri = getImageUri(MakePP.this, imageBitmap);
            remover.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            playerView.setVisibility(View.GONE);
            if (player != null) {
                if (isPlaying()) {
                    player.stop();
                    player.release();
                }
            }
            audioView.setVisibility(View.GONE);
            pause();
            videoUri = null;
            audioUri = null;
            type = 2;

        }
        if (requestCode == GALLERY && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imgUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(MakePP.this.getContentResolver(), imgUri);
                imageView.setImageBitmap(bitmap);
                remover.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
                if (player != null) {
                    if (isPlaying()) {
                        player.stop();
                        player.release();
                    }
                }

                audioView.setVisibility(View.GONE);
                pause();
                videoUri = null;
                audioUri = null;
                type = 2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_LOCATION && resultCode == RESULT_OK) {
            finish();
        }
        if (requestCode == GET_USERS && resultCode == RESULT_OK) {
            sendto = data.getParcelableArrayListExtra("sendto");
            pillAdapter = new PillAdapter(sendto);
            contact_view.setAdapter(pillAdapter);
        }
    }

    private String getfileExt(Uri videoUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
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

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        pictureDialog.setIcon(R.drawable.ic_upload);
        String[] pictureDialogItems = {
                "Gallery Image",
                "Video"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                chooseVideo();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_VIDEO);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player == null && videoUri != null) {
            initializePlayer(videoUri);
        }
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
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
            pause();
        }
    }

    public void locationCheck() {
        Dexter.withActivity(this)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET
                ))
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

}