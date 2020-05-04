package com.aputech.dora.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOCATION = 345;

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm aa");
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
        Intent intent = new Intent(MakePP.this, SelectLocation.class);
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            imageView.setVisibility(View.VISIBLE);
        }
        if (requestCode == REQUEST_LOCATION) {
            Bundle extras = data.getExtras();
            latLng = (LatLng) extras.get("LatLng");
            boolean skipcheck = (boolean) extras.get("skip");
            Toast.makeText(MakePP.this, latLng.toString(), Toast.LENGTH_LONG).show();
            uploadFire(type, skipcheck);
        }
    }

    private void uploadFire(int type, boolean skip) {
        String text = editText.getText().toString();
        final Post post = new Post();
        if (!skip) {
            GeoPoint geoPoint = new GeoPoint(latLng.latitude, latLng.longitude);
            post.setLocation(geoPoint);
        } else {
            post.setLocation(null);
        }
        post.setType(type);
        post.setDescription(text);
        post.setUserid(auth.getUid());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm aa");
        if (type == 2) {
            post.setImageUrl("sdasda");
        }
        if (type == 3) {
            post.setVideoUrl("sdadadasd");

        }
        if (type == 4) {
            post.setAudioUrl("dnaoidaoid");
        }
        final String date = dateFormat.format(Calendar.getInstance().getTime());
        notebookRef.add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String commentref = documentReference.getId();
                post.setRefComments(commentref);
               // post.setUptime(date);
                documentReference.set(post);
                Toast.makeText(MakePP.this, "Note Added Successfully", Toast.LENGTH_LONG).show();
                finish();

            }
        });
//        if (activity==2){
//            finish();
//                CollectionReference collectionReference = db.collection("inbox");
//                message mms= new message();
//                mms.setType(1);
//                mms.setUptime("hehoa");
//                mms.setSentBy(auth.getUid());
//                GeoPoint geoPoint = new GeoPoint(latLng.latitude,latLng.longitude);
//                mms.setLocation(geoPoint);
//                collectionReference.add(mms);
//
//        }

    }
}