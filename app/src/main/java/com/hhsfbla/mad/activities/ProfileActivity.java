package com.hhsfbla.mad.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;
import com.hhsfbla.mad.dialogs.ChangeChapterDialog;
import com.hhsfbla.mad.dialogs.DeleteAccountDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Represents a class where users can view and edit their profile, as well as change chapters, sign out, or delete their account
 */
public class ProfileActivity extends AppCompatActivity implements DeleteAccountDialog.DeleteAccountDialogListener, ChangeChapterDialog.ChangeChapterDialogListener {

    private FirebaseUser user;
    private CircleImageView profilePic;
    private ImageButton backBtn, doneBtn;
    private TextInputEditText name, pos, blurb;
    private TextView chapterDisplay;
    private Button chapter;
    private FirebaseFirestore db;
    private Button sign_outBtn;
    private Button deleteAccount;
    private ProgressDialog progressDialog;
    private static final String TAG = "DASHBOARD";

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     * with data of the existing event from the database
     * @param savedInstanceState the save state of the activity or page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        backBtn = findViewById(R.id.backBtn);
        name = findViewById(R.id.nameTextField);
        chapter = findViewById(R.id.chapterButton);
        profilePic = findViewById(R.id.profilePic);
        sign_outBtn = findViewById(R.id.sign_outBtn);
        doneBtn = findViewById(R.id.doneBtn2);
        pos = findViewById(R.id.posTextField);
        blurb = findViewById(R.id.blurbTextField);
        deleteAccount = findViewById(R.id.deleteAccountButton);
        chapterDisplay = findViewById(R.id.chapterTextView);

        setTitle("Profile");
        String photo = String.valueOf(user.getPhotoUrl());
        Picasso.get().load(photo).into(profilePic);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(user.getUid()).update("name", name.getText().toString());
                db.collection("users").document(user.getUid()).update("officerPos", pos.getText().toString());
                db.collection("users").document(user.getUid()).update("blurb", blurb.getText().toString());
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


        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                User u = snapshot.toObject(User.class);
                name.setText(snapshot.toObject(User.class).getName());

                db.collection("chapters").document(u.getChapter()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        chapterDisplay.setText("Chapter: " + snapshot.toObject(Chapter.class).getName());
                    }
                });

                if (u.getUserType() == UserType.OFFICER) {
                    pos.setVisibility(View.VISIBLE);
                    blurb.setVisibility(View.VISIBLE);
                }

                pos.setText(u.getOfficerPos());
                blurb.setText(u.getBlurb());
            }
        });
    }

    /**
     * Signs the user out
     */
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

    /**
     * Opens the dialog to confirm changing chapters
     */
    private void openChangeChapterDialog() {
        ChangeChapterDialog dialog = new ChangeChapterDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    /**
     * Opens the dialog to confirm account deletion
     */
    private void openDeleteAccountDialog() {
        DeleteAccountDialog dialog = new DeleteAccountDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    /**
     * Deletes the account based on confirmation and sends the user to the login screen
     * @param confirm whether or not to delete the account
     */
    @Override
    public void sendConfirmation(boolean confirm) {
        if (confirm) {
            progressDialog.setMessage("Deleting...");
            progressDialog.show();
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {
                    db.collection("chapters").document(documentSnapshot.get("chapter").toString()).update("users", FieldValue.arrayRemove(user.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.collection("chapters").document(documentSnapshot.get("chapter").toString()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                        if (((List<String>) snap.get("attendees")).contains(user.getUid())) {
                                            db.collection("chapters").document(documentSnapshot.get("chapter").toString()).collection("events").document(snap.getId()).update("attendees", FieldValue.arrayRemove(user.getUid()));
                                        }
                                    }
                                    db.collection("users").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    FirebaseAuth.getInstance().signOut();
                                                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                                }
                                            });

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

    /**
     * Changes chapters based on the confirmation and sends the user to the chapter signup screen
     * @param confirm whether or not to change chapters
     */
    @Override
    public void sendChangeConfirmation(boolean confirm) {
        if (confirm) {
            progressDialog.setMessage("Saving changes...");
            progressDialog.show();
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {
                    final String chapter = documentSnapshot.get("chapter").toString();
                    db.collection("users").document(user.getUid()).update("chapter", "");
                    db.collection("users").document(user.getUid()).update("userType", UserType.MEMBER);
                    db.collection("chapters").document(chapter).update("users", FieldValue.arrayRemove(user.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.collection("chapters").document(chapter).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.toObject(Chapter.class).getUsers() == null || documentSnapshot.toObject(Chapter.class).getUsers().isEmpty()) {
                                        db.collection("chapters").document(chapter).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                finish();
                                                Intent intent = new Intent(ProfileActivity.this, SignupActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        db.collection("chapters").document(chapter).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                                    if (((List<String>) snap.get("attendees")).contains(user.getUid())) {
                                                        db.collection("chapters").document(chapter).collection("events").document(snap.getId()).update("attendees", FieldValue.arrayRemove(user.getUid()));
                                                    }
                                                }
                                                progressDialog.dismiss();
                                                finish();
                                                Intent intent = new Intent(ProfileActivity.this, SignupActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                        if (documentSnapshot.toObject(Chapter.class).getUsers().size() == 1) {
                                            db.collection("users").document(documentSnapshot.toObject(Chapter.class).getUsers().get(0)).update("userType", UserType.ADVISOR);
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }
}
