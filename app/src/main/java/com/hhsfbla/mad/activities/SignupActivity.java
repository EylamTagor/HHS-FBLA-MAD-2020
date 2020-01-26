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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

public class SignupActivity extends AppCompatActivity {


    private ImageButton backBtn;
    private TextView areyouTxtView;
    private RadioButton advisorRadioBtn;
    private RadioButton officerRadioBtn;
    private RadioButton studentRadioBtn;
    private Button okBtn;
    private TextView createTxtView;
    private EditText nameEditTxt;
    private EditText findChapterText;
    private Button nextBtn;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser fuser;
    private static final String TAG = "signupactivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fuser = auth.getCurrentUser();

        backBtn = findViewById(R.id.backBtn);
        okBtn = findViewById(R.id.okBtn);
        createTxtView = findViewById(R.id.createTxtView);
        nameEditTxt = findViewById(R.id.nameEditTxt);
        findChapterText = findViewById(R.id.find_chapter_edit_text);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicked");
                updateUI();
            }
        });

//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
//            }
//        });
    }

    public void updateUI() {
        Log.d(TAG, "updateui");
        if(findChapterText.getText().toString().matches("") && nameEditTxt.getText().toString().matches("")) {
            Log.d(TAG, "both fields empty");
            Toast.makeText(SignupActivity.this, "Enter data", Toast.LENGTH_LONG);
        } else if(!findChapterText.getText().toString().matches("") && !nameEditTxt.getText().toString().matches("")) {
            Log.d(TAG, "both fields full");
            Toast.makeText(SignupActivity.this, "Enter data", Toast.LENGTH_LONG);
        } else if(!findChapterText.getText().toString().matches("")) {
            Log.d(TAG, "top field: ");
            db.collection("chapters").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(final QuerySnapshot queryChapterSnapshots) {
                    Log.d(TAG, "getting chapters success");
                    db.collection("users").document(fuser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d(TAG, "success getting users after getting chapters");

                            for(QueryDocumentSnapshot chapterDoc : queryChapterSnapshots) {
                                if (chapterDoc.get("name").toString().equals(findChapterText.getText().toString())) {
                                    String id = chapterDoc.getId();
                                    Chapter chapter = chapterDoc.toObject(Chapter.class);
                                    User user = new User(fuser.getDisplayName(), chapter, fuser.getEmail());
                                    chapter.addMember(user);
                                    db.collection("chapters").document(id).set(chapter, SetOptions.merge());
                                    db.collection("users").document(fuser.getUid()).set(user, SetOptions.merge());
                                }
                            }
                            //TODO check if chapter doesnt exist
//                            Toast.makeText(SignupActivity.this, "Chapter doesn't exist", Toast.LENGTH_LONG);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "failed getting users after getting chapters");
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "failed getting chapter");
                }
            });
            sendToNextPage();
        } else if(!nameEditTxt.getText().toString().matches("")) {
            Log.d(TAG, "bottom field: ");
            Chapter chapter = new Chapter(nameEditTxt.getText().toString());
            User user = new User(fuser.getDisplayName(), chapter, fuser.getEmail());
            user.setUserType(UserType.ADVISOR);
            chapter.addMember(user);

            db.collection("users").document(fuser.getUid()).set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "adding user success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "adding user failed");
                }
            });

            //TODO check if chapter already exists

            db.collection("chapters").document().set(chapter).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "new chapter successfully added");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "new chapter adding failed");
                }
            });

            sendToNextPage();
        }
    }

    private void sendToNextPage() {
        Log.d(TAG, "Update UI");
        fuser = auth.getCurrentUser();
        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
