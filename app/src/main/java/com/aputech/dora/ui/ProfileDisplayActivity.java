package com.aputech.dora.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Adpater.FireAdapter;
import com.aputech.dora.Model.Fol;
import com.aputech.dora.Model.Post;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDisplayActivity extends AppCompatActivity {
    private static final int GALLERY = 78;
    private static final int REQUEST_IMAGE_CAPTURE = 5678;
    FloatingActionButton editImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RelativeLayout layout;
    boolean Following;
    AppBarLayout appBarLayout;
    Query query;
    TextView email;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    RecyclerView recyclerView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FireAdapter adapter;
    private User user;
    private MaterialButton settings;
    private CircleImageView profileimg;
    private TextView following, posts, followers, name, bio;
    private ImageView level;
    private Uri filePath;
    private RecyclerView.AdapterDataObserver observer;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycler_view);
        settings = findViewById(R.id.followandset);
        bio = findViewById(R.id.bio);
        layout = findViewById(R.id.topdisp);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        appBarLayout = findViewById(R.id.app_bar);
        settings.setText("FOLLOW");
        email = findViewById(R.id.email);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProfileDisplayActivity.this));
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        editImage = findViewById(R.id.editimage);
        followers = findViewById(R.id.numFolo);
        name = findViewById(R.id.nametitle);
        final RelativeLayout relativeLayout = findViewById(R.id.noresult);
        profileimg = findViewById(R.id.profiledisplay);
        following = findViewById(R.id.numFoly);
        posts = findViewById(R.id.numPosts);
        level = findViewById(R.id.level);
        posts.setText(String.valueOf(user.getPostnum()));
        bio.setText(user.getBio());
        email.setText(user.getEmailAdress());
        followers.setText(String.valueOf(user.getFollower()));
        name.setText(user.getUserName());
        db.collection("Users").document(user.getUserid()).collection("Following")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                            }
                            following.setText(String.valueOf(count));
                        } else {
                            Log.d("DROP CHAT", "Error getting documents: ", task.getException());
                        }
                    }
                });
        if (user.getPostnum() < 100) {
            Glide
                    .with(ProfileDisplayActivity.this)
                    .load(R.drawable.ic_grade)
                    .into(level);
        }
        if (user.getPostnum() > 100 && user.getPostnum() > 500) {
            Glide
                    .with(ProfileDisplayActivity.this)
                    .load(R.drawable.ic_grade1)
                    .into(level);
        }
        if (user.getPostnum() > 500) {
            Glide
                    .with(ProfileDisplayActivity.this)
                    .load(R.drawable.ic_grade2)
                    .into(level);
        }

        if (user.getProfileUrl() != null) {
            Glide
                    .with(ProfileDisplayActivity.this)
                    .load(user.getProfileUrl())
                    .into(profileimg);
        }

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
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                    layout.setVisibility(View.VISIBLE);
                    isShow = false;
                }
            }
        });
        CollectionReference userpost = db.collection("Posts");

        if (user.getUserid().equals(auth.getUid())) {
            editImage.setVisibility(View.VISIBLE);
            settings.setText("SETTINGS");
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileDisplayActivity.this, ProfileSettings.class);
                    startActivity(intent);
                }
            });
            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPictureDialog();
                }
            });
            query = userpost.whereEqualTo("userid", auth.getUid()).orderBy("priority", Query.Direction.DESCENDING);
        } else {
            editImage.setVisibility(View.INVISIBLE);
            query = userpost.whereEqualTo("userid", user.getUserid()).orderBy("priority", Query.Direction.DESCENDING);
            final DocumentReference docref = db.collection("Users").document(auth.getUid()).collection("Following").document(user.getUserid());
            docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            settings.setText("UNFOLLOW");
                            Following = true;
                        } else {
                            Following = false;
                            settings.setText("FOLLOW");
                        }
                    }
                }

            });
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                            db.collection("Users").document(user.getUserid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    User you = documentSnapshot.toObject(User.class);
                                    if (Following) {
                                        db.collection("Users").document(auth.getUid()).collection("Following").document(user.getUserid()).delete();
                                        db.collection("Users").document(user.getUserid()).update("follower", you.getFollower() - 1);
                                        followers.setText(String.valueOf(you.getFollower() - 1));
                                        settings.setText("FOLLOW");
                                        Following = false;

                                    } else {
                                        Fol fol = new Fol();
                                        fol.setExists("");
                                        db.collection("Users").document(auth.getUid()).collection("Following").document(user.getUserid()).set(fol);
                                        db.collection("Users").document(user.getUserid()).update("follower", you.getFollower() + 1);
                                        followers.setText(String.valueOf(you.getFollower() + 1));
                                        settings.setText("UNFOLLOW");
                                        Following = true;
                                    }



                        }
                    });


                }
            });
        }
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();
        adapter = new FireAdapter(options, ProfileDisplayActivity.this);
        recyclerView.setAdapter(adapter);
        relativeLayout.setVisibility(View.VISIBLE);
        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (adapter.getItemCount() == 0) {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (adapter.getItemCount() > 0) {
                    relativeLayout.setVisibility(View.INVISIBLE);
                } else {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }
        };


    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(ProfileDisplayActivity.this);
        pictureDialog.setTitle("Select Action");
        pictureDialog.setIcon(R.drawable.ic_upload);
        String[] pictureDialogItems = {
                "Gallery",
                "Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profileimg.setImageBitmap(imageBitmap);
            filePath = getImageUri(ProfileDisplayActivity.this, imageBitmap);
            Continue();
        }
        if (requestCode == GALLERY && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(ProfileDisplayActivity.this.getContentResolver(), filePath);
                profileimg.setImageBitmap(bitmap);
                Continue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "ProfileImage", null);
        return Uri.parse(path);
    }

    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(ProfileDisplayActivity.this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void Continue() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(ProfileDisplayActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            final StorageReference ref = storageReference.child("images/" + auth.getUid());
            ref.putFile(filePath)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        progressDialog.dismiss();
                                        DocumentReference userinfo = db.collection("Users").document(auth.getUid());
                                        userinfo.update("profileUrl", uri.toString());
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileDisplayActivity.this, "Upload Failed Network Error", Toast.LENGTH_LONG).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileDisplayActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }


    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
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
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(observer);
            adapter.stopListening();
        }

    }
}
