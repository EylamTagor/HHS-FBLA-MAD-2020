package com.hhsfbla.mad.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.User;

/**
 * Represents a page where advisors and officers can edit the information, contact details, name, description, and location of their chapter
 */
public class EditChapterActivity extends AppCompatActivity {

    private TextInputEditText chapName, chapDesc, facebookPage, insta, location, website;
    private Button edit;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private ProgressDialog progressDialog;
    private static final String TAG = "Setup Activity";

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     * with data of the existing event from the database
     *
     * @param savedInstanceState the save state of the activity or page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chapter);
        setTitle("Chapter Setup");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        website = findViewById(R.id.editChapterWebsite);
        edit = findViewById(R.id.editChapterButton);
        chapDesc = findViewById(R.id.editChapterDescription);
        chapName = findViewById(R.id.editChapterName);
        facebookPage = findViewById(R.id.editChapterFacebook);
        insta = findViewById(R.id.editChapterInstagram);
        location = findViewById(R.id.editChapterLocation);

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUSer = documentSnapshot.toObject(User.class);
                db.collection("chapters").document(currentUSer.getChapter()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final Chapter chapter = documentSnapshot.toObject(Chapter.class);
                        if (!(chapter.getName() == null || chapter.getName().equalsIgnoreCase(""))) {
                            chapName.setText(chapter.getName());
                        } else {
                            chapDesc.setText("No Description");
                        }
                        if (!(chapter.getDescription() == null || chapter.getDescription().equalsIgnoreCase(""))) {
                            chapDesc.setText(chapter.getDescription());
                        } else {
                            chapDesc.setText("No Description");
                        }
                        if (!(chapter.getLocation() == null || chapter.getLocation().equalsIgnoreCase(""))) {
                            location.setText(chapter.getLocation());
                        } else {
                            location.setText("No Location");
                        }
                        if (!(chapter.getFacebookPage() == null || chapter.getFacebookPage().equalsIgnoreCase(""))) {
                            facebookPage.setText(chapter.getFacebookPage());
                        } else {
                            facebookPage.setText("No Facebook Page");
                        }
                        if (!(chapter.getInstagramTag() == null || chapter.getInstagramTag().equalsIgnoreCase(""))) {
                            insta.setText(chapter.getInstagramTag());
                        } else {
                            insta.setText("No Instagram Account");
                        }
                        if (!(chapter.getWebsite() == null || chapter.getWebsite().equalsIgnoreCase(""))) {
                            website.setText(chapter.getWebsite());
                        } else {
                            website.setText("No Website");
                        }
                    }
                });
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {

                        Chapter example = new Chapter(chapName.getText().toString(), location.getText().toString());
                        example.setInstagramTag(insta.getText().toString().trim());
                        example.setFacebookPage(facebookPage.getText().toString().trim());
                        example.setWebsite(website.getText().toString().trim());
                        example.setDescription(chapDesc.getText().toString().trim());
                        example.addMember(user.getUid());
                        db.collection("chapters").document(snapshot.toObject(User.class).getChapter()).set(example, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                startActivity(new Intent(EditChapterActivity.this, HomeActivity.class));
                            }
                        });
                    }
                });
            }
        });
    }
}
