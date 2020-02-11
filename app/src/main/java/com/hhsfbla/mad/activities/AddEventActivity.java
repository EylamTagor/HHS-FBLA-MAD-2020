package com.hhsfbla.mad.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.ChapterEvent;

public class AddEventActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;

    private ImageButton backBtn2, doneBtn;
    private EditText nameEditTxt, dateEditTxt, timeEditTxt, locaEditTxt, descrEditTxt, linkEditTxt;
    private ImageButton imageBtn;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final String TAG = "ADDEVENTPAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        setTitle("Add Event");

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        backBtn2 = findViewById(R.id.backBtn2);
        doneBtn = findViewById(R.id.finishEventBtn);
        nameEditTxt = findViewById(R.id.nameField);
        dateEditTxt = findViewById(R.id.dateField);
        timeEditTxt = findViewById(R.id.timeField);
        locaEditTxt = findViewById(R.id.locationField);
        descrEditTxt = findViewById(R.id.descriptionField);
        linkEditTxt = findViewById(R.id.facebookLinkField);
        imageBtn = findViewById(R.id.uploadEventPic);

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
                addEvent();
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
    }

    public void addEvent() {
        Bitmap bitmap = ((BitmapDrawable) imageBtn.getDrawable()).getBitmap();

        final ChapterEvent event = new ChapterEvent(
                nameEditTxt.getText().toString(),
                dateEditTxt.getText().toString(),
                timeEditTxt.getText().toString(),
                locaEditTxt.getText().toString(),
                descrEditTxt.getText().toString(),
                linkEditTxt.getText().toString(),
                bitmap);
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
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri image = data.getData();
            imageBtn.setImageURI(image);
        }
    }
}
