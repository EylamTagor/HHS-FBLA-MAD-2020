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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

import java.util.ArrayList;

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
    private StorageReference storageReference;

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
        storageReference = FirebaseStorage.getInstance().getReference("images").child("events");
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
                createChapter();
            }
        });

    }

    /**
     * Creates the chapter with data from the page components, adds the current user into the new chapter,
     * and sets the current user to advisor status
     */
    public void createChapter() {
        if (chapName.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            chapName.requestFocus();
            return;
        }
        progressDialog.show();
        db.collection("chapters").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (doesChapterExist(queryDocumentSnapshots)) {
                    Toast.makeText(getApplicationContext(), "Chapter already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        DocumentReference ref = db.collection("chapters").document();
                        final String id = ref.getId();
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
                                String isChangingChapter = getIntent().getStringExtra("CHANGE_CHAPTER");
                                if (isChangingChapter == null || isChangingChapter.equalsIgnoreCase("")) {
                                    db.collection("users").document(user.getUid()).update("chapter", id).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            db.collection("users").document(user.getUid()).update("userType", UserType.ADVISOR).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    startActivity(new Intent(SetupActivity.this, HomeActivity.class));
                                                }
                                            });
                                        }
                                    });

                                } else {
                                    changeChapter(id);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Checks if the chapter that the user is creating is similar to one that already exists
     *
     * @param queryDocumentSnapshots a list of the chapters from the database
     * @return whether the chapter exists or not
     */
    public boolean doesChapterExist(QuerySnapshot queryDocumentSnapshots) {
        Chapter example = new Chapter(chapName.getText().toString(), location.getText().toString());
        example.setInstagramTag(insta.getText().toString().trim());
        example.setFacebookPage(facebookPage.getText().toString().trim());
        example.setDescription(chapDesc.getText().toString().trim());
        example.setWebsite(website.getText().toString().trim());
        for (Chapter chapter : queryDocumentSnapshots.toObjects(Chapter.class)) {
            if (chapter.equals(example)) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Chapter already Exists", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the users chapter to the parameter id, sets their status to member, and removes them from all their previous
     * chapters events, if they were signed up. Also empties their event and competion signups
     *
     * @param id the id of the chapter selected
     */
    public void changeChapter(final String id) {
        final DocumentReference userRef = db.collection("users").document(user.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot userSnap) {
                final DocumentReference chapterRef = db.collection("chapters").document(userSnap.toObject(User.class).getChapter());
                chapterRef.update("users", FieldValue.arrayRemove(user.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        chapterRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                                if (snapshot.toObject(Chapter.class).getUsers().size() == 0) {
                                    deleteEventImages(chapterRef.collection("events"));
                                    chapterRef.delete();
                                    userRef.update("myEvents", new ArrayList<ChapterEvent>());
                                } else {
                                    if (snapshot.toObject(Chapter.class).getUsers().size() == 1) {
                                        db.collection("users").document(snapshot.toObject(Chapter.class).getUsers().get(0)).update("userType", UserType.ADVISOR);
                                    }
                                    removeFromEvents(chapterRef);
                                }
                                updateUser(id);
                            }
                        });
                    }
                });
            }
        });

    }

    private void deleteEventImages(CollectionReference ref) {
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                    if (snap.toObject(ChapterEvent.class).getPic() != null && !snap.toObject(ChapterEvent.class).getPic().equalsIgnoreCase(""))
                        storageReference.child(snap.getId()).delete();
                }
            }
        });
    }

    private void removeFromEvents(final DocumentReference chapterRef) {
        chapterRef.collection("events").whereArrayContains("attendees", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                    chapterRef.collection("events").document(snap.getId()).update("attendees", FieldValue.arrayRemove(user.getUid()));
                }
            }
        });
    }

    private void updateUser(final String id) {
        final DocumentReference userRef = db.collection("users").document(user.getUid());
        userRef.update("chapter", id).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                userRef.update("myEvents", new ArrayList<String>()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userRef.update("userType", UserType.ADVISOR).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userRef.update("comps", new ArrayList<String>()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

}
