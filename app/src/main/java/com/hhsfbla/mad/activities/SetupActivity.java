package com.hhsfbla.mad.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

/**
 * Represents a page where advisors can create new chapters for their schools if one hasn't been created yet
 */
public class SetupActivity extends AppCompatActivity {

    private TextInputEditText chapName, chapDesc, facebookPage, insta, location, website;
    private Button create;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private ProgressDialog progressDialog;
    private static final String TAG = "Setup Activity";

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     *
     * @param savedInstanceState the save state of the activity or page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        setTitle("Chapter Setup");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating...");
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        website = findViewById(R.id.chapterWebsite);
        create = findViewById(R.id.createChapterButton);
        chapDesc = findViewById(R.id.chapterName);
        chapName = findViewById(R.id.chapterName);
        facebookPage = findViewById(R.id.chapterFacebook);
        insta = findViewById(R.id.chapterInstagram);
        location = findViewById(R.id.chapterLocation);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                db.collection("chapters").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(doesChapterExist(queryDocumentSnapshots)) {
                            return;
                        }
                        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                                DocumentReference ref = db.collection("chapters").document();
                                String id = ref.getId();
                                User test;
                                if(snapshot.toObject(User.class) == null) {
                                    test = new User(user.getDisplayName(), id, user.getEmail());
                                    test.setPic(user.getPhotoUrl().toString());
                                    db.collection("users").document(user.getUid()).set(test);
                                } else {
                                    db.collection("users").document(user.getUid()).update("chapter", id);
                                }
                                db.collection("users").document(user.getUid()).update("userType", UserType.ADVISOR);
                                Chapter example = new Chapter(chapName.getText().toString(), location.getText().toString());
                                example.setInstagramTag(insta.getText().toString().trim());
                                example.setFacebookPage(facebookPage.getText().toString().trim());
                                example.setWebsite(website.getText().toString().trim());
                                example.setDescription(chapDesc.getText().toString().trim());
                                example.addMember(user.getUid());
                                ref.set(example).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
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

    /**
     * Checks if the chapter that the user is creating is similar to one that already exists
     * @param queryDocumentSnapshots a list of the chapters from the database
     * @return whether the chapter exists or not
     */
    private boolean doesChapterExist(QuerySnapshot queryDocumentSnapshots) {
        Chapter example = new Chapter(chapName.getText().toString(), location.getText().toString());
        example.setInstagramTag(insta.getText().toString().trim());
        example.setFacebookPage(facebookPage.getText().toString().trim());
        example.setDescription(chapDesc.getText().toString().trim());
        example.setWebsite(website.getText().toString().trim());
        for(Chapter chapter : queryDocumentSnapshots.toObjects(Chapter.class)) {
            if(chapter.equals(example)) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Chapter already Exists", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

}
