package com.aputech.dora.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.aputech.dora.R;

import com.aputech.dora.ui.Fragments.Profile;
import com.aputech.dora.ui.Fragments.Reminder;
import com.aputech.dora.ui.Fragments.Trending;
import com.aputech.dora.ui.Fragments.home;
import com.bumptech.glide.Glide;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;


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
         Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        navView = findViewById(R.id.nav_view);
        fragmentContainerView = findViewById(R.id.nav_host_fragment);
        BottomNavigationMenuView mbottomNavigationMenuView =
                (BottomNavigationMenuView) navView.getChildAt(0);

        View view = mbottomNavigationMenuView.getChildAt(4);

        BottomNavigationItemView itemView = (BottomNavigationItemView) view;

        View cart_badge = LayoutInflater.from(this)
                .inflate(R.layout.profile_menu_layout,
                        mbottomNavigationMenuView, false);



        ImageView profileImage =  cart_badge.findViewById(R.id.toolbar_profile_image);
        if (auth.getCurrentUser()!=null){
            Glide
                    .with(this)
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .into(profileImage);
        }
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navView.setSelectedItemId(R.id.profile);
            }
        });
        itemView.addView(cart_badge);


        BottomNavigationView.OnNavigationItemSelectedListener listener= new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Fragment newFragment;
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();;
                if (navView.getSelectedItemId() != id) {
                    switch (id) {
                        case R.id.navigation_home:
                            newFragment = new home();
                            transaction.replace(R.id.nav_host_fragment, newFragment);
                            transaction.commit();
                            return true;
                        case R.id.trending:
                            newFragment = new Trending();
                            transaction.replace(R.id.nav_host_fragment, newFragment);
                            transaction.commit();
                            return true;
                        case R.id.profile:
                            newFragment = new Profile();
                            transaction.replace(R.id.nav_host_fragment, newFragment);
                            transaction.commit();
                            return true;
                        case R.id.remind:
                            newFragment = new Reminder();
                            transaction.replace(R.id.nav_host_fragment, newFragment);
                            transaction.commit();
                            return true;
                        default:
                            return false;
                    }
                }

                return false;
            }

        };
        navView.setOnNavigationItemSelectedListener(listener);
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
        navView.setSelectedItemId(R.id.navigation_home);
        Fragment newFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();;
        newFragment = new home();
        transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.commit();
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mail) {
            Intent intent = new Intent(HActivity.this,msgActivity.class);
            startActivity(intent);
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
    }
}
