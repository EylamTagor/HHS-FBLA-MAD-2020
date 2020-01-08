package com.hhsfbla.mad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.hhsfbla.mad.R;

public class LoginActivity extends AppCompatActivity {

    private TextView welcomeTxtView;
    private Button loginGoogleBtn;
    private Button loginFacebookBtn;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        welcomeTxtView = findViewById(R.id.welcomeTxtView);
        loginGoogleBtn = findViewById(R.id.loginGoogleBtn);
        loginFacebookBtn = findViewById(R.id.loginFacebookBtn);
        loginBtn = findViewById(R.id.loginBtn);

    }
}
