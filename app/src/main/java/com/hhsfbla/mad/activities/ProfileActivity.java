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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.dialogs.ChangeChapterDialog;
import com.hhsfbla.mad.dialogs.DeleteAccountDialog;
import com.hhsfbla.mad.dialogs.DeleteEventDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileActivity extends AppCompatActivity implements DeleteAccountDialog.DeleteAccountDialogListener, ChangeChapterDialog.ChangeChapterDialogListener {

    private FirebaseUser user;
    private ImageView profilePic;
    private ImageButton backBtn, doneBtn2;
    private TextInputEditText name, email;
    private Button chapter;
    private FirebaseFirestore db;
    private Button sign_outBtn;
    private Button deleteAccount;

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
        chapter = findViewById(R.id.chapterButton);
        profilePic = findViewById(R.id.profilePic);
        sign_outBtn = findViewById(R.id.sign_outBtn);
        doneBtn2 = findViewById(R.id.doneBtn2);
        deleteAccount = findViewById(R.id.deleteAccountButton);
        setTitle("Profile");
        email.setText(user.getEmail());
        String photo = String.valueOf(user.getPhotoUrl());
        Picasso.get().load(photo).into(profilePic);





        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });

        doneBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(user.getUid()).update("name",name.getText().toString());
//                setName(name.getText());
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });


        chapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeChapterDialog();
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDeleteAccountDialog();
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
    }

    private void openChangeChapterDialog() {
        ChangeChapterDialog dialog = new ChangeChapterDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    private void openDeleteAccountDialog() {
        DeleteAccountDialog dialog = new DeleteAccountDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void sendConfirmation(boolean confirm) {
        if(confirm) {
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {
                    db.collection("chapters").document(documentSnapshot.get("chapter").toString()).update("users", FieldValue.arrayRemove(user.getUid()));
                    db.collection("chapters").document(documentSnapshot.get("chapter").toString()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot snap : queryDocumentSnapshots) {
                                if(((List<String>)snap.get("attendees")).contains(user.getUid())) {
                                    db.collection("chapters").document(documentSnapshot.get("chapter").toString()).collection("events").document(snap.getId()).update("attendees", FieldValue.arrayRemove(user.getUid()));
                                }
                            }
                            db.collection("users").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    public void sendChangeConfirmation(boolean confirm) {
        if(confirm) {

            db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {
                    db.collection("chapters").document(documentSnapshot.get("chapter").toString()).update("users", FieldValue.arrayRemove(user.getUid()));
                    db.collection("chapters").document(documentSnapshot.get("chapter").toString()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot snap : queryDocumentSnapshots) {
                                if(((List<String>)snap.get("attendees")).contains(user.getUid())) {
                                    db.collection("chapters").document(documentSnapshot.get("chapter").toString()).collection("events").document(snap.getId()).update("attendees", FieldValue.arrayRemove(user.getUid()));
                                }
                            }
                            finish();
                            Intent intent = new Intent(ProfileActivity.this, SignupActivity.class);
                            startActivity(intent);
                        }
                    });
                    
                }
            });
        }
    }
}
