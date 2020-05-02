package com.aputech.dora.ui.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.dora.Adpater.FireAdapter;
import com.aputech.dora.Model.Comment;
import com.aputech.dora.Model.Fol;
import com.aputech.dora.R;
import com.aputech.dora.Model.Note;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Posts");
    ArrayList<String> Following= new ArrayList<>();
    FirebaseAuth auth= FirebaseAuth.getInstance();
    //HomeAdapter adapter;
    ObservableSnapshotArray followposts;

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
        final RelativeLayout relativeLayout= root.findViewById(R.id.noresult);
        final RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        notebookRef =db.collection("Posts");
        Query query = notebookRef.orderBy("priority", Query.Direction.DESCENDING);
        // The "base query" is a query with no startAt/endAt/limit clauses that the adapter can use
// to form smaller queries for each page.  It should only include where() and orderBy() clauses


// This configuration comes from the Paging Support Library
// https://developer.android.com/reference/android/arch/paging/PagedList.Config.html
//        PagedList.Config config = new PagedList.Config.Builder()
//                .setEnablePlaceholders(false)
//                .setPrefetchDistance(10)
//                .setPageSize(20)
//                .build();

// The options for the adapter combine the paging configuration with query information
// and application-specific options for lifecycle, etc.
//        FirestorePagingOptions<Note> options = new FirestorePagingOptions.Builder<Note>()
//                .setLifecycleOwner(this)
//                .setQuery(query, config, Note.class)
//                .build();
        // notebookRef =db.collection("Posts").document(note.getRefComments()).collection("comments");

//        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
//                .setQuery(query, Comment.class)
//                .build();
//        Query query= notebookRef.orderBy("priority", Query.Direction.DESCENDING);
//        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
//                .setQuery(query,Note.class)
//                .build();
//        adapter = new HomeAdapter(options, getActivity());
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setAdapter(adapter);
//        CollectionReference collectionReference= db.collection("Users").document(auth.getUid()).collection("Following");
//        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Following.add(document.getId());
//                    }
//
//                }
//            }
//        });


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        //adapter.startListening();
     //   adapter.registerAdapterDataObserver(adapterDataObserver);

    }

    @Override
    public void onStop() {
        super.onStop();
     //   adapter.stopListening();
      //  adapter.unregisterAdapterDataObserver(adapterDataObserver);
    }
}