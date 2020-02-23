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

public class SignupActivity extends AppCompatActivity {
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
}