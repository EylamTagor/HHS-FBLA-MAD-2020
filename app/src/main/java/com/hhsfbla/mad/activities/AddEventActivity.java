package com.hhsfbla.mad.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

import java.util.Calendar;

/**
 * Represents a page where officers and advisors can create chapter events
 */
public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private ImageButton backBtn2, doneBtn, imageBtn;
    private TextInputEditText nameEditTxt, dateEditTxt, timeEditTxt, locaEditTxt, descrEditTxt, limitEditText, linkEditTxt;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final String TAG = "ADDEVENTPAGE";
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private Button setDate, setTime, removeImage;
    private Bitmap bitmap;
    private ImageRotator imageRotator;

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     * with data from the database
     *
     * @param savedInstanceState the save state of the activity or page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        setTitle("Add Event");

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        backBtn2 = findViewById(R.id.backBtn2);
        doneBtn = findViewById(R.id.doneBtn);
        nameEditTxt = findViewById(R.id.nameEditTxt);
        dateEditTxt = findViewById(R.id.dateEditTxt);
        timeEditTxt = findViewById(R.id.timeEditTxt);
        locaEditTxt = findViewById(R.id.locaEditTxt);
        descrEditTxt = findViewById(R.id.descrEditTxt);
        limitEditText = findViewById(R.id.memberLimitAdd);
        linkEditTxt = findViewById(R.id.linkEditTxt);
        imageBtn = findViewById(R.id.addEventImageBtn);
        setDate = findViewById(R.id.setDateButton);
        setTime = findViewById(R.id.setTimeButton);
        removeImage = findViewById(R.id.removeImageButton);
        storageReference = FirebaseStorage.getInstance().getReference("images").child("events");
        imageRotator = new ImageRotator(this);

        setCurrentTime();

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
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), "Upload In Progress", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            DocumentReference ref = db.collection("chapters").document(snapshot.toObject(User.class).getChapter()).collection("events").document();
                            uploadFile(ref.getId());
                        }
                    });
                }
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePicker();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage();
            }
        });
    }

    private void removeImage() {
        imageUri = null;
        bitmap = null;
        removeImage.setVisibility(View.GONE);
        imageBtn.setImageResource(R.drawable.camera_icon);
        imageBtn.setPadding(0, 64, 0, 64);
    }

    private void setCurrentTime() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        onDateSet(null, year, month, day);
        onTimeSet(null, hour, minute);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Creates a new chapter event in the database using the textfields on this page and selected image
     *
     * @param uri the uri of the event image
     * @param id  the id of the event in the database
     */
    public void addEvent(Uri uri, final String id) {
        try {
            int x = Integer.parseInt(limitEditText.getText().toString().trim());
            if(x == 0) {
                limitEditText.setText("No Limit");
            }
        } catch(NumberFormatException e) {
            Log.d(TAG, "editEvent: " + e.getMessage());
            if(limitEditText.getText().toString().trim().equalsIgnoreCase("") || limitEditText.getText().toString().trim().equalsIgnoreCase("no limit")) {
                limitEditText.setText("No Limit");
            } else {
                Toast.makeText(this, "Please enter a numeric limit", Toast.LENGTH_SHORT).show();
                limitEditText.setText("No Limit");
                return;
            }
        }
        final ChapterEvent event = new ChapterEvent(
                nameEditTxt.getText().toString().trim(),
                dateEditTxt.getText().toString().trim(),
                timeEditTxt.getText().toString().trim(),
                locaEditTxt.getText().toString().trim(),
                descrEditTxt.getText().toString().trim(),
                linkEditTxt.getText().toString().trim(),
                uri == null ? "" : uri.toString(),
                limitEditText.getText().toString().trim().equalsIgnoreCase("no limit") ? ChapterEvent.NO_LIMIT : Integer.parseInt(limitEditText.getText().toString().trim()));
        progressDialog.setMessage("Saving...");
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot userSnap) {
                Log.d(TAG, "Adding event");
                DocumentReference ref = db.collection("chapters").document(userSnap.get("chapter").toString()).collection("events").document(id);
                ref.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        startActivity(new Intent(AddEventActivity.this, HomeActivity.class));
                    }
                });
            }
        });

    }

    /**
     * This method gets called after an action to get data from the user
     *
     * @param requestCode the request code of the request
     * @param resultCode  a code representing the state of the result of the action
     * @param data        the data gained from the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            bitmap = imageRotator.getImageBitmap(imageUri);
            imageBtn.setImageBitmap(bitmap);
            removeImage.setVisibility(View.VISIBLE);
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
    public void uploadFile(final String id) {
        if (bitmap != null) {
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
                                    addEvent(uri, id);
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
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            addEvent(null, id);
        }
    }
}
