package com.aputech.dora.ui.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aputech.dora.Adpater.HomeAdapter;
import com.aputech.dora.Model.Note;
import com.aputech.dora.Model.User;
import com.aputech.dora.ui.ProfileSettings;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userinfo = db.collection("Users").document(auth.getUid());
    private CollectionReference notebookRef = db.collection("Notebook");
    private HomeAdapter adapter;
    private User user;
    private CircleImageView profileimg;
    private TextView following,posts,followers,name,bio;
    private ImageView level;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bio = root.findViewById(R.id.bio);
        MaterialButton settings=root.findViewById(R.id.followandset);
        settings.setText("SETTINGS");
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), ProfileSettings.class);
                startActivity(intent);
            }
        });
        followers= root.findViewById(R.id.numFolo);
        following =root.findViewById(R.id.numFoly);
        posts =root.findViewById(R.id.numPosts);
        level=root.findViewById(R.id.level);

        name= root.findViewById(R.id.nametitle);
        profileimg= root.findViewById(R.id.profiledisplay);
        userinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                Query profilequery= notebookRef.whereIn(FieldPath.documentId(),user.getPosts()).orderBy("priority", Query.Direction.DESCENDING);

                FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(profilequery, Note.class)
                        .build();
                adapter = new HomeAdapter(options,getActivity());

                recyclerView.setAdapter(adapter);
                adapter.startListening();
                bio.setText(user.getBio());
                posts.setText(String.valueOf(user.getPosts().size()));
                if (user.getFollowers()!=null){
                    followers.setText(String.valueOf(user.getFollowers().size()));
                }else{
                    followers.setText(String.valueOf(0));
                }
                if (user.getFollowing()!=null){
                    following.setText(String.valueOf(user.getFollowing().size()));

                }else{
                    following.setText(String.valueOf(0));
                }

                if (user.getUserlevel()==0){
                    Glide
                            .with(getActivity())
                            .load(R.drawable.ic_grade)
                            .into(level);
                }
                if (user.getUserlevel()==1){
                    Glide
                            .with(getActivity())
                            .load(R.drawable.ic_grade1)
                            .into(level);
                }
                if (user.getUserlevel()==2){
                    Glide
                            .with(getActivity())
                            .load(R.drawable.ic_grade2)
                            .into(level);
                }
                name.setText(user.getUserName());
                Glide
                        .with(getActivity())
                        .load(user.getProfileUrl())
                        .into(profileimg);

            }
        });
        ImageView editImage = root.findViewById(R.id.editimage);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//        final TextView textView = root.findViewById(R.id.section_label);
//        pageViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
       // adapter.stopListening();
    }
}
