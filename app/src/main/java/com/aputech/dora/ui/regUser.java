package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aputech.dora.LocationJob;
import com.aputech.dora.Model.Note;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterActivity;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class regUser extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY = 2;
    TextInputLayout Email, Uname, Bio;

    FloatingActionButton upimg;
    CircleImageView dispimg;
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Users");
    private Uri filePath;
    boolean permission;
    MaterialButton Continue;
    FloatingActionButton remove;
    boolean act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_user);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genderspinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Email = findViewById(R.id.email);
        Uname = findViewById(R.id.Uname);
        Bio = findViewById(R.id.Bio);
        remove = findViewById(R.id.remove);
        dispimg = findViewById(R.id.profiledefault);
        Continue = findViewById(R.id.button_next);
        upimg = findViewById(R.id.upimage);
        Intent intent= getIntent();
         act=intent.getBooleanExtra("edit",false);
        if (act){
            Continue.setText("UPDATE");
            DocumentReference documentReference =db.collection("Users").document(firebaseAuth.getUid());
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user= documentSnapshot.toObject(User.class);
                    Uname.getEditText().setText(user.getUserName());
                    Bio.getEditText().setText(user.getBio());
                    Email.getEditText().setText(user.getEmailAdress());
                }
            });
            Continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Continue();
                }
            });

        }else{
            Continue.setText("CONTINUE");
            Continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Continue();
                }
            });
        }
        Dexter.withActivity(regUser.this).withPermissions(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            permission = true;
                        } else {
                            permission = false;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        upimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permission) {
                    showPictureDialog();
                } else {
                    Toast.makeText(regUser.this, "Permission not Granted", Toast.LENGTH_SHORT).show();
                }

            }
        });


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
            dispimg.setImageBitmap(imageBitmap);
            filePath = getImageUri(regUser.this, imageBitmap);
            remove.setVisibility(View.VISIBLE);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filePath=null;
                    dispimg.setImageResource(R.drawable.ic_user);
                    remove.setVisibility(View.INVISIBLE);
                }
            });

        }
        if (requestCode == GALLERY && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(regUser.this.getContentResolver(), filePath);
                dispimg.setImageBitmap(bitmap);
                remove.setVisibility(View.VISIBLE);
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filePath=null;
                        dispimg.setImageResource(R.drawable.ic_user);
                        remove.setVisibility(View.INVISIBLE);
                    }
                });
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

    public void Continue() {
        if (TextUtils.isEmpty(Uname.getEditText().getText().toString()) || TextUtils.isEmpty(Bio.getEditText().getText().toString())) {
            Toast.makeText(regUser.this, "Empty field not allowed!", Toast.LENGTH_SHORT).show();
        } else {
            String email = Email.getEditText().getText().toString();
            String bio = Bio.getEditText().getText().toString();
            String username = Uname.getEditText().getText().toString();
            final User user = new User();
            user.setBio(bio);
            user.setEmailAdress(email);
            user.setUserid(firebaseAuth.getUid());
            user.setUserName(username);
            if (filePath != null) {

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                final StorageReference ref = storageReference.child("images/" + firebaseAuth.getUid());
                ref.putFile(filePath)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            user.setProfileUrl(uri.toString());
                                            progressDialog.dismiss();
                                            notebookRef.document(firebaseAuth.getCurrentUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(regUser.this, "User Added Successfully", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(regUser.this, HActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                                } else {

                                    progressDialog.dismiss();
                                    Toast.makeText(regUser.this, "Upload Failed Network Error", Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(regUser.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            } else {
                if (!act){
                    user.setProfileUrl(null);
                }
                notebookRef.document(firebaseAuth.getCurrentUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(regUser.this, "User Added Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(regUser.this, HActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        }


    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
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

    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }
}
