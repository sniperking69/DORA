package com.aputech.dora.ui.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.aputech.dora.Adpater.FireAdapter;
import com.aputech.dora.Model.Note;
import com.aputech.dora.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Trending extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");
    private FireAdapter adapter;
    private  RelativeLayout relativeLayout;
    private RecyclerView.AdapterDataObserver adapterDataObserver;

    public Trending() {
        // Required empty public constructor
    }

    private static Trending newInstance(String param1, String param2) {
        Trending fragment = new Trending();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trending, container, false);
        relativeLayout =root.findViewById(R.id.noresult);
        Query query = notebookRef.orderBy("priority", Query.Direction.DESCENDING);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        adapter = new FireAdapter(options,getActivity());
        recyclerView.setAdapter(adapter);
        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                if (itemCount > 0) {
                    relativeLayout.setVisibility(View.GONE);
                } else {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (itemCount == 1) {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }
        };

        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.registerAdapterDataObserver(adapterDataObserver);

    }

    @Override
    public void onStop() {
        super.onStop();
           adapter.stopListening();
          adapter.unregisterAdapterDataObserver(adapterDataObserver);
    }
}
