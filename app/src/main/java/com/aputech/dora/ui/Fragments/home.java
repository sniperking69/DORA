package com.aputech.dora.ui.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aputech.dora.Adpater.FireAdapter;
import com.aputech.dora.Adpater.HomeAdapter;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.aputech.dora.Model.Post;
import com.aputech.dora.ui.MapView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class home extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth= FirebaseAuth.getInstance();
    private CollectionReference notebookRef = db.collection("Users").document(auth.getUid()).collection("Following");
    ArrayList<String> Following= new ArrayList<>();
    private HomeAdapter adapter;
    private  RelativeLayout relativeLayout;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private MaterialButton map_View;
    //HomeAdapter adapter;
    ObservableSnapshotArray followposts;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CollectionReference mPostsCollection = db.collection("Posts");
    private EventListener<QuerySnapshot> eventListener;

    public static home newInstance(int index) {
        home fragment = new home();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_h, container, false);
        relativeLayout =root.findViewById(R.id.noresult);

        final RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        map_View= root.findViewById(R.id.map_style);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        map_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapView.class);
                startActivity(intent);
            }
        });

        relativeLayout.setVisibility(View.VISIBLE);

        notebookRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Following.add(documentSnapshot.getId());
                }

                Query query = db.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
                FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();

                adapter = new HomeAdapter(options,getActivity(),Following);
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

        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter!=null){
            adapter.stopListening();
        }

    }
}