package com.hhsfbla.mad.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.dialogs.DatePicker;
import com.hhsfbla.mad.dialogs.TimePicker;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Represents a page where members and officers can join competitions,
 * and general competition details can be viewed, along with the competitors from the user's chapter
 */
public class EditEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final int RESULT_LOAD_IMAGE = 1;
//    private StorageTask uploadTask;
//    private StorageReference storageReference;
    private ImageButton backBtn2, doneBtn/*, imageBtn*/;
    private TextInputEditText nameEditTxt;
    private TextInputEditText dateEditTxt;
    private TextInputEditText timeEditTxt;
    private TextInputEditText locaEditTxt;
    private TextInputEditText descrEditTxt;
    private EditText linkEditTxt;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Uri imageUri;
    private Button setDateButton, setTimeButton;
    private static final String TAG = "EDIT EVENT PAGE";
    private ProgressDialog progressDialog;
    private boolean hasImageChanged;

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     * with data of the existing event from the database
     * @param savedInstanceState the save state of the activity or page
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        setTitle("Edit Event");
        progressDialog = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        hasImageChanged = false;
        backBtn2 = findViewById(R.id.editEventBackButton);
        doneBtn = findViewById(R.id.editEventFinishButton);
        nameEditTxt = findViewById(R.id.eventNameEdit);
        dateEditTxt = findViewById(R.id.eventDateEdit);
        timeEditTxt = findViewById(R.id.eventTimeEdit);
        locaEditTxt = findViewById(R.id.eventLocationEdit);
        descrEditTxt = findViewById(R.id.eventDescriptionEdit);
        linkEditTxt = findViewById(R.id.eventLinkEdit);
//        imageBtn = findViewById(R.id.eventImageEdit);
        setDateButton = findViewById(R.id.editSetDateButton);
        setTimeButton = findViewById(R.id.editSetTimeButton);
//        storageReference = FirebaseStorage.getInstance().getReference("images").child("events");

        backBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditEventActivity.this, HomeActivity.class));
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {

            //TODO: save information typed on this page
            @Override
            public void onClick(View view) {
//                if(uploadTask != null && uploadTask.isInProgress()) {
//                    Toast.makeText(getApplicationContext(), "Upload In Progress", Toast.LENGTH_LONG).show();
//                } else {
                    db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            db.collection("chapters").document(snapshot.toObject(User.class).getChapter()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for(DocumentSnapshot snap : queryDocumentSnapshots) {
                                        String name = getIntent().getStringExtra("EVENT_NAME");
                                        if(snap.toObject(ChapterEvent.class).getName().equalsIgnoreCase(name)) {
                                            uploadFile(snap.getId());
                                        }
                                    }
                                }
                            });

                        }
                    });
//               }
            }
        });

//        imageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, RESULT_LOAD_IMAGE);
//            }
//        });

        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePicker();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                db.collection("chapters").document(currentUser.getChapter()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String name = getIntent().getStringExtra("EVENT_NAME");

                        List<ChapterEvent> events = queryDocumentSnapshots.toObjects(ChapterEvent.class);
                        for(ChapterEvent event : events) {
                            if(event.getName().equalsIgnoreCase(name)) {
                                nameEditTxt.setText(event.getName());
                                dateEditTxt.setText(event.getDate());
                                timeEditTxt.setText(event.getTime());
                                locaEditTxt.setText(event.getLocation());
                                descrEditTxt.setText(event.getDescription());
                                linkEditTxt.setText(event.getFacebookLink());
//                                if(event.getPic() != null) {
//                                    imageUri = Uri.parse(event.getPic());
//                                    Picasso.get().load(imageUri).fit().centerCrop().into(imageBtn);
//                                }
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * Updates the chapter event in the database using the textfields on this page and selected image
     *
     * @param uri the new uri of the event image
     */
    public void editEvent(final Uri uri) {

        progressDialog.setMessage("Saving...");
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot userSnap) {
                db.collection("chapters").document(userSnap.get("chapter").toString()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String name = getIntent().getStringExtra("EVENT_NAME");
                        List<ChapterEvent> events = queryDocumentSnapshots.toObjects(ChapterEvent.class);
                        for(DocumentSnapshot snap : queryDocumentSnapshots) {
                            if(snap.toObject(ChapterEvent.class).getName().equalsIgnoreCase(name)) {
                                final ChapterEvent event = new ChapterEvent(
                                        nameEditTxt.getText().toString().trim(),
                                        dateEditTxt.getText().toString().trim(),
                                        timeEditTxt.getText().toString().trim(),
                                        locaEditTxt.getText().toString().trim(),
                                        descrEditTxt.getText().toString().trim(),
                                        linkEditTxt.getText().toString().trim()/*,
                                        uri == null ? snap.toObject(ChapterEvent.class).getPic() : uri.toString()*/);
                                db.collection("chapters").document(userSnap.get("chapter").toString()).collection("events").document(snap.getId()).set(event, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(EditEventActivity.this, HomeActivity.class));
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * This method gets called after an action to get data from the user
     * Sets image to newly selected image
     * @param requestCode the request code of the request
     * @param resultCode a code representing the state of the result of the action
     * @param data the data gained from the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
//            imageUri = data.getData();
//            imageBtn.setImageURI(imageUri);
//            hasImageChanged = true;
//        }
    }

    /**
     * Uploads the event image to cloud storage with the file name as id
     * @param id the name of the file
     */
    private void uploadFile(String id) {
//        if(imageUri != null && hasImageChanged) {
//            progressDialog.setMessage("Uploading...");
//            progressDialog.show();
//            final StorageReference fileRef = storageReference.child(id);
//            uploadTask = fileRef.putFile(imageUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    editEvent(uri);
//                                }
//                            });
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//
//                        }
//                    });
//        } else {
            //TODO add dialog
//            Toast.makeText(this, "No Image Selected", Toast.LENGTH_LONG).show();
            editEvent(null);
//        }
    }

    /**
     * Sets the date textfield to the newly selected data in the format mm/dd/yyyy
     * @param datePicker the dialog that chooses the date
     * @param i the newly selected year
     * @param i1 the newly selected month
     * @param i2 the newly selected day
     */
    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
        String month = (i1 + 1) < 10 ? "0" + (i1 + 1) : (i1  +1) + "";
        String day = i2 < 10 ? "0" + i2 : i2 + "";

        dateEditTxt.setText(month + "/" + day + "/" + i);
    }

    /**
     * Sets the time textfield to the newly selected time in the format hh:mm, military time
     * @param timePicker the dialog that chooses the time
     * @param i the newly selected hour
     * @param i1 the newly selected minute
     */
    @Override
    public void onTimeSet(android.widget.TimePicker timePicker, int i, int i1) {
        String hour = i < 10 ? "0" + i : i + "";
        String minute = i1 < 10 ? "0" + i1 : i1 + "";
        timeEditTxt.setText(hour + ":" + minute);
    }
}
