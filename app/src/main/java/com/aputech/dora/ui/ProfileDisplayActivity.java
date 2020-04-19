package com.aputech.dora.ui;

import android.content.Intent;
import android.os.Bundle;

import com.aputech.dora.Adpater.HomeAdapter;
import com.aputech.dora.Model.Note;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDisplayActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userinfo ;
    private CollectionReference notebookRef = db.collection("Notebook");
    private HomeAdapter adapter;
    private User user;
  private Boolean follower;
    private MaterialButton settings;
    private CircleImageView profileimg;
    private TextView following,posts,followers,name;
    private ImageView level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent= getIntent();
        final String Userid= intent.getStringExtra("user_id");
        userinfo = db.collection("Users").document(Userid);
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
       settings= findViewById(R.id.followandset);

        final RelativeLayout layout= findViewById(R.id.topdisp);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        followers= findViewById(R.id.numFolo);
        following =findViewById(R.id.numFoly);
        posts =findViewById(R.id.numPosts);
        level=findViewById(R.id.level);

        name= findViewById(R.id.nametitle);
        profileimg= findViewById(R.id.profiledisplay);
        userinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                Query profilequery= notebookRef.whereIn(FieldPath.documentId(),user.getPosts()).orderBy("priority", Query.Direction.DESCENDING);

                FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(profilequery, Note.class)
                        .build();
                adapter = new HomeAdapter(options,getApplicationContext());

                recyclerView.setAdapter(adapter);
                adapter.startListening();
                posts.setText(String.valueOf(user.getPosts().size()));
                if (user.getFollowing()!=null && user.getFollowers()!=null){
                    following.setText(String.valueOf(user.getFollowing().size()));
                    followers.setText(String.valueOf(user.getFollowers().size()));
                }else{
                    following.setText(String.valueOf(0));
                    followers.setText(String.valueOf(0));
                }

                if (user.getUserlevel()==0){
                    Glide
                            .with(getApplicationContext())
                            .load(R.drawable.ic_grade)
                            .into(level);
                }
                if (user.getUserlevel()==1){
                    Glide
                            .with(getApplicationContext())
                            .load(R.drawable.ic_grade1)
                            .into(level);
                }
                if (user.getUserlevel()==2){
                    Glide
                            .with(getApplicationContext())
                            .load(R.drawable.ic_grade2)
                            .into(level);
                }
                appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    boolean isShow = true;
                    int scrollRange = -1;

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (scrollRange == -1) {
                            scrollRange = appBarLayout.getTotalScrollRange();
                        }
                        if (scrollRange + verticalOffset == 0) {
                            collapsingToolbarLayout.setTitle(user.getUserName());
                            layout.setVisibility(View.INVISIBLE);
                            isShow = true;
                        } else if(isShow) {
                            collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                            layout.setVisibility(View.VISIBLE);
                            isShow = false;
                        }
                    }
                });
                name.setText(user.getUserName());
                Glide
                        .with(getApplicationContext())
                        .load(user.getProfileUrl())
                        .into(profileimg);
                if (user.getFollowers() !=null){
                    follower=user.getFollowers().contains(auth.getUid());
                    if(follower){
                        settings.setText("unFollow");
                    }else{
                        settings.setText("follow");
                    }
                    settings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                }else{
                    settings.setText("follow");
                    settings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            settings.setText("unfollow");
                        }
                    });
                }

            }


        });

    }
    private void followbutton(){

        //follow function
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
