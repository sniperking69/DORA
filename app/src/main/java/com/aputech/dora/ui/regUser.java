package com.aputech.dora.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.aputech.dora.LocationJob;
import com.aputech.dora.Model.Note;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class regUser extends AppCompatActivity {
    TextInputLayout Email,Uname,Bio;
    Spinner spinner;
    FloatingActionButton upimg;
    CircleImageView dispimg;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Users");;
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



    }

    public void Continue(View view) {
        String email= Email.getEditText().getText().toString();
        String bio= Bio.getEditText().getText().toString();
        String username = Uname.getEditText().getText().toString();
        final User user = new User();
        user.setBio(bio);
        user.setProfileUrl(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
        user.setEmailAdress(email);
        user.setUserid(firebaseAuth.getUid());
        user.setGender(spinner.getSelectedItem().toString());
        user.setUserName(username);
        notebookRef.document(firebaseAuth.getCurrentUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(regUser.this, "Note Added Successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(regUser.this,HActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
