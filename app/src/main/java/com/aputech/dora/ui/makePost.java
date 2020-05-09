package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.aputech.dora.Model.Post;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class makePost extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE =1;
    private static final int REQUEST_LOCATION = 2;
    private static final int REQUEST_VIDEO = 3;
    private static final int REQUEST_AUDIO=4;
    private EditText editText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView imageView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    MaterialButton camera,gallery,audio;
    private int type;
    FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    private TextView user_name;
    private TextView time;
    private ImageView remover;
    private ImageView level;
    private Uri videoUri;
    VideoView videoView;
    private CircleImageView profile;
    private View audioView;
    private Uri imgUri;
    private Uri audioUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post);
        editText=findViewById(R.id.para);
        imageView = findViewById(R.id.dispimg);
        audio = findViewById(R.id.Audio);
        firebaseStorage=FirebaseStorage.getInstance();
        user_name = findViewById(R.id.user_name);
        time = findViewById(R.id.time);
        remover= findViewById(R.id.remover);
        level= findViewById(R.id.level);
        videoView=findViewById(R.id.videoDisplay);
        gallery=findViewById(R.id.Gallery);
        camera =findViewById(R.id.Camera);
        profile=findViewById(R.id.poster_profile);
        remover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoUri=null;
                imgUri=null;
                audioUri=null;
                videoView.setVisibility(View.GONE);
                if (videoView.isPlaying()){
                    videoView.stopPlayback();
                }
                imageView.setVisibility(View.GONE);
                audioView.setVisibility(View.GONE);
            }
        });
        DocumentReference documentReference = db.collection("Users").document(auth.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                user_name.setText(user.getUserName());
                String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                time.setText(currentDateTimeString);
                if (user.getPostnum() < 100) {
                    Glide
                            .with(makePost.this)
                            .load(R.drawable.ic_grade)
                            .into(level);
                }
                if (user.getPostnum() < 100 && user.getPostnum() > 500) {
                    Glide
                            .with(makePost.this)
                            .load(R.drawable.ic_grade1)
                            .into(level);
                }
                if (user.getPostnum() > 500) {
                    Glide
                            .with(makePost.this)
                            .load(R.drawable.ic_grade2)
                            .into(level);
                }
                if (user.getProfileUrl()!=null){
                    Glide
                            .with(makePost.this)
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
                Intent intent= new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_VIDEO);
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
        MediaController mediaController= new MediaController(makePost.this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();

    }

    public void Done(View view) {
        Intent intent= new Intent(makePost.this,SelectLocation.class);
        intent.putExtra("type",type);
        intent.putExtra("Desc",editText.getText().toString());
        intent.putExtra("user_id",auth.getUid());
        if (type == 3) {
            intent.putExtra("Uri", videoUri.toString());
            intent.putExtra("ext",getfileExt(videoUri));
        }
        startActivityForResult(intent,REQUEST_LOCATION);
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
        if (requestCode==REQUEST_VIDEO && resultCode ==RESULT_OK && data !=null && data.getData()!=null){
            videoUri=data.getData();
            videoView.setVideoURI(videoUri);
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            type=3;
        }
        if (requestCode == REQUEST_AUDIO && resultCode ==RESULT_OK && data !=null && data.getData()!=null) {
//            Mediafileinfo item = audioList.get(i);
//            Uri myUri = Uri.parse(item.getData());
//            mediaPlayer = new MediaPlayer();
//            try {
//                // mediaPlayer.setDataSource(String.valueOf(myUri));
//                mediaPlayer.setDataSource(MainActivity.this,myUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                mediaPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mediaPlayer.start();
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            videoUri=null;
            if (videoView.isPlaying()){
                videoView.stopPlayback();
                videoView.setVideoURI(null);
            }
            type=2;

        }
        if (requestCode ==REQUEST_LOCATION && resultCode == RESULT_OK){
            finish();
        }
    }
    private String getfileExt(Uri videoUri){
        ContentResolver contentResolver= getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (videoUri!=null){
            videoView.start();
        }

    }
}
