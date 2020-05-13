package com.aputech.dora.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.aputech.dora.Adpater.HomeAdapter;
import com.aputech.dora.Adpater.NearByAdapter;
import com.aputech.dora.Model.Post;
import com.aputech.dora.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NearByPosts extends AppCompatActivity {
    private Toolbar myToolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth= FirebaseAuth.getInstance();
    private CollectionReference notebookRef = db.collection("Users").document(auth.getUid()).collection("Following");
    private NearByAdapter adapter;
    private RelativeLayout relativeLayout;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    ArrayList<String> nearbyposts= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_posts);
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Settings");
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent= getIntent();
        nearbyposts= intent.getStringArrayListExtra("post");
        relativeLayout = findViewById(R.id.noresult);
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(NearByPosts.this));
        relativeLayout.setVisibility(View.VISIBLE);
        notebookRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    nearbyposts.add(documentSnapshot.getId());
                }

                Query query = db.collection("Posts").orderBy("priority", Query.Direction.DESCENDING);
                FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();

                adapter = new NearByAdapter(options,NearByPosts.this,nearbyposts);
                adapterDataObserver = new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        if (adapter.ActualSize()==0){
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        if (adapter.ActualSize() > 0){
                            relativeLayout.setVisibility(View.INVISIBLE);
                        }else{
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                    }
                };
                adapter.registerAdapterDataObserver(adapterDataObserver);
                recyclerView.setAdapter(adapter);
                adapter.startListening();
            }

        });


    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top);
    }
}
