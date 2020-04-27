package com.aputech.dora.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aputech.dora.R;
import com.aputech.dora.ui.Fragments.Profile;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ProfileSettings extends AppCompatActivity {
    CardView profileedit,logout;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        profileedit= findViewById(R.id.editProfile);
        logout = findViewById(R.id.logout);
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Settings");
        setSupportActionBar(myToolbar);

        profileedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettings.this,ProfileSettings.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                AuthUI.getInstance().signOut(ProfileSettings.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(ProfileSettings.this,SplashActivity.class);
                                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                startActivity(intent);

                            }
                        });
            }
        });

    }
}
