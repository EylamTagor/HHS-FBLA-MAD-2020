package com.hhsfbla.mad.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hhsfbla.mad.R;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private ImageView profilePic;
    private ImageButton backBtn;
    private EditText name;
    private EditText email;
    private EditText chapter;
    private FirebaseFirestore db;
    private Button sign_outBtn;
    private Button doneBtn2;

    private static final String TAG = "DASHBOARD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        backBtn = findViewById(R.id.backBtn);
        name = findViewById(R.id.nameTextField);
        email = findViewById(R.id.emailTextField);
        chapter = findViewById(R.id.chapterTextField);
        profilePic = findViewById(R.id.profilePic);
        sign_outBtn = findViewById(R.id.sign_outBtn);
        doneBtn2 = findViewById(R.id.doneBtn2);
        setTitle("Profile");
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(documentSnapshot.get("name").toString());
                if(documentSnapshot.get("chapter") != null) {
                    db.collection("chapters").document(documentSnapshot.get("chapter").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot chapterSnap) {
                            chapter.setText(chapterSnap.get("name").toString());
                        }
                    });
                }
            }
        });
        email.setText(user.getEmail());
        String photo = String.valueOf(user.getPhotoUrl());
        Picasso.get().load(photo).into(profilePic);

        backBtn.setBackgroundColor(255);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });

        doneBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: save the new info into database
                db.collection("users").document(user.getUid()).update("name",name.getText().toString());
//                setName(name.getText());
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });


        sign_outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    public void signOut() {
        Log.d(TAG, "SIGN OUT: " + user.getEmail());
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        user = null;
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    }
                });
//        FirebaseAuth.getInstance().signOut();
//        user = null;
//        startActivity(new Intent(HomeActivity.this, LoginActivity.class));

    }






}
