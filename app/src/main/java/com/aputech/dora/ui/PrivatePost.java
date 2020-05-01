package com.aputech.dora.ui;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;


import com.aputech.dora.Model.User;
import com.aputech.dora.Model.message;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.EventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivatePost extends FragmentActivity implements OnMapReadyCallback {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<message> listsent=new ArrayList<>();
    private ArrayList<message> listreceived=new ArrayList<message>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_post);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        FloatingActionButton floatingActionButton = findViewById(R.id.button_add_msg);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivatePost.this,MakePP.class);
                //intent.putExtra("activity",2);
                startActivity(intent);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(mMap.getMinZoomLevel());
        mMap.setMaxZoomPreference(mMap.getMaxZoomLevel());
        getCurrentLocation();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                loadData();
            }
        });
    }
    //Getting current location
    private void getCurrentLocation() {
        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    LatLng latLng = new LatLng(task.getResult().getLatitude() ,task.getResult().getLongitude() );
                    moveMap(latLng);
                }
            });

        }catch (SecurityException ex){
            Log.e("LocationBook", "Lost location permission. Could not request it"+ex);
        }
    }

    private void moveMap( LatLng latLng) {
        float zoom = 14.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String url) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        Glide
                .with(context)
                .load(url)
                .into(markerImage);
//        TextView txt_name = (TextView)marker.findViewById(R.id.name);
//        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }
private void loadData(){
    Query query =db.collection("inbox").whereEqualTo("sentTo",auth.getUid());
    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    listreceived.add(document.toObject(message.class));
                    // Log.d("bigpp", document.getId() + " => " + document.getData());
                }
            } else {
                // Log.d("bigpp", "Error getting documents: ", task.getException());
            }
            if (!listreceived.isEmpty()){
                for (int i=0;i<listreceived.size();i++){
                    message msg = listreceived.get(i);
//
//                                final LatLng customMarkerLocationOne = new LatLng(Double.parseDouble(msg.getLat()), Double.parseDouble(msg.getLng()));
//                                DocumentReference userinfo= db.collection("Users").document(msg.getUserid());
//                                userinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                        User user= documentSnapshot.toObject(User.class);
//                                        mMap.addMarker(new MarkerOptions().position(customMarkerLocationOne).
//                                                icon(BitmapDescriptorFactory.fromBitmap(
//                                                        createCustomMarker(PrivatePost.this,R.drawable.ic_logo,user.getProfileUrl())))).setTitle("You Are not at this location");
//                                    }
//                                });

                    //LatLng sydney = new LatLng(-34, 151);
                    //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                }
            }
        }
    });
    Query query2 =db.collection("inbox").whereEqualTo("sentBy",auth.getUid());
    query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    listsent.add(document.toObject(message.class));
                    // Log.d("bigpp", document.getId() + " => " + document.getData());
                }
            } else {
                //    Log.d("bigpp", "Error getting documents: ", task.getException());
            }
            if (!listsent.isEmpty()){
                for (int i=0;i<listsent.size();i++){
                    message msg = listsent.get(i);
                    final LatLng customMarkerLocationOne = new LatLng(msg.getLocation().getLatitude(),msg.getLocation().getLongitude());
                    mMap.addMarker(new MarkerOptions().position(customMarkerLocationOne).title("Click to Edit Message"));
                }
            }
        }
    });

}
    @Override
    protected void onResume() {
        super.onResume();
        if(mMap != null){ //prevent crashing if the map doesn't exist yet (eg. on starting activity)
            mMap.clear();
            loadData();
            // add markers from database to the map
        }
    }
}
