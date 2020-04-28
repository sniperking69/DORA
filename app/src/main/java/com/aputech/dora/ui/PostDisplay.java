package com.aputech.dora.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aputech.dora.Adpater.CommentAdapter;
import com.aputech.dora.Model.Note;
import com.aputech.dora.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostDisplay extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef ;
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private CommentAdapter adapter;
    MaterialButton up,down;
    private EditText editText;
    RelativeLayout noresult;
    String Document;
    String Userid,user_name;
    int Commentnum;
    private String collection;
    ImageView delete,edit;
    Note note;
    private TextView postText,userName,post_time;
    ImageView locate,ProfileImg;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);
        editText = findViewById(R.id.commenttext);
        noresult= findViewById(R.id.noresult);
        userName = findViewById(R.id.user_name);
        post_time= findViewById(R.id.time);
        locate =findViewById(R.id.locate);
        up = findViewById(R.id.upbutton);
        delete = findViewById(R.id.delete);
        edit = findViewById(R.id.edit);
        down = findViewById(R.id.downbutton);
        postText = findViewById(R.id.text_view_description);
        ProfileImg = findViewById(R.id.poster_profile);
    }
}
