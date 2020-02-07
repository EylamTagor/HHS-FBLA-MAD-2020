package com.hhsfbla.mad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.adapters.ChapterAdapter;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ChapterAdapter adapter;
    private List<Chapter> chapterList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser fuser;
    private boolean failed = true;

    private static final String TAG = "signupactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("User Setup");
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fuser = auth.getCurrentUser();

        searchView = findViewById(R.id.searchChapters);
        recyclerView = findViewById(R.id.chapterList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chapterList = new ArrayList<>();
        adapter = new ChapterAdapter(this, chapterList);

        db.collection("chapters").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    chapterList.add(queryDocumentSnapshots.getDocuments().get(i).toObject(Chapter.class));
                }
                Log.d(TAG, "lol");
                Log.d(TAG, chapterList.toString() + "here");
                adapter.setChapterList(chapterList);
                recyclerView.setAdapter(adapter);
            }
        });


    }

    public void updateUI() {

    }

    private void sendToNextPage() {
        if (!failed) {
            Log.d(TAG, "Update UI");
            fuser = auth.getCurrentUser();
            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
