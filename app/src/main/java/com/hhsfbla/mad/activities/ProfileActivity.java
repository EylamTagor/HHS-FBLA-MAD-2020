package com.hhsfbla.mad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hhsfbla.mad.R;

import java.io.InputStream;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private ImageView profilePic;
    private ImageButton backButton;
    private TextView name;
    private TextView email;
    private TextView chapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        backButton = findViewById(R.id.backBtn);
        name = findViewById(R.id.nameTextField);
        email = findViewById(R.id.emailTextField);
        chapter = findViewById(R.id.chapterTextField);
        profilePic = findViewById(R.id.profilePic);

        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
//        chapter.setText(user.);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });

        profilePic.setImageDrawable(LoadImageFromWebOperations(user.getPhotoUrl().toString()));
        Log.d("HIHIHI", user.getPhotoUrl().toString());
    }

    public Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
