package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class makePost extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 7087;
    private static final int REQUEST_LOCATION = 798;
    private static final int REQUEST_VIDEO = 3213;
    private EditText editText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Posts");
    private ImageView imageView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    MaterialButton camera,gallery,audio;
    private int type;
    private LatLng latLng;
    FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    StorageReference storageReference= firebaseStorage.getReference("videos");
    private TextView user_name;
    private TextView time;
    private ImageView level;
    private Uri videoUri;
    VideoView videoView;
    boolean skipcheck;
    private CircleImageView profile;
    private int AudioUpload=32143;

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
        level= findViewById(R.id.level);
        videoView=findViewById(R.id.videoDisplay);
        MediaController mediaController= new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();
        gallery=findViewById(R.id.Gallery);
        camera =findViewById(R.id.Camera);
        profile=findViewById(R.id.poster_profile);
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
                startActivityForResult(audiointent, AudioUpload);
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
        if (requestCode == AudioUpload && resultCode ==RESULT_OK && data !=null && data.getData()!=null) {
          Toast.makeText(makePost.this,"ADD AUDIO",Toast.LENGTH_LONG).show();
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







}
