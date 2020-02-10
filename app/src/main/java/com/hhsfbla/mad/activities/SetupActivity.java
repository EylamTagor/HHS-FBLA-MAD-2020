package com.hhsfbla.mad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.User;

public class SetupActivity extends AppCompatActivity {

    private TextView chapName, chapDesc, facebookPage, insta, location;
    private Button create;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final String TAG = "Setup Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        setTitle("Chapter Setup");

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        create = findViewById(R.id.createChapterButton);
        chapDesc = findViewById(R.id.chapterName);
        chapName = findViewById(R.id.chapterName);
        facebookPage = findViewById(R.id.chapterFacebook);
        insta = findViewById(R.id.chapterInstagram);
        location = findViewById(R.id.chapterLocation);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("chapters").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Chapter example = new Chapter(chapName.getText().toString(), location.getText().toString());
                        for(Chapter chapter : queryDocumentSnapshots.toObjects(Chapter.class)) {
                            if(chapter.equals(example)) {
                                Toast.makeText(getApplicationContext(), "Chapter already Exists", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                });
                db.collection("users").document(user.getUid()).set(new User(user.getDisplayName(), null, user.getEmail())).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Chapter chapter = new Chapter(chapName.getText().toString(), location.getText().toString());
                        Log.d(TAG, chapName.getText().toString());
                        chapter.setDescription(chapDesc.getText().toString());
                        chapter.setFacebookPage(facebookPage.getText().toString());
                        chapter.setInstagramTag(insta.getText().toString());
                        chapter.addMember(user.getUid());
                        final DocumentReference newChap = db.collection("chapters").document();
                        newChap.set(chapter).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.collection("users").document(user.getUid()).update("chapter", newChap.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(SetupActivity.this, HomeActivity.class));
                                    }
                                });
                            }
                        });
                    }
                });

            }
        });

    }
}
