package com.aputech.dora;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.aputech.dora.Model.Post;
import com.aputech.dora.Model.notification;
import com.aputech.dora.ui.HActivity;
import com.aputech.dora.ui.MapView;
import com.aputech.dora.ui.NearByPosts;
import com.aputech.dora.ui.NearByPrivatePosts;
import com.aputech.dora.ui.PostDisplay;
import com.aputech.dora.ui.PrivatePostDisplay;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.aputech.dora.ui.HActivity.CHANNEL_ID;


public class LocationJob extends JobService {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Query query = db.collection("Users").document(auth.getUid()).collection("notify");
    private Query querylocation = db.collection("Posts");
    private static final String TAG = "joblocationservice";
    ListenerRegistration registration;
    ListenerRegistration reg;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    Handler jam;
    private LocationCallback locationCallback;
    private ArrayList<Post> posts=new ArrayList<>();
    private boolean jobCancelled = false;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                getDeviceLocation();
                reg = querylocation.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                Post noti = dc.getDocument().toObject(Post.class);
                                posts.add(noti);
                        }
                    }
                });
                registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                notification noti = dc.getDocument().toObject(notification.class);
                                sendOnChannel1(noti);
                            }
                        }
                    }
                });
            }
        });

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        registration.remove();
        jam.removeCallbacksAndMessages(null);
        jobCancelled = true;
        return true;
    }
    public void sendOnChannel1(notification noti) {
        Intent intent;
        if (noti.getTyp()==1){
            intent = new Intent(this, PrivatePostDisplay.class);
            intent.putExtra("post",noti.getDocument());
            intent.putExtra("typ",1);
        }if (noti.getTyp()==0){
            intent = new Intent(this, PostDisplay.class);
            intent.putExtra("post",noti.getDocument());
            intent.putExtra("typ",0);
        }else{
            intent = new Intent(this, MapView.class);
            intent.putExtra("post",noti.getDocument());
            intent.putExtra("typ",2);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle("Drop Chat")
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentText(noti.getText())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                Log.d(TAG, "in if: "+mLastKnownLocation);
                                for (Post post:posts){
                                    PostCalculate(post.getLocation().getLatitude() ,post.getLocation().getLongitude());
                                }
                                 jam = new Handler();
                                        jam.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getDeviceLocation();
                                    }
                                },420000);

                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(1000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        }
                    }
                });
    }
    private boolean closekm(Location A, Location B){
        float dis = A.distanceTo(B) / 1000;
        return dis < 1;

    }
    private void PostCalculate(double latitude ,double longitude){
        ArrayList<String> PostNearby=new ArrayList<>();
        Location postlocation = new Location("");
        postlocation.setLatitude(latitude);
        postlocation.setLongitude(longitude);
        if (closekm(postlocation,mLastKnownLocation)) {
            int x=0;
            while (x<posts.size()){
                Post pst= posts.get(x);
                if (pst.getLocation()!=null){
                    Location postA = new Location("");
                    postA.setLatitude(pst.getLocation().getLatitude());
                    postA.setLongitude(pst.getLocation().getLongitude());
                    Location postB = new Location("");
                    postB.setLatitude(pst.getLocation().getLatitude());
                    postB.setLongitude(pst.getLocation().getLongitude());
                    if (closekm(postA,postB)){
                        PostNearby.add(pst.getRefComments());
                    }
                }
                x+=1;
            }
            if (PostNearby.size()>=1){
                String commentref=PostNearby.get(0);
                for (Post post:posts){

                    for (String pst:PostNearby){
                        if (post.getRefComments().equals(pst)){
                            posts.remove(post);
                        }
                    }
                }
                notification noti = new notification();
                noti.setDocument(commentref);
                noti.setTyp(1);
                noti.setUserid("nearby");
                noti.setText("You Have Post Near By");
                CollectionReference  notiref= db.collection("Users").document(auth.getUid()).collection("notify");
                notiref.add(noti);

            }
        }
    }
    private void MessageCalculated(){


    }

}
