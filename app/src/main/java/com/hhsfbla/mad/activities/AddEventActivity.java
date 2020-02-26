package com.hhsfbla.mad.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final int RESULT_LOAD_IMAGE = 1;
    private Uri imageUri;
    private Bitmap bitmap;
//    private StorageTask uploadTask;
    private ImageButton backBtn2, doneBtn/*, imageBtn*/;
    private TextInputEditText nameEditTxt;
    private TextInputEditText dateEditTxt;
    private TextInputEditText timeEditTxt;
    private TextInputEditText locaEditTxt;
    private TextInputEditText descrEditTxt;
    private EditText linkEditTxt;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final String TAG = "ADDEVENTPAGE";
//    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private Button setDate, setTime;
    private byte[] uploadBytes;

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
        linkEditTxt = findViewById(R.id.linkEditTxt);
//        imageBtn = findViewById(R.id.imageBtn);
        setDate = findViewById(R.id.setDateButton);
        setTime = findViewById(R.id.setTimeButton);
//        storageReference = FirebaseStorage.getInstance().getReference("images").child("events");
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        onDateSet(null, year, month, day);
        onTimeSet(null, hour, minute);

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
//                if(uploadTask != null && uploadTask.isInProgress()) {
//                    Toast.makeText(getApplicationContext(), "Upload In Progress", Toast.LENGTH_LONG).show();
//                } else {
                    db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            DocumentReference ref = db.collection("chapters").document(snapshot.toObject(User.class).getChapter()).collection("events").document();

                            uploadFile(ref.getId());

                        }
                    });
//                }
            }
        });

//        imageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, RESULT_LOAD_IMAGE);
//            }
//        });

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
    }

    public void addEvent(Uri uri, final String id) {
//        Bitmap bitmap = ((BitmapDrawable) imageBtn.getDrawable()).getBitmap();
        final ChapterEvent event = new ChapterEvent(
                nameEditTxt.getText().toString().trim(),
                dateEditTxt.getText().toString().trim(),
                timeEditTxt.getText().toString().trim(),
                locaEditTxt.getText().toString().trim(),
                descrEditTxt.getText().toString().trim(),
                linkEditTxt.getText().toString().trim()/*,
                uri == null ? "" : uri.toString()*/);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
//
//            imageUri = data.getData();
//            bitmap = loadImage(imageUri);
//            imageBtn.setImageBitmap(bitmap);
////            Picasso.get().load(imageUri).into(imageBtn);
//        }
    }

    private void uploadFile(final String id) {
//        if(imageUri != null && bitmap != null) {
//            Log.d(TAG, imageUri.toString());
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
//                                    addEvent(uri, id);
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
//            Toast.makeText(this, "No Image Selected", Toast.LENGTH_LONG).show();
            addEvent(null, id);
//        }
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
        String month = (i1 + 1) < 10 ? "0" + (i1 + 1) : (i1  +1) + "";
        String day = i2 < 10 ? "0" + i2 : i2 + "";

        dateEditTxt.setText(month + "/" + day + "/" + i);
    }

    @Override
    public void onTimeSet(android.widget.TimePicker timePicker, int i, int i1) {
        String hour = i < 10 ? "0" + i : i + "";
        String minute = i1 < 10 ? "0" + i1 : i1 + "";
        timeEditTxt.setText(hour + ":" + minute);
    }

//    public Bitmap rotateImage(Bitmap source, float angle) {
//        Matrix matrix = new Matrix();
//        matrix.setRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
//                matrix, true);
//    }

//    public Bitmap decodeUriToBitmap(Uri sendUri) {
//        Bitmap getBitmap = null;
//        try {
//            InputStream image_stream;
//            try {
//                image_stream = getContentResolver().openInputStream(sendUri);
//                getBitmap = BitmapFactory.decodeStream(image_stream);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return getBitmap;
//    }

//    private Bitmap loadImage(Uri imageUri) {
//        Log.d(TAG, "helllllllllo");
//        Log.d(TAG, imageUri.toString());
//        Bitmap bitmap = decodeUriToBitmap(imageUri);
//        InputStream imageStream = null;
//        try {
//            try {
//                imageStream = getContentResolver().openInputStream(imageUri);
//                bitmap = BitmapFactory.decodeStream(imageStream);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        ExifInterface ei = null;
////        try {
////            if(imageStream == null) {
////                Log.d(TAG, "errrorr");
////            } else {
////                ei = new ExifInterface(imageStream);
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
////                ExifInterface.ORIENTATION_UNDEFINED);
////        Bitmap rotatedBitmap = null;
////        switch(orientation) {
////
////            case ExifInterface.ORIENTATION_ROTATE_90:
////                Log.d(TAG, "helllllllllo");
////
////                rotatedBitmap = rotateImage(bitmap, 90);
////                break;
////
////            case ExifInterface.ORIENTATION_ROTATE_180:
////                Log.d(TAG, "helllllllllo");
////
////                rotatedBitmap = rotateImage(bitmap, 180);
////                break;
////
////            case ExifInterface.ORIENTATION_ROTATE_270:
////                Log.d(TAG, "helllllllllo");
////
////                rotatedBitmap = rotateImage(bitmap, 270);
////                break;
////
////            case ExifInterface.ORIENTATION_NORMAL:
////                rotatedBitmap = bitmap;
////            default:
////                rotatedBitmap = bitmap;
////        }
//        Log.d(TAG, bitmap.getWidth() + "");
//        Log.d(TAG, bitmap.getHeight() + "");
//        if(bitmap.getWidth() > bitmap.getHeight()) {
//            return bitmap;
//        } else {
//            return rotateImage(bitmap, 270);
//        }
//    }

}
