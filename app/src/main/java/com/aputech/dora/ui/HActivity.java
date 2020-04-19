package com.aputech.dora.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.aputech.dora.Adpater.HomeAdapter;
import com.aputech.dora.Adpater.SearchAdapter;
import com.aputech.dora.Model.Note;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;

import com.aputech.dora.ui.Fragments.Profile;
import com.aputech.dora.ui.Fragments.Notify;
import com.aputech.dora.ui.Fragments.Trending;
import com.aputech.dora.ui.Fragments.home;
import com.bumptech.glide.Glide;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class HActivity extends AppCompatActivity {

    FragmentContainerView fragmentContainerView;
    ImageView Home,Trending,Reminder,profileImage;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Toolbar myToolbar;
    RelativeLayout searchlayout;
    SearchView searchView;
    FloatingActionButton newPost;
    LinearLayout bottomlinear;
    RecyclerView recyclerView;
    boolean adapterlisten=false;
    private SearchAdapter adapter;
    private CollectionReference collectionReference= db.collection("Users");
    int page=2;
    boolean search_bool=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h);
          myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        bottomlinear= findViewById(R.id.bottomlinear);
        fragmentContainerView = findViewById(R.id.nav_host_fragment);
        Home= findViewById(R.id.Home);
        searchView=findViewById(R.id.searchArea);
        searchView.setSubmitButtonEnabled(true);
        final RecyclerView recyclerView = findViewById(R.id.search_rec);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ImageView searchSubmit = (ImageView) searchView.findViewById (androidx.appcompat.R.id.search_go_btn);
        searchSubmit.setColorFilter (Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Query userquery= collectionReference.whereEqualTo("userName",query);
                FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(userquery, User.class)
                        .build();
                adapter = new SearchAdapter(options,HActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.startListening();


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Reminder= findViewById(R.id.Remind);
        searchlayout= findViewById(R.id.search_list);
        Trending= findViewById(R.id.Trending);
        Fragment newFragment;
        ImageView backsearch = findViewById(R.id.backsearch);
        backsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFAB();
                search_bool=false;
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();;
        newFragment = new Trending();
        transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.commit();
        page=2;
        Reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page != 3) {
                    page=3;
                    Animation animation = AnimationUtils.loadAnimation(HActivity.this, R.anim.bounce);
                    Reminder.startAnimation(animation);
                    Fragment newFragment;
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    newFragment = new Notify();
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

         newPost = findViewById(R.id.fab);

        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HActivity.this,Post.class);
                startActivity(intent);
//                AuthUI.getInstance().signOut(HActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            public void onComplete(@NonNull Task<Void> task) {
//                                // user is now signed out
//                               // startActivity(new Intent(HActivity.this, Post.class));
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
        switch (item.getItemId()) {
            case R.id.search:
                revealFAB();
                search_bool=true;
                return true;
            case R.id.mail:
                Intent intent = new Intent(HActivity.this,msgActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void revealFAB() {
        final View view = findViewById(R.id.search_view);
        myToolbar.setVisibility(View.INVISIBLE);

        int cx = view.getWidth() -200;
        int cy = view.getHeight()/2 ;

        float finalRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);
                searchlayout.setVisibility(View.VISIBLE);
                myToolbar.setVisibility(View.INVISIBLE);
                fragmentContainerView.setVisibility(View.INVISIBLE);
                newPost.setVisibility(View.INVISIBLE);
                bottomlinear.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();

    }
    private void hideFAB() {
        final View view = findViewById(R.id.search_view);
            adapter.stopListening();

        int cx = view.getWidth()-200;
        int cy = view.getHeight()/2 ;

        float initialRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                view.setVisibility(View.INVISIBLE);
                searchlayout.setVisibility(View.INVISIBLE);
                myToolbar.setVisibility(View.VISIBLE);
                fragmentContainerView.setVisibility(View.VISIBLE);
                newPost.setVisibility(View.VISIBLE);
                bottomlinear.setVisibility(View.VISIBLE);

            }
        });

        anim.start();
    }
    @Override
    public void onBackPressed() {

        if (search_bool){
            hideFAB();
            search_bool=false;
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
