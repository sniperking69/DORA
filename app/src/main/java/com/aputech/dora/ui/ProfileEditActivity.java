package com.aputech.dora.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aputech.dora.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userinfo = db.collection("Users").document(auth.getUid());
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY = 2;
    TextInputLayout Email,Uname,Bio;
    Spinner spinner;
    FloatingActionButton upimg;
    CircleImageView dispimg;
    FirebaseStorage storage ;
    StorageReference storageReference ;
    private Uri filePath;
    boolean permission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        spinner= findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genderspinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        spinner.setAdapter(adapter);
        Email=findViewById(R.id.email);
        Uname = findViewById(R.id.Uname);
        Bio = findViewById(R.id.Bio);
        spinner = findViewById(R.id.spinner);
        dispimg = findViewById(R.id.profiledefault);
        upimg = findViewById(R.id.upimage);

    }
}
