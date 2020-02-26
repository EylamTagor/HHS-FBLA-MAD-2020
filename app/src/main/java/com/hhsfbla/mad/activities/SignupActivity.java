package com.hhsfbla.mad.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.hhsfbla.mad.adapters.ChapterAdapter;
import com.hhsfbla.mad.data.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SignupActivity extends AppCompatActivity implements ChapterAdapter.OnItemClickListener{
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ChapterAdapter adapter;
    private List<Chapter> chapterList;
    private StorageTask uploadTask;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser fuser;
    private Button createNewChapter;
    private static final String TAG = "signupactivity";
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("User Setup");
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fuser = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("images").child("pfps");
        createNewChapter = findViewById(R.id.createNewChapter);
        searchView = findViewById(R.id.searchChapters);
        recyclerView = findViewById(R.id.chapterList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chapterList = new ArrayList<>();
        adapter = new ChapterAdapter(getApplicationContext(), chapterList);
        adapter.setOnItemCLickListener(this);
        recyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        db.collection("chapters").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                chapterList = queryDocumentSnapshots.toObjects(Chapter.class);
                adapter.setChapterList(chapterList);
                adapter.notifyDataSetChanged();

            }
        });

        createNewChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, SetupActivity.class));
            }
        });

    }

    private void updateUser(final DocumentSnapshot snapshot, final Uri uri) {
        db.collection("chapters").document(snapshot.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot queryDocumentSnapshots) {
//                                db.collection("users").document(fuser.getUid()).set(new User(fuser.getDisplayName(), snapshot.getId(), fuser.getEmail()));
                db.collection("users").document(fuser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if(user == null) {
                            user = new User(fuser.getDisplayName(), snapshot.getId(), fuser.getEmail());
                            if(uri != null){
                                user.setPic(uri.toString());
                            }
                            db.collection("users").document(fuser.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    db.collection("chapters").document(snapshot.getId()).update("users", FieldValue.arrayUnion(fuser.getUid()));
//                                                    progressDialog.dismiss();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                    return;
                                }
                            });
                        } else {
                            db.collection("users").document(fuser.getUid()).update("chapter", snapshot.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    db.collection("chapters").document(snapshot.getId()).update("users", FieldValue.arrayUnion(fuser.getUid()));
//                                                    progressDialog.dismiss();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                    return;
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    private void uploadFile(final DocumentSnapshot snapshot) {
        if(fuser.getPhotoUrl() != null) {
//            progressDialog.setMessage("Uploading...");
//            progressDialog.show();
            final StorageReference fileRef = storageReference.child(fuser.getUid());
            uploadTask = fileRef.putFile(fuser.getPhotoUrl())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    updateUser(snapshot, uri);
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
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_LONG).show();
            updateUser(snapshot, null);
        }
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        updateUser(snapshot, fuser.getPhotoUrl());
    }
}