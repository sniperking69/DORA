package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aputech.dora.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class uploadActivity extends AppCompatActivity {
    private FirebaseFirestore firestoreDatabase;
    private DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        firestoreDatabase = FirebaseFirestore.getInstance();
    }

    public void uploading_Data(View view) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Tokyo");
        data.put("country", "Japan");
        firestoreDatabase.collection("cities")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Done", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed", "Error adding document", e);
                    }
                });
    }

    public void setLocation(View view) {

    }
}
