package com.aputech.dora.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.aputech.dora.Adpater.FireAdapter;
import com.aputech.dora.Model.Note;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDisplayActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userinfo ;
    private CollectionReference notebookRef = db.collection("Notebook");
    private FireAdapter adapter;
    FloatingActionButton editImage;
    private User user;
  private Boolean follower;
    private MaterialButton settings;
    private CircleImageView profileimg;
    private TextView following,posts,followers,name,bio;
    private ImageView level;
    private Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 1;
    int Activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent= getIntent();
        Activity = intent.getIntExtra("act",1);
        final String Userid= intent.getStringExtra("user_id");
        userinfo = db.collection("Users").document(Userid);
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
       settings= findViewById(R.id.followandset);
       bio = findViewById(R.id.bio);
        final RelativeLayout layout= findViewById(R.id.topdisp);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
         editImage = findViewById(R.id.editimage);

        followers= findViewById(R.id.numFolo);
        name= findViewById(R.id.nametitle);
        profileimg= findViewById(R.id.profiledisplay);
        following =findViewById(R.id.numFoly);
        posts =findViewById(R.id.numPosts);
        level=findViewById(R.id.level);
        if (Activity==0){
            editImage.setVisibility(View.VISIBLE);
            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
            });
            settings.setText("Settings");
            userinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class);
//                    Query profilequery= notebookRef.whereIn(FieldPath.documentId(),user.getPosts()).orderBy("priority", Query.Direction.DESCENDING);
//
//                    FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
//                            .setQuery(profilequery, Note.class)
//                            .build();
//                    adapter = new FireAdapter(options,getApplicationContext());
//
//                    recyclerView.setAdapter(adapter);
//                    adapter.startListening();
//                    posts.setText(String.valueOf(user.getPosts().size()));
//                    if (user.getFollowing()!=null && user.getFollowers()!=null){
//                        following.setText(String.valueOf(user.getFollowing().size()));
//                        followers.setText(String.valueOf(user.getFollowers().size()));
//                    }else{
//                        following.setText(String.valueOf(0));
//                        followers.setText(String.valueOf(0));
//                    }
//
//                    if (user.getUserlevel()==0){
//                        Glide
//                                .with(getApplicationContext())
//                                .load(R.drawable.ic_grade)
//                                .into(level);
//                    }
//                    if (user.getUserlevel()==1){
//                        Glide
//                                .with(getApplicationContext())
//                                .load(R.drawable.ic_grade1)
//                                .into(level);
//                    }
//                    if (user.getUserlevel()==2){
//                        Glide
//                                .with(getApplicationContext())
//                                .load(R.drawable.ic_grade2)
//                                .into(level);
//                    }
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarLayout.setTitle(user.getUserName());
                                layout.setVisibility(View.INVISIBLE);
                                isShow = true;
                            } else if(isShow) {
                                collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                                layout.setVisibility(View.VISIBLE);
                                isShow = false;
                            }
                        }
                    });
                    bio.setText(user.getBio());
                    name.setText(user.getUserName());
                    Glide
                            .with(getApplicationContext())
                            .load(user.getProfileUrl())
                            .into(profileimg);
                    settings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileDisplayActivity.this,ProfileSettings.class);
                            startActivity(intent);
                        }
                    });
                }


            });
        }if (Activity==1){
            editImage.setVisibility(View.INVISIBLE);
            settings.setText("Follow");
            userinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class);
//                    Query profilequery= notebookRef.whereIn(FieldPath.documentId(),user.getPosts()).orderBy("priority", Query.Direction.DESCENDING);
//
//                    FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
//                            .setQuery(profilequery, Note.class)
//                            .build();
//                    adapter = new FireAdapter(options,getApplicationContext());
//
//                    recyclerView.setAdapter(adapter);
//                    adapter.startListening();
                 //   posts.setText(String.valueOf(user.getPosts().size()));
//                    if (user.getFollowing()!=null && user.getFollowers()!=null){
//                        following.setText(String.valueOf(user.getFollowing().size()));
//                        followers.setText(String.valueOf(user.getFollowers().size()));
//                    }else{
//                        following.setText(String.valueOf(0));
//                        followers.setText(String.valueOf(0));
//                    }
//
//                    if (user.getUserlevel()==0){
//                        Glide
//                                .with(getApplicationContext())
//                                .load(R.drawable.ic_grade)
//                                .into(level);
//                    }
//                    if (user.getUserlevel()==1){
//                        Glide
//                                .with(getApplicationContext())
//                                .load(R.drawable.ic_grade1)
//                                .into(level);
//                    }
//                    if (user.getUserlevel()==2){
//                        Glide
//                                .with(getApplicationContext())
//                                .load(R.drawable.ic_grade2)
//                                .into(level);
//                    }
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarLayout.setTitle(user.getUserName());
                                layout.setVisibility(View.INVISIBLE);
                                isShow = true;
                            } else if(isShow) {
                                collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                                layout.setVisibility(View.VISIBLE);
                                isShow = false;
                            }
                        }
                    });
                    bio.setText(user.getBio());
                    name.setText(user.getUserName());
                    Glide
                            .with(getApplicationContext())
                            .load(user.getProfileUrl())
                            .into(profileimg);
//                    if (user.getFollowers()!=null){
//                        if (user.getFollowers().contains(auth.getUid())){
//                            settings.setText("UNFOLLOW");
//                        }
//                    }
                    settings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if (user.getFollowers()!=null){
//                                if (user.getFollowers().contains(auth.getUid())){
//                                    DocumentReference documentRef= db.collection("Users").document(auth.getUid());
//                                    DocumentReference documentReference= db.collection("Users").document(user.getUserid());
//                                    documentReference.update("followers", FieldValue.arrayRemove(auth.getUid()));
//                                    documentRef.update("following", FieldValue.arrayRemove(user.getUserid()));
//                                    settings.setText("FOLLOW");
//                                }else{
//                                    DocumentReference documentRef= db.collection("Users").document(auth.getUid());
//                                    DocumentReference documentReference= db.collection("Users").document(user.getUserid());
//                                    documentReference.update("followers", FieldValue.arrayUnion(auth.getUid()));
//                                    documentRef.update("following", FieldValue.arrayUnion(user.getUserid()));
//                                    settings.setText("UNFOLLOW");
//                                }
//                            }else{
//                                DocumentReference documentRef= db.collection("Users").document(auth.getUid());
//                                DocumentReference documentReference= db.collection("Users").document(user.getUserid());
//                                documentReference.update("followers", FieldValue.arrayUnion(auth.getUid()));
//                                documentRef.update("following", FieldValue.arrayUnion(user.getUserid()));
//                            }

                        }
                    });
                }


            });


        }



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileimg.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
