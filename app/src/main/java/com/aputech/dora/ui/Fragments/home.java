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

import com.aputech.dora.Adpater.HomeAdapter;
import com.aputech.dora.R;
import com.aputech.dora.Adpater.FireAdapter;
import com.aputech.dora.Model.Note;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
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
    HomeAdapter adapter;
    ArrayList<String> Following= new ArrayList<>();
    FirebaseAuth auth= FirebaseAuth.getInstance();

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
        CollectionReference collectionReference= db.collection("Users").document(auth.getUid()).collection("Following");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Following.add(document.getId());
                    }


                    Query query= notebookRef.orderBy("priority", Query.Direction.DESCENDING);
                    FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                            .setQuery(query,Note.class)
                            .build();
                    if (options.getSnapshots().size() > 0) {
                        relativeLayout.setVisibility(View.INVISIBLE);
                    } else {
                        relativeLayout.setVisibility(View.VISIBLE);
                    }
                 //   adapter = new HomeAdapter(options, getActivity(),Following);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                }
            }
        });


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