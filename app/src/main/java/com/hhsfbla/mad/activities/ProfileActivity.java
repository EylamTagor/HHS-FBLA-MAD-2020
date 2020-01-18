package com.hhsfbla.mad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hhsfbla.mad.R;

import java.io.InputStream;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = findViewById(R.id.profilePic);
        user = FirebaseAuth.getInstance().getCurrentUser();
        profilePic.setImageDrawable(LoadImageFromWebOperations(user.getPhotoUrl().toString()));
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
