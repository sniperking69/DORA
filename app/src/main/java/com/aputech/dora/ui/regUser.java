package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.aputech.dora.LocationJob;
import com.aputech.dora.Model.Note;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class regUser extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    TextInputLayout Email,Uname,Bio;
    Spinner spinner;
    FloatingActionButton upimg;
    CircleImageView dispimg;
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Users");;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_user);
        spinner= (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genderspinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Email=findViewById(R.id.email);
        Uname = findViewById(R.id.Uname);
        Bio = findViewById(R.id.Bio);
        spinner = findViewById(R.id.spinner);
        dispimg = findViewById(R.id.profiledefault);
        upimg = findViewById(R.id.upimage);
        upimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create an Intent with action as ACTION_PICK
                Intent intent=new Intent(Intent.ACTION_PICK);
                // Sets the type as image/*. This ensures only components of type image are selected
                intent.setType("image/*");
                //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                // Launching the Intent
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                dispimg.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void Continue(View view) {
        if (TextUtils.isEmpty(Uname.getEditText().getText().toString()) || TextUtils.isEmpty(Bio.getEditText().getText().toString()) ){
            Toast.makeText(regUser.this, "Empty field not allowed!", Toast.LENGTH_SHORT).show();
        }else {
            String email= Email.getEditText().getText().toString();
            String bio= Bio.getEditText().getText().toString();
            String username = Uname.getEditText().getText().toString();
            final User user = new User();
            user.setBio(bio);
            user.setEmailAdress(email);
            user.setUserid(firebaseAuth.getUid());
            user.setGender(spinner.getSelectedItem().toString());
            user.setUserName(username);
            if(filePath != null)
            {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                StorageReference ref = storageReference.child("images/"+ firebaseAuth.getUid());
                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                user.setProfileUrl(taskSnapshot.getUploadSessionUri().toString());
                                notebookRef.document(firebaseAuth.getCurrentUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(regUser.this, "User Added Successfully", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(regUser.this,HActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(regUser.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded "+(int)progress+"%");
                            }
                        });
            }else{
                user.setProfileUrl(null);
                notebookRef.document(firebaseAuth.getCurrentUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(regUser.this, "User Added Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(regUser.this,HActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        }


    }
}
