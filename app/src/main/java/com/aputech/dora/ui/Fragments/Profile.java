package com.aputech.dora.ui.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aputech.dora.Adpater.FireAdapter;
import com.aputech.dora.Model.Post;
import com.aputech.dora.Model.User;
import com.aputech.dora.ui.ProfileSettings;
import com.aputech.dora.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

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
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY = 2;
    private DocumentReference userinfo = db.collection("Users").document(auth.getUid());
    private User user;
    private CollectionReference collectionReference = db.collection("Posts");
    private RelativeLayout relativeLayout;
    private CircleImageView profileimg;
    ImageView editImage;
    private Uri filePath;
    FireAdapter adapter;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    private TextView following, posts, followers, name, bio,email;
    private ImageView level;
    RecyclerView recyclerView;
    private RecyclerView.AdapterDataObserver observer;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Post> Posts = new ArrayList<>();


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
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
       recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bio = root.findViewById(R.id.bio);
        editImage = root.findViewById(R.id.editimage);
        relativeLayout = root.findViewById(R.id.noresult);
        followers = root.findViewById(R.id.numFolo);
        following = root.findViewById(R.id.numFoly);
        posts = root.findViewById(R.id.numPosts);
        level = root.findViewById(R.id.level);
        email =root.findViewById(R.id.email);
        name = root.findViewById(R.id.nametitle);
        profileimg = root.findViewById(R.id.profiledisplay);
        MaterialButton settings = root.findViewById(R.id.followandset);
        Query query = collectionReference.whereEqualTo("userid",auth.getUid()).orderBy("priority", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();
        adapter = new FireAdapter(options,getActivity());
        recyclerView.setAdapter(adapter);
        settings.setText("SETTINGS");
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileSettings.class);
                startActivity(intent);
            }
        });
        relativeLayout.setVisibility(View.VISIBLE);
        observer =new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (adapter.getItemCount()==0){
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (adapter.getItemCount() >0){
                    relativeLayout.setVisibility(View.INVISIBLE);
                }else{
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }
        };

        userinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                posts.setText(String.valueOf(user.getPostnum()));
                bio.setText(user.getBio());
                email.setText(user.getEmailAdress());
                followers.setText(String.valueOf(user.getFollower()));
                following.setText(String.valueOf(user.getFollowing()));
                if (user.getPostnum() < 100) {
                    Glide
                            .with(getActivity())
                            .load(R.drawable.ic_grade)
                            .into(level);
                }
                if (user.getPostnum() < 100 && user.getPostnum() > 500 ) {
                    Glide
                            .with(getActivity())
                            .load(R.drawable.ic_grade1)
                            .into(level);
                }
                if (user.getPostnum() > 500) {
                    Glide
                            .with(getActivity())
                            .load(R.drawable.ic_grade2)
                            .into(level);
                }
                name.setText(user.getUserName());
                if (user.getProfileUrl()!=null){
                    Glide
                            .with(getActivity())
                            .load(user.getProfileUrl())
                            .into(profileimg);
                }


            }
        });
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        adapter.startListening();
        adapter.registerAdapterDataObserver(observer);
        return root;
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        pictureDialog.setIcon(R.drawable.ic_upload);
        String[] pictureDialogItems = {
                "Gallery",
                "Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profileimg.setImageBitmap(imageBitmap);
            filePath = getImageUri(getActivity(), imageBitmap);
            Continue();
        }
        if (requestCode == GALLERY && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                profileimg.setImageBitmap(bitmap);
                Continue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "ProfileImage", null);
        return Uri.parse(path);
    }
    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    public void Continue() {
            if (filePath != null) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Uploading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                final StorageReference ref = storageReference.child("images/" + auth.getUid());
                ref.putFile(filePath)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            progressDialog.dismiss();
                                            userinfo.update("profileUrl",uri.toString());
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Upload Failed Network Error", Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
            }






    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
