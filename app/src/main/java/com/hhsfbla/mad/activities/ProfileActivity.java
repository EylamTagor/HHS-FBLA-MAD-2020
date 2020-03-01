package com.hhsfbla.mad.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;
import com.hhsfbla.mad.dialogs.ChangeChapterDialog;
import com.hhsfbla.mad.dialogs.DeleteAccountDialog;
import com.hhsfbla.mad.utils.ImageRotator;
import com.squareup.picasso.Picasso;

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
    private static final int RESULT_LOAD_IMAGE = 1;
    private Bitmap bitmap;
    private Uri imageUri;
    private boolean hasImageChanged;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private ImageRotator imageRotator;

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     * with data of the existing event from the database
     *
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
        storageReference = FirebaseStorage.getInstance().getReference("images").child("pfps");
        hasImageChanged = false;
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
        imageRotator = new ImageRotator(this);
        setTitle("Profile");

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
                if (u.getPic() != null && !u.getPic().equalsIgnoreCase("")) {
                    Picasso.get().load(Uri.parse(u.getPic())).into(profilePic);
                } else {
                    Picasso.get().load(user.getPhotoUrl()).into(profilePic);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), "Upload In Progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile(user.getUid());
                }
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
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
    }

    private void signOut() {
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

    /**
     * Deletes the account based on confirmation and sends the user to the login screen
     *
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
                            db.collection("chapters").document(documentSnapshot.get("chapter").toString()).collection("events").whereArrayContains("attendees", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                        db.collection("chapters").document(documentSnapshot.get("chapter").toString()).collection("events").document(snap.getId()).update("attendees", FieldValue.arrayRemove(user.getUid()));
                                    }
                                    Log.d(TAG, "onSuccess: " + documentSnapshot.toObject(User.class).getPic());
                                    Log.d(TAG, "onSuccess: " + user.getPhotoUrl());
                                    if (documentSnapshot.toObject(User.class).getPic() != null && !documentSnapshot.toObject(User.class).getPic().equalsIgnoreCase("") && !documentSnapshot.toObject(User.class).getPic().equalsIgnoreCase(user.getPhotoUrl().toString())) {
                                        deleteFromStorage();
                                    } else {

                                        db.collection("users").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FirebaseAuth.getInstance().signOut();
                                                        progressDialog.dismiss();
                                                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                                    }
                                                });

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });

                }
            });
        }
    }

    private void deleteFromStorage() {
        Log.d(TAG, "deleteFromStorage: ");
        storageReference.child(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("users").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseAuth.getInstance().signOut();
                                progressDialog.dismiss();
                                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                            }
                        });

                    }
                });
            }
        });
    }

    /**
     * Changes chapters based on the confirmation and sends the user to the chapter signup screen
     *
     * @param confirm whether or not to change chapters
     */
    @Override
    public void sendChangeConfirmation(boolean confirm) {
        if (confirm) {
            startActivity(new Intent(ProfileActivity.this, SignupActivity.class));
        }
    }

    /**
     * This method gets called after an action to get data from the user
     * Sets image to newly selected image
     *
     * @param requestCode the request code of the request
     * @param resultCode  a code representing the state of the result of the action
     * @param data        the data gained from the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            bitmap = imageRotator.getImageBitmap(imageUri);
            profilePic.setImageBitmap(bitmap);
            hasImageChanged = true;
        }
    }

    /**
     * Uploads the event image to cloud storage with the file name as id
     *
     * @param id the name of the file
     */
    public void uploadFile(String id) {
        if (hasImageChanged && bitmap != null) {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            final StorageReference fileRef = storageReference.child(id);
            byte[] file = imageRotator.getBytesFromBitmap(bitmap);
            uploadTask = fileRef.putBytes(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    editUser(uri);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        } else {
            editUser(null);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void editUser(Uri uri) {
        db.collection("users").document(user.getUid()).update("name", name.getText().toString());
        db.collection("users").document(user.getUid()).update("officerPos", pos.getText().toString());
        db.collection("users").document(user.getUid()).update("blurb", blurb.getText().toString());
        if (uri != null) {
            db.collection("users").document(user.getUid()).update("pic", uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                }
            });
        } else {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        }
    }
}
