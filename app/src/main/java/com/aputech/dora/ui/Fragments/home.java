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
    private FireAdapter adapter;
    private  RelativeLayout relativeLayout;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private MaterialButton map_View;
    //HomeAdapter adapter;
    ObservableSnapshotArray followposts;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FirestorePagingAdapter<Post, PostViewHolder> mAdapter;
    private CollectionReference mPostsCollection = db.collection("Posts");

    private EventListener eventListener;
    ListenerRegistration listenerRegistration;
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
        mRecyclerView = root.findViewById(R.id.recycler_view);

        mSwipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);

        // Init mRecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();


        // Refresh Action on Swipe Refresh Layout
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refresh();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
    private void setupAdapter() {

        // Init Paging Configuration
        final PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(10)
                .build();
        eventListener =new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    //Post usr = documentSnapshot.toObject(Post.class);
                    Following.add(documentSnapshot.getId());
                }
                Query mQuery = mPostsCollection.orderBy("priority", Query.Direction.DESCENDING);
                FirestorePagingOptions options = new FirestorePagingOptions.Builder<Post>()
                        .setLifecycleOwner(getActivity())
                        .setQuery(mQuery, config, Post.class
//                                new SnapshotParser<Post>() {
//                            @NonNull
//                            @Override
//                            public Post parseSnapshot(@NonNull DocumentSnapshot snapshot) {
//                                Post p = snapshot.toObject(Post.class);
//                                if (p != null) {
//                                    if (Following.contains(p.getUserid())) {
//                                        return p;
//                                    } else {
//                                        return null;
//                                    }
//                                }else{
//                                    return null;
//                                }
//
//                            }
//                        }
                        ).build();

                // Instantiate Paging Adapter
                mAdapter = new FirestorePagingAdapter<Post, PostViewHolder>(options) {
                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = getLayoutInflater().inflate(R.layout.row_feed, parent, false);
                        return new PostViewHolder(view);
                    }

                    @Override
                    public int getItemCount() {
                        return super.getItemCount();
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull PostViewHolder holder, int i, @NonNull Post post) {
                        // Bind to ViewHolder
                       // Log.d("bigpp", "onBindViewHolder: "+Following+"   "+post.getUserid());
                        if (!Following.contains(post.getUserid())){
                            holder.cardView.setVisibility(View.GONE);
                        }

                      //  viewHolder.itemView.setVisibility(View.VISIBLE);
                    }

                    @Nullable
                    @Override
                    protected DocumentSnapshot getItem(int position) {
                        return super.getItem(position);
                    }

                    @Override
                    protected void onError(@NonNull Exception e) {
                        super.onError(e);
                        Log.e("MainActivity", e.getMessage());
                    }

                    @Override
                    protected void onLoadingStateChanged(@NonNull LoadingState state) {
                        switch (state) {
                            case LOADING_INITIAL:


//                                    mAdapter.getCurrentList().remove()
                            case LOADING_MORE:
                                mSwipeRefreshLayout.setRefreshing(true);
                                break;

                            case LOADED:
                                mSwipeRefreshLayout.setRefreshing(false);


                                break;

                            case ERROR:
                                Toast.makeText(
                                        getActivity(),
                                        "Error Occurred!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                mSwipeRefreshLayout.setRefreshing(false);
                                break;

                            case FINISHED:
                                mSwipeRefreshLayout.setRefreshing(false);
                                break;
                        }
                    }

                };

                // Finally Set the Adapter to mRecyclerView
                mRecyclerView.setAdapter(mAdapter);


            }
        };
        listenerRegistration= notebookRef.addSnapshotListener(eventListener);
        // Init Adapter Configuration


    }
   public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView user_name;
        TextView textViewDescription;
        TextView time;
        MaterialButton up, down;
        ImageView img;
        ImageView level;
        CardView cardView;
        ImageView LocationIcon, delete, edit;
        CircleImageView profile;
        MaterialButton Commentbutton;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.card);
            up = itemView.findViewById(R.id.upbutton);
            down = itemView.findViewById(R.id.downbutton);
            edit = itemView.findViewById(R.id.edit);
            // playerView = itemView.findViewById(R.id.video_view);
            delete = itemView.findViewById(R.id.delete);
            user_name = itemView.findViewById(R.id.user_name);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            time = itemView.findViewById(R.id.time);
            level = itemView.findViewById(R.id.level);
            LocationIcon = itemView.findViewById(R.id.locate);
            profile = itemView.findViewById(R.id.poster_profile);
            img = itemView.findViewById(R.id.img);
            Commentbutton = itemView.findViewById(R.id.comment);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (listenerRegistration!=null){
            listenerRegistration.remove();
        }

    }
}