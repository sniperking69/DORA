package com.aputech.dora.ui;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.aputech.dora.LocationJob;
import com.aputech.dora.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.aputech.dora.ui.HActivity.JOB_ID;

public class ProfileSettings extends AppCompatActivity {
    CardView profileedit, logout;
    Switch notiswitch;
    private Toolbar myToolbar;

    public static boolean isJobServiceOn(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        boolean hasBeenScheduled = false;
        if (scheduler != null) {
            for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
                if (jobInfo.getId() == JOB_ID) {
                    hasBeenScheduled = true;
                    break;
                }

            }
        }
        return hasBeenScheduled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        profileedit = findViewById(R.id.editProfile);
        logout = findViewById(R.id.logout);
        myToolbar = findViewById(R.id.my_toolbar);
        notiswitch = findViewById(R.id.notiswitch);
        myToolbar.setTitle("Settings");
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);
        SharedPreferences sharedPref = getSharedPreferences("usersettings", Context.MODE_PRIVATE);
        int set = sharedPref.getInt("JOB", 1);
        if (set == 1) {
            notiswitch.setChecked(true);
        } else {
            notiswitch.setChecked(false);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        notiswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getSharedPreferences("usersettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                if (isChecked) {
                    MainOpen();
                    editor.putInt("JOB", 1);
                    editor.apply();
                } else {
                    editor.putInt("JOB", 0);
                    editor.apply();
                    JobScheduler jobScheduler =
                            (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    jobScheduler.cancel(JOB_ID);
                }
            }
        });
        profileedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettings.this, regUser.class);
                intent.putExtra("edit", true);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuthUI.getInstance().signOut(ProfileSettings.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        JobScheduler jobScheduler =
                                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                        jobScheduler.cancel(JOB_ID);
                        Intent intent = new Intent(ProfileSettings.this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                });
            }
        });

    }

    public void MainOpen() {
        ComponentName componentName = new ComponentName(this, LocationJob.class);
        JobInfo info = new JobInfo.Builder(JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (!isJobServiceOn(getApplicationContext())) {
            int resultCode = 0;
            if (scheduler != null) {
                resultCode = scheduler.schedule(info);
            }
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d("Drop Chat Service", "Job scheduled");
            } else {
                Log.d("Drop Chat Service", "Job scheduling failed");
            }
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
