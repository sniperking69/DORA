package com.aputech.dora.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aputech.dora.Model.Post;
import com.aputech.dora.Model.message;
import com.aputech.dora.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 459;
    FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private long current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            checkifalreadyuser();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.RegTheme)
                            .setLogo(R.drawable.ic_launcher_round)
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build()))
                            .build(),
                    MY_REQUEST_CODE);

        }


    }

    public void locationCheck() {
        Dexter.withContext(this)
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
                        UpdateUI();
                        revealFAB();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void UpdateUI() {
        CheckPostTimer();
        CheckPrivateTimer();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HActivity.class));
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                checkifalreadyuser();
            } else {
                if (response != null) {
                    Toast.makeText(this, "" + Objects.requireNonNull(response.getError()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void checkifalreadyuser() {
        DocumentReference docIdRef = db.collection("Users").document(auth.getCurrentUser().getUid());
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        locationCheck();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, regUser.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.d("DropChat", "Failed with: ", task.getException());
                }
            }
        });
    }

    private void revealFAB() {
        final View view = findViewById(R.id.imageView);
        view.setVisibility(View.INVISIBLE);
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

        float finalRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.setDuration(3000);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);

            }
        });
        anim.start();

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

    private void CheckPrivateTimer() {
        db.collection("Inbox").whereEqualTo("receiver", auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                current = System.currentTimeMillis();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    message post = documentSnapshot.toObject(message.class);
                    long posttime = post.getTimestamp().getTime();
                    long ftime = current - posttime;
                    if (ftime > 86400000) {
                        DeletePrivatePost(post.getRefmsg(), post.getType(), post.getAudioUrl(), post.getVideoUrl(), post.getImageUrl());
                    }
                }

            }
        });
        db.collection("Inbox").whereEqualTo("sender", auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                current = System.currentTimeMillis();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    message post = documentSnapshot.toObject(message.class);
                    long posttime = post.getTimestamp().getTime();
                    long ftime = current - posttime;
                    if (ftime > 86400000) {
                        DeletePrivatePost(post.getRefmsg(), post.getType(), post.getAudioUrl(), post.getVideoUrl(), post.getImageUrl());
                    }
                }

            }
        });
    }

    private void DeletePrivatePost(final String Postid, int type, String Audio, String Video, String Image) {
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
        db.collection("Inbox").document(Postid).delete();
    }

    private void CheckPostTimer() {
        db.collection("Posts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                current = System.currentTimeMillis();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Post post = documentSnapshot.toObject(Post.class);
                    long posttime = post.getTimestamp().getTime();
                    long ftime = current - posttime;
                    if (ftime > 86400000) {
                        DeletePost(post.getRefComments(), post.getType(), post.getAudioUrl(), post.getVideoUrl(), post.getImageUrl());
                    }
                }

            }
        });
    }
}





