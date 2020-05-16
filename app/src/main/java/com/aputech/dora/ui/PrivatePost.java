package com.aputech.dora.ui;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.aputech.dora.Model.Post;
import com.aputech.dora.Model.User;
import com.aputech.dora.Model.message;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivatePost extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<message> listInbox=new ArrayList<>();
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private View mapView;
    private final float DEFAULT_ZOOM = 15;
    private String notimsg;
    private int typ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_post);
        Intent intent=getIntent();
        notimsg=intent.getStringExtra("post");
        typ=intent.getIntExtra("typ",0);
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_api_key));
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setHint("Search Location");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                moveMap(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
              //Show Error
            }
        });
        FloatingActionButton floatingActionButton = findViewById(R.id.button_add_msg);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivatePost.this,MakePP.class);
                startActivity(intent);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PrivatePost.this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMarkerClickListener(this);
        loadData();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (typ==1){
                    db.collection("Inbox").document(notimsg).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            message m=documentSnapshot.toObject(message.class);
                            LatLng LL= new LatLng(m.getLocation().getLatitude(),m.getLocation().getLongitude());
                            moveMap(LL);
                        }
                    });

                }
            }
        });
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(10, 10, 10, 150);
        }

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(PrivatePost.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(PrivatePost.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(PrivatePost.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(PrivatePost.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

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
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(PrivatePost.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

private void loadData(){
    db.collection("Inbox").whereEqualTo("receiver",auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    message msg=document.toObject(message.class);
                    listInbox.add(msg);
                    final LatLng customMarkerLocationOne = new LatLng(msg.getLocation().getLatitude(), msg.getLocation().getLongitude());
                    mMap.addMarker(new MarkerOptions().position(customMarkerLocationOne));
                }
            } else {
                Toast.makeText(PrivatePost.this,"Error",Toast.LENGTH_SHORT).show();
            }
            db.collection("Inbox").whereEqualTo("sender",auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            message msg=document.toObject(message.class);
                            listInbox.add(msg);
                            LatLng customMarkerLocationOne = new LatLng(msg.getLocation().getLatitude(), msg.getLocation().getLongitude());
                            mMap.addMarker(new MarkerOptions().position(customMarkerLocationOne).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        }
                    } else {
                        Toast.makeText(PrivatePost.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    });


}

    @Override
    public boolean onMarkerClick(Marker marker) {
        ArrayList<String> PostNearby=new ArrayList<>();
        Location postlocation = new Location("");
        postlocation.setLatitude(marker.getPosition().latitude);
        postlocation.setLongitude(marker.getPosition().longitude);
        if (closekm(postlocation,mLastKnownLocation)) {
            int x=0;
            while (x<listInbox.size()){
                message msg = listInbox.get(x);
                if (msg.getLocation()!=null){
                    if (msg.getLocation().getLongitude()==marker.getPosition().longitude &&
                            msg.getLocation().getLatitude() ==marker.getPosition().latitude){
                        PostNearby.add(msg.getRefmsg());
                        x+=1;
                        while(x<listInbox.size()){
                            message pst= listInbox.get(x);
                            if (pst.getLocation()!=null){
                                Location postA = new Location("");
                                postA.setLatitude(pst.getLocation().getLatitude());
                                postA.setLongitude(pst.getLocation().getLongitude());
                                Location postB = new Location("");
                                postB.setLatitude(msg.getLocation().getLatitude());
                                postB.setLongitude(msg.getLocation().getLongitude());
                                if (closekm(postA,postB)){
                                    PostNearby.add(pst.getRefmsg());
                                }
                            }
                            x+=1;
                        }
                        break;
                    }
                }
                x+=1;
            }
            if (PostNearby.size()==1){
                Intent intent = new Intent(PrivatePost.this, PrivatePostDisplay.class);
                intent.putExtra("post", PostNearby.get(0));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                PostNearby.add(PostNearby.get(0));
            }else {
                Intent intent = new Intent(PrivatePost.this, NearByPrivatePosts.class);
                intent.putExtra("post", PostNearby);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            }
        } else {
            for (message post : listInbox) {
                if (post.getLocation().getLongitude() == marker.getPosition().longitude && post.getLocation().getLatitude() == marker.getPosition().latitude) {
                    if (auth.getUid().equals(post.getSender())) {
                        PostNearby.add(post.getRefmsg());
                    }
                }
            }
            if (PostNearby.size()==0) {
                View contextView = findViewById(R.id.main);
                Snackbar.make(contextView, R.string.not_at_location, Snackbar.LENGTH_SHORT).show();
            }else{
                if (PostNearby.size()==1){
                    Intent intent = new Intent(PrivatePost.this, PrivatePostDisplay.class);
                    intent.putExtra("post", PostNearby.get(0));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    PostNearby.add(PostNearby.get(0));
                }else{
                    Intent intent = new Intent(PrivatePost.this, NearByPrivatePosts.class);
                    intent.putExtra("post", PostNearby);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            }

        }

        return false;
    }
    private boolean closekm(Location A, Location B){
        float dis = A.distanceTo(B) / 1000;
        return dis < 1;

    }

    private void moveMap(LatLng latLng) {
        float zoom = 15;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

}
