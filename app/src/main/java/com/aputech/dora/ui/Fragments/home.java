package com.aputech.dora.ui.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Adpater.HomeAdapter;
import com.aputech.dora.Model.Post;
import com.aputech.dora.R;
import com.aputech.dora.ui.MapView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class home extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<String> Following = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Users").document(auth.getUid()).collection("Following");
    private HomeAdapter adapter;
    private RelativeLayout relativeLayout;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private MaterialButton map_View;

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
        relativeLayout = root.findViewById(R.id.noresult);

        final RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        map_View = root.findViewById(R.id.map_style);
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

                adapter = new HomeAdapter(options, getActivity(), Following);
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
        if (adapter != null) {
            adapter.stopListening();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }

    }
}