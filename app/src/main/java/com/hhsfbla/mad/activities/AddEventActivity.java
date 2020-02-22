package com.hhsfbla.mad.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.ChapterEvent;
import com.squareup.picasso.Picasso;

public class AddEventActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private ImageButton backBtn2, doneBtn, imageBtn;
    private TextInputEditText nameEditTxt;
    private TextInputEditText dateEditTxt;
    private TextInputEditText timeEditTxt;
    private TextInputEditText locaEditTxt;
    private TextInputEditText descrEditTxt;
    private EditText linkEditTxt;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final String TAG = "ADDEVENTPAGE";
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        setTitle("Add Event");

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        backBtn2 = findViewById(R.id.backBtn2);
        doneBtn = findViewById(R.id.doneBtn);
        nameEditTxt = findViewById(R.id.nameEditTxt);
        dateEditTxt = findViewById(R.id.dateEditTxt);
        timeEditTxt = findViewById(R.id.timeEditTxt);
        locaEditTxt = findViewById(R.id.locaEditTxt);
        descrEditTxt = findViewById(R.id.descrEditTxt);
        linkEditTxt = findViewById(R.id.linkEditTxt);
        imageBtn = findViewById(R.id.imageBtn);
        storageReference = FirebaseStorage.getInstance().getReference("images");
        backBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddEventActivity.this, HomeActivity.class));
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {

            //TODO: save information typed on this page
            @Override
            public void onClick(View view) {
                if(uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), "Upload In Progress", Toast.LENGTH_LONG).show();
                } else {
                    uploadFile();
                }
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
    }

    public void addEvent(Uri uri) {
//        Bitmap bitmap = ((BitmapDrawable) imageBtn.getDrawable()).getBitmap();
        final ChapterEvent event = new ChapterEvent(
                nameEditTxt.getText().toString(),
                dateEditTxt.getText().toString(),
                timeEditTxt.getText().toString(),
                locaEditTxt.getText().toString(),
                descrEditTxt.getText().toString(),
                linkEditTxt.getText().toString(),
                uri == null ? "" : uri.toString());
//        if(uploadTask != null && uploadTask.isInProgress()) {
//            Toast.makeText(this, "Upload in Progress", Toast.LENGTH_LONG).show();
//        } else {
//            uploadFile();
//        }
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot userSnap) {
                Log.d(TAG, "Adding event");
                db.collection("chapters").document(userSnap.get("chapter").toString()).collection("events").document().set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(AddEventActivity.this, HomeActivity.class));
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageBtn);
        }
    }

    private void uploadFile() {
        if(imageUri != null) {
            final StorageReference fileRef = storageReference.child(nameEditTxt.getText().toString());
            uploadTask = fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    addEvent(uri);
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
            //TODO add dialog
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_LONG).show();
            addEvent(null);
        }
    }
}
