package com.aputech.dora.ui;

import android.os.Bundle;
import android.view.Menu;

import android.view.View;
import android.widget.ImageView;

import com.aputech.dora.R;

import com.bumptech.glide.Glide;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;


import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HActivity extends AppCompatActivity {
    public BottomNavigationView navView;
    FragmentContainerView fragmentContainerView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection(Objects.requireNonNull(auth.getUid()));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h);
        navView = findViewById(R.id.nav_view);
        fragmentContainerView = findViewById(R.id.nav_host_fragment);

//        BottomNavigationView.OnNavigationItemSelectedListener listener= new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                if (item.getItemId() == R.id.post) {
//                    Intent intent = new Intent(HActivity.this, uploadActivity.class);
//                    startActivity(intent);
//                    return true;
//                }
//                return false;
//            }
//        };
    //    navView.setOnNavigationItemSelectedListener(listener);
       View view= navView.getMenu().findItem(R.id.profile).getActionView();
       ImageView profileImage = view.findViewById(R.id.toolbar_profile_image);
        if (auth.getCurrentUser()!=null){
            Glide
                    .with(this)
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .into(profileImage);
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//      NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
//        FloatingActionButton fab = findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AuthUI.getInstance().signOut(HActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            public void onComplete(@NonNull Task<Void> task) {
//                                // user is now signed out
//                                startActivity(new Intent(HActivity.this, MainActivity.class));
//                                finish();
//                            }
//                        });
//            }
//        });
    }

    @Override
    protected void onStart() {
        navView.setSelectedItemId(R.id.trending);
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
