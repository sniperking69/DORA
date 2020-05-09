package com.aputech.dora.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aputech.dora.Model.Post;
import com.aputech.dora.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MakePP extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 235;
    private static final int REQUEST_LOCATION = 3975;

    private EditText editText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Posts");
    private ImageView imageView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    MaterialButton camera, gallery, audio;
    boolean addaudio = false, addimage, addedvideo;
    private LatLng latLng;
    private int type;
    private TextView user_name;
    private TextView time;
    private ImageView level;
    private CircleImageView profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_p_p);
        editText = findViewById(R.id.para);
        imageView = findViewById(R.id.dispimg);
        audio = findViewById(R.id.Audio);
        user_name = findViewById(R.id.user_name);
        time = findViewById(R.id.time);
        level = findViewById(R.id.level);
        MaterialButton button= findViewById(R.id.AddUser);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakePP.this,SelectUser.class);
                startActivity(intent);
                overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });
        profile = findViewById(R.id.poster_profile);
        Intent intent = getIntent();
        type = 1;
        gallery = findViewById(R.id.Gallery);
        camera = findViewById(R.id.Camera);
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        Intent intent= new Intent(MakePP.this,SelectPrivateLocation.class);
        intent.putExtra("type",type);
        intent.putExtra("Desc",editText.getText().toString());
        intent.putExtra("user_id",auth.getUid());
//        if (type == 3) {
//            intent.putExtra("Uri", videoUri.toString());
//            intent.putExtra("ext",getfileExt(videoUri));
//        }
        startActivityForResult(intent,REQUEST_LOCATION);
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==REQUEST_VIDEO && resultCode ==RESULT_OK && data !=null && data.getData()!=null){
//            videoUri=data.getData();
//            videoView.setVideoURI(videoUri);
//            imageView.setVisibility(View.GONE);
//            videoView.setVisibility(View.VISIBLE);
//            type=3;
//        }
//        if (requestCode == AudioUpload && resultCode ==RESULT_OK && data !=null && data.getData()!=null) {
//            Toast.makeText(makePost.this,"ADD AUDIO",Toast.LENGTH_LONG).show();
//        }
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
//            imageView.setVisibility(View.VISIBLE);
//            videoView.setVisibility(View.GONE);
//            videoUri=null;
//            if (videoView.isPlaying()){
//                videoView.stopPlayback();
//                videoView.setVideoURI(null);
//            }
//            type=2;
//        }
//        if (requestCode ==REQUEST_LOCATION && resultCode == RESULT_OK){
//            finish();
//        }
//    }
//    private String getfileExt(Uri videoUri){
//        ContentResolver contentResolver= getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
//    }
}