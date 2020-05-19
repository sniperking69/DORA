package com.aputech.dora.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aputech.dora.Model.Post;
import com.aputech.dora.R;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class MapView extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private final float DEFAULT_ZOOM = 15;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String notipost;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Posts");
    private ArrayList<Post> posts = new ArrayList<>();
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private View mapView;
    private int typ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        Intent intent = getIntent();
        notipost = intent.getStringExtra("post");
        typ = intent.getIntExtra("typ", 0);
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
                Log.i("Bigpp", "An error occurred: " + status);
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapView.this);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (typ == 2) {
                    db.collection("Posts").document(notipost).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Post m = documentSnapshot.toObject(Post.class);
                            if (m.getLocation()!=null){
                                LatLng LL = new LatLng(m.getLocation().getLatitude(), m.getLocation().getLongitude());
                                moveMap(LL);
                            }

                        }
                    });
                }
                notebookRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Post p = documentSnapshot.toObject(Post.class);
                            posts.add(p);
                        }
                        for (int x = 0; x < posts.size(); x++) {
                            Post msg = posts.get(x);
                            if (msg.getLocation() != null) {
                                if (auth.getUid().equals(msg.getUserid())) {
                                    LatLng customMarkerLocationOne = new LatLng(msg.getLocation().getLatitude(), msg.getLocation().getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(customMarkerLocationOne).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                } else {
                                    final LatLng customMarkerLocationOne = new LatLng(msg.getLocation().getLatitude(), msg.getLocation().getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(customMarkerLocationOne));
                                }

                            }
                        }
                    }
                });

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

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapView.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapView.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(MapView.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapView.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
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
                            Toast.makeText(MapView.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        ArrayList<String> PostNearby = new ArrayList<>();
        Location postlocation = new Location("");
        postlocation.setLatitude(marker.getPosition().latitude);
        postlocation.setLongitude(marker.getPosition().longitude);
        if (closekm(postlocation, mLastKnownLocation)) {
            int x = 0;
            while (x < posts.size()) {
                Post post = posts.get(x);
                if (post.getLocation() != null) {
                    if (post.getLocation().getLongitude() == marker.getPosition().longitude &&
                            post.getLocation().getLatitude() == marker.getPosition().latitude) {
                        PostNearby.add(post.getRefComments());
                        x += 1;
                        while (x < posts.size()) {
                            Post pst = posts.get(x);
                            if (pst.getLocation() != null) {
                                Location postA = new Location("");
                                postA.setLatitude(pst.getLocation().getLatitude());
                                postA.setLongitude(pst.getLocation().getLongitude());
                                Location postB = new Location("");
                                postB.setLatitude(post.getLocation().getLatitude());
                                postB.setLongitude(post.getLocation().getLongitude());
                                if (closekm(postA, postB)) {
                                    PostNearby.add(pst.getRefComments());
                                }
                            }
                            x += 1;
                        }
                        break;
                    }
                }
                x += 1;
            }
            if (PostNearby.size() == 1) {
                Intent intent = new Intent(MapView.this, PostDisplay.class);
                intent.putExtra("post", PostNearby.get(0));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                PostNearby.add(PostNearby.get(0));
            } else {
                Intent intent = new Intent(MapView.this, NearByPosts.class);
                intent.putExtra("post", PostNearby);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            }
        } else {
            for (Post post : posts) {
                if (post.getLocation().getLongitude() == marker.getPosition().longitude && post.getLocation().getLatitude() == marker.getPosition().latitude) {
                    if (auth.getUid().equals(post.getUserid())) {
                        PostNearby.add(post.getRefComments());
                    }
                }
            }
            if (PostNearby.size() == 0) {
                Toast.makeText(MapView.this, "Not Near the Post", Toast.LENGTH_SHORT).show();
            } else {
                if (PostNearby.size() == 1) {
                    Intent intent = new Intent(MapView.this, PostDisplay.class);
                    intent.putExtra("post", PostNearby.get(0));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    PostNearby.add(PostNearby.get(0));
                } else {
                    Intent intent = new Intent(MapView.this, NearByPosts.class);
                    intent.putExtra("post", PostNearby);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            }

        }

        return false;
    }

    private boolean closekm(Location A, Location B) {
        float dis = A.distanceTo(B) / 1000;
        return dis < 2;

    }

    private void moveMap(LatLng latLng) {
        float zoom = 15;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private String PostCalculate(double latitude, double longitude) {
        Location postlocation = new Location("");
        postlocation.setLatitude(latitude);
        postlocation.setLongitude(longitude);
        if (closekm(postlocation, mLastKnownLocation)) {
            int x = 0;
            while (x < posts.size()) {
                Post pst = posts.get(x);
                if (pst.getLocation() != null) {
                    Location postA = new Location("");
                    postA.setLatitude(pst.getLocation().getLatitude());
                    postA.setLongitude(pst.getLocation().getLongitude());
                    Location postB = new Location("");
                    postB.setLatitude(pst.getLocation().getLatitude());
                    postB.setLongitude(pst.getLocation().getLongitude());
                    if (closekm(postA, postB)) {
                        return pst.getRefComments();
                        // PostNearby.add(pst.getRefComments());
                    }
                }
                x += 1;
            }
        }
        return "0";


    }
}