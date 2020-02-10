package com.hhsfbla.mad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.User;

import java.util.List;

public class EventPageActivity extends AppCompatActivity {
    private ChapterEvent mainEvent;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Button joinButton;
    private Button unJoinButton;
    private static final String TAG = "Event Details Page";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        setTitle("Event Details");
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        mainEvent = new ChapterEvent();
        joinButton = findViewById(R.id.joinButton);
        unJoinButton = findViewById(R.id.unJoinButton);

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                db.collection("chapters").document(currentUser.getChapter()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<ChapterEvent> events = queryDocumentSnapshots.toObjects(ChapterEvent.class);
                        String name = getIntent().getStringExtra("EVENT_POSITION");
                        for(ChapterEvent event : events) {
                            if(event.getName().equals(name)) {
                                if(event.getAttendees().contains(user.getUid())) {
                                    joinButton.setVisibility(View.GONE);
                                    unJoinButton.setVisibility(View.VISIBLE);
                                } else {
                                    joinButton.setVisibility(View.VISIBLE);
                                    unJoinButton.setVisibility(View.GONE);
                                }
                                mainEvent = event;
                                return;
                            }
                        }
                    }
                });
            }
        });
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Join onclick");
                final DocumentReference userdoc = db.collection("users").document(user.getUid());
                userdoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot userSnap) {
                        final String name = getIntent().getStringExtra("EVENT_POSITION");
                        db.collection("chapters").document(userSnap.toObject(User.class).getChapter()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(QueryDocumentSnapshot event : queryDocumentSnapshots) {
                                    if(event.get("name").equals(name)) {
                                        Log.d(TAG, event.getId());
                                        Log.d(TAG, event.getId() + "here");
                                        userdoc.update("myEvents", FieldValue.arrayUnion(event.getId()));
                                        Log.d(TAG, "finished");
                                        db.collection("chapters").document(userSnap.toObject(User.class).getChapter()).collection("events").document(event.getId()).update("attendees", FieldValue.arrayUnion(user.getUid()));
                                        finish();
                                        startActivity(getIntent());
                                    }
                                }
                            }
                        });
                    }
                });

            }
        });
        unJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Unjoin onclick");
                final DocumentReference userdoc = db.collection("users").document(user.getUid());
                userdoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot userSnap) {
                        final String name = getIntent().getStringExtra("EVENT_POSITION");
                        db.collection("chapters").document(userSnap.toObject(User.class).getChapter()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(QueryDocumentSnapshot event : queryDocumentSnapshots) {
                                                if(event.get("name").equals(name)) {
                                                    userdoc.update("myEvents", FieldValue.arrayRemove(event.getId()));
                                                    Log.d(TAG, "finished");
                                        db.collection("chapters").document(userSnap.toObject(User.class).getChapter()).collection("events").document(event.getId()).update("attendees", FieldValue.arrayRemove(user.getUid()));
                                                    finish();
                                                    startActivity(getIntent());
                                    }
                                }
                            }
                        });
                    }
                });

            }
        });
    }
}
