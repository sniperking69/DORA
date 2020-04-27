package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aputech.dora.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.List;

public class SelectLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private String geocode;
    LatLng latLngfinal;
    MaterialButton SKIP,Forward;
    private TextView resutText;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();
        resutText = (TextView) findViewById(R.id.dragg_result);
        SKIP= findViewById(R.id.SKIP);
        Forward = findViewById(R.id.forward);
        searchView = findViewById(R.id.searchlocation);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mMap.clear();
                String location = searchView.getQuery().toString();
                List<Address> addressList=null;
                if (location !=null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(SelectLocation.this);

                    try {
                        addressList=geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    final LatLng latLng= new LatLng(address.getLatitude(),address.getLongitude());
                    latLngfinal=latLng;
                    mMap.addMarker(new MarkerOptions().position(latLng)).setDraggable(true);
                    moveMap(latLng);
                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker arg0) {
                            // TODO Auto-generated method stub
                            Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        public void onMarkerDragEnd(Marker arg0) {
                            // TODO Auto-generated method stub
                            Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                            Geocodeget(arg0.getPosition());
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                            latLngfinal=arg0.getPosition();
                        }

                        @Override
                        public void onMarkerDrag(Marker arg0) {
                            // TODO Auto-generated method stub
                            Log.i("System out", "onMarkerDrag...");
                        }
                    });


                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        SKIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("LatLng", latLngfinal);
                intent.putExtra("geocode", geocode);
                intent.putExtra("skip", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("LatLng", latLngfinal);
                intent.putExtra("geocode", geocode);
                intent.putExtra("skip", false);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
      //  configureCameraIdle();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //Getting current location
    private void getCurrentLocation() {
        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    LatLng latLng = new LatLng(task.getResult().getLatitude() ,task.getResult().getLongitude() );
                    latLngfinal=latLng;
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
private void Geocodeget( LatLng latLng){
    Geocoder geocoder = new Geocoder(SelectLocation.this);
    latLngfinal=latLng;
    try {
        List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        if (addressList != null && addressList.size() > 0) {
            String locality = addressList.get(0).getAddressLine(0);
            String country = addressList.get(0).getCountryName();
            if (!locality.isEmpty() && !country.isEmpty())
                geocode=locality + "  " + country;
            resutText.setText(geocode);

        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(onCameraIdleListener);
    }
}
