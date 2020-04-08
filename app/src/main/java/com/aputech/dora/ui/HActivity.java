package com.aputech.dora.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


import com.aputech.dora.R;

import com.aputech.dora.ui.Fragments.Profile;
import com.aputech.dora.ui.Fragments.Reminder;
import com.aputech.dora.ui.Fragments.Trending;
import com.aputech.dora.ui.Fragments.home;
import com.bumptech.glide.Glide;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    FragmentContainerView fragmentContainerView;
    ImageView Home,Trending,Reminder,profileImage;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection(Objects.requireNonNull(auth.getUid()));
    int page=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h);
         Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        fragmentContainerView = findViewById(R.id.nav_host_fragment);
        Home= findViewById(R.id.Home);
        Reminder= findViewById(R.id.Remind);
        Trending= findViewById(R.id.Trending);
        Fragment newFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();;
        newFragment = new home();
        transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.commit();
        page=1;
        Reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page != 3) {
                    page=3;
                    Animation animation = AnimationUtils.loadAnimation(HActivity.this, R.anim.bounce);
                    Reminder.startAnimation(animation);
                    Fragment newFragment;
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    newFragment = new Reminder();
                    transaction.replace(R.id.nav_host_fragment, newFragment);
                    transaction.commit();
                }
            }
        });
        Trending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page != 2) {
                    page=2;
                    Animation animation = AnimationUtils.loadAnimation(HActivity.this, R.anim.bounce);
                    Trending.startAnimation(animation);
                    Fragment newFragment;
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    newFragment = new Trending();
                    transaction.replace(R.id.nav_host_fragment, newFragment);
                    transaction.commit();
                }
            }
        });
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page!=1){
                    page=1;
                    Animation animation=AnimationUtils.loadAnimation(HActivity.this,R.anim.bounce);
                    Home.startAnimation(animation);
                    Fragment newFragment;
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    newFragment = new home();
                    transaction.replace(R.id.nav_host_fragment, newFragment);
                    transaction.commit();
                }

            }
        });

      profileImage =  findViewById(R.id.toolbar_profile_image);
        if (auth.getCurrentUser()!=null){
            Glide
                    .with(this)
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .into(profileImage);
        }
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page!=4) {
                    page=4;
                    Animation animation = AnimationUtils.loadAnimation(HActivity.this, R.anim.bounce);
                    profileImage.startAnimation(animation);
                    Fragment newFragment;
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    newFragment = new Profile();
                    transaction.replace(R.id.nav_host_fragment, newFragment);
                    transaction.commit();
                }
            }
        });

        FloatingActionButton newPost = findViewById(R.id.fab);

        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Animation animation=AnimationUtils.loadAnimation(HActivity.this,R.anim.move);
//                fab.startAnimation(animation);
                Intent intent = new Intent(HActivity.this,Post.class);
                startActivity(intent);
//                AuthUI.getInstance().signOut(HActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            public void onComplete(@NonNull Task<Void> task) {
//                                // user is now signed out
//                                startActivity(new Intent(HActivity.this, Post.class));
//                                finish();
//                            }
//                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
