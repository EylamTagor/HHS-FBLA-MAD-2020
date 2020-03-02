package com.hhsfbla.mad.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.hhsfbla.mad.utils.ImageRotator;
import com.squareup.picasso.Picasso;

/**
 * Represents a page where members and officers can join competitions,
 * and general competition details can be viewed, along with the competitors from the user's chapter
 */
public class EditEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final int RESULT_LOAD_IMAGE = 1;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private ImageButton backBtn2, doneBtn, imageBtn;
    private TextInputEditText nameEditTxt, dateEditTxt, timeEditTxt, locaEditTxt, descrEditTxt, memberLimit, linkEditTxt;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Uri imageUri;
    private Button setDateButton, setTimeButton, removeImageButton;
    private static final String TAG = "EDIT EVENT PAGE";
    private ProgressDialog progressDialog;
    private boolean hasImageChanged;
    private String id;
    private Bitmap bitmap;
    private ImageRotator imageRotator;

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     * with data of the existing event from the database
     *
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
        memberLimit = findViewById(R.id.eventMemberLimitEdit);
        linkEditTxt = findViewById(R.id.eventLinkEdit);
        imageBtn = findViewById(R.id.eventImageEdit);
        setDateButton = findViewById(R.id.editSetDateButton);
        setTimeButton = findViewById(R.id.editSetTimeButton);
        removeImageButton = findViewById(R.id.editRemoveImageButton);
        storageReference = FirebaseStorage.getInstance().getReference("images").child("events");
        imageRotator = new ImageRotator(this);
        id = getIntent().getStringExtra("EVENT_ID");

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
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), "Upload In Progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile(id);
                }
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

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

        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImageFromStorage();
            }
        });
        initPage();
    }

    private void removeImageFromStorage() {
        imageUri = null;
        bitmap = null;
        hasImageChanged = true;
        removeImageButton.setVisibility(View.GONE);
        imageBtn.setImageResource(R.drawable.camera_icon);
        imageBtn.setPadding(0, 64, 0, 64);
    }

    private void initPage() {
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                db.collection("chapters").document(currentUser.getChapter()).collection("events").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        ChapterEvent event = snapshot.toObject(ChapterEvent.class);
                        nameEditTxt.setText(event.getName());
                        dateEditTxt.setText(event.getDate());
                        timeEditTxt.setText(event.getTime());
                        locaEditTxt.setText(event.getLocation());
                        descrEditTxt.setText(event.getDescription());
                        linkEditTxt.setText(event.getFacebookLink());
                        if (event.getMemberLimit() != ChapterEvent.NO_LIMIT)
                            memberLimit.setText(event.getMemberLimit() + "");
                        else
                            memberLimit.setText("No Limit");

                        if (event.getPic() != null && !event.getPic().equals("")) {
                            imageUri = Uri.parse(event.getPic());
                            ImageRotator.loadImageWrapContent(getApplicationContext(), imageBtn, imageUri.toString());
                            removeImageButton.setVisibility(View.VISIBLE);
                        } else {
                            removeImageButton.setVisibility(View.GONE);
                            imageBtn.setImageResource(R.drawable.camera_icon);
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
        try {
            int x = Integer.parseInt(memberLimit.getText().toString().trim());
            if (x == 0) {
                memberLimit.setText("No Limit");
            }
        } catch (NumberFormatException e) {
            Log.d(TAG, "editEvent: " + e.getMessage());
            if (memberLimit.getText().toString().trim().equalsIgnoreCase("") || memberLimit.getText().toString().trim().equalsIgnoreCase("no limit")) {
                memberLimit.setText("No Limit");
            } else {
                Toast.makeText(this, "Please enter a numeric limit", Toast.LENGTH_SHORT).show();
                memberLimit.setText("No Limit");
                return;
            }
        }
        progressDialog.setMessage("Saving...");
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot userSnap) {
                db.collection("chapters").document(userSnap.get("chapter").toString()).collection("events").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {

                        ChapterEvent event = new ChapterEvent(
                                nameEditTxt.getText().toString().trim(),
                                dateEditTxt.getText().toString().trim(),
                                timeEditTxt.getText().toString().trim(),
                                locaEditTxt.getText().toString().trim(),
                                descrEditTxt.getText().toString().trim(),
                                linkEditTxt.getText().toString().trim(),
                                uri == null ? "" : uri.toString(),
                                memberLimit.getText().toString().trim().equalsIgnoreCase("no limit") ? ChapterEvent.NO_LIMIT : Integer.parseInt(memberLimit.getText().toString().trim()));
                        event.setAttendees(snapshot.toObject(ChapterEvent.class).getAttendees());
                        db.collection("chapters").document(userSnap.get("chapter").toString()).collection("events").document(id).set(event, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                startActivity(new Intent(EditEventActivity.this, HomeActivity.class));
                            }
                        });
                    }

                });
            }
        });
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
            ImageRotator.loadImageWrapContent(this, imageBtn, imageUri.toString());
            hasImageChanged = true;
            removeImageButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets the date textfield to the newly selected data in the format mm/dd/yyyy
     *
     * @param datePicker the dialog that chooses the date
     * @param i          the newly selected year
     * @param i1         the newly selected month
     * @param i2         the newly selected day
     */
    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
        String month = (i1 + 1) < 10 ? "0" + (i1 + 1) : (i1 + 1) + "";
        String day = i2 < 10 ? "0" + i2 : i2 + "";

        dateEditTxt.setText(month + "/" + day + "/" + i);
    }

    /**
     * Sets the time textfield to the newly selected time in the format hh:mm, military time
     *
     * @param timePicker the dialog that chooses the time
     * @param i          the newly selected hour
     * @param i1         the newly selected minute
     */
    @Override
    public void onTimeSet(android.widget.TimePicker timePicker, int i, int i1) {
        String hour = i < 10 ? "0" + i : i + "";
        String minute = i1 < 10 ? "0" + i1 : i1 + "";
        timeEditTxt.setText(hour + ":" + minute);
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
                                    editEvent(uri);
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
            if (!hasImageChanged && bitmap != null) {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            } else if(bitmap == null && hasImageChanged) {
                storageReference.child(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        editEvent(null);
                    }
                });
            } else {
                editEvent(imageUri);
            }
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }
}
