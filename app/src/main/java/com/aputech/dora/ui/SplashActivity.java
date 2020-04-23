package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.aputech.dora.LocationJob;
import com.aputech.dora.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 459;
    FirebaseAuth auth;
    public static final String CHANNEL_1_ID = "channel1";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm aa");
        Log.d("DropChat", "onCreate: "+Calendar.getInstance().getFirstDayOfWeek() +" fksofissi "+ Calendar.getInstance().getTimeInMillis());
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null){
            checkifalreadyuser();
        }else{
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

    private void locationCheck(){
        Dexter.withActivity(this)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA
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

    private void UpdateUI(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HActivity.class));
                finish();
            }
        },3000);
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
    private void checkifalreadyuser(){
        DocumentReference docIdRef = db.collection("Users").document(auth.getCurrentUser().getUid());
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        locationCheck();
                    } else {
                        Intent intent = new Intent(SplashActivity.this,regUser.class);
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
        int cx = view.getWidth() /2;
        int cy = view.getHeight()/2 ;

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
}





