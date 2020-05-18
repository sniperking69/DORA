package com.aputech.dora.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Adpater.NearByAdapter;
import com.aputech.dora.Model.Post;
import com.aputech.dora.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class NearByPosts extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<String> nearbyposts = new ArrayList<>();
    private Toolbar myToolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NearByAdapter adapter;
    private RelativeLayout relativeLayout;
    private RecyclerView.AdapterDataObserver adapterDataObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_posts);
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("NearBy Posts");
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        nearbyposts = intent.getStringArrayListExtra("post");
        relativeLayout = findViewById(R.id.noresult);
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(NearByPosts.this));
        relativeLayout.setVisibility(View.VISIBLE);
        Query query = db.collection("Posts").orderBy("priority", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        adapter = new NearByAdapter(options, NearByPosts.this, nearbyposts);
        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (adapter.ActualSize() == 0) {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (adapter.ActualSize() > 0) {
                    relativeLayout.setVisibility(View.INVISIBLE);
                } else {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }
        };
        adapter.registerAdapterDataObserver(adapterDataObserver);
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_in_top);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_in_top);
    }
}
