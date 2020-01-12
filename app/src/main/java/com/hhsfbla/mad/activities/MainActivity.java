package com.hhsfbla.mad.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hhsfbla.mad.R;

import java.util.concurrent.TimeUnit;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }, 1000);
    }
}
