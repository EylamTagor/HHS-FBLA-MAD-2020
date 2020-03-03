package com.hhsfbla.mad.activities;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
import com.hhsfbla.mad.adapters.ChapterAdapter;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a class where users can choose their chapter or opt to create a new one
 */
public class SignupActivity extends AppCompatActivity implements ChapterAdapter.OnItemClickListener {
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

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     * with data of the existing event from the database
     *
     * @param savedInstanceState the save state of the activity or page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Find a Chapter");
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("images").child("events");
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

        createNewChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, SetupActivity.class));
            }
        });

        db.collection("chapters").orderBy("name").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                chapterList = queryDocumentSnapshots.toObjects(Chapter.class);
                adapter.setChapterList(chapterList);
                adapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * Sets the users chapter to the parameter id, sets their status to member, and removes them from all their previous
     * chapters events, if they were signed up. Also empties their event and competion signups
     *
     * @param id the id of the chapter selected
     */
    public void changeChapter(final String id) {
        final DocumentReference userRef = db.collection("users").document(fuser.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot userSnap) {
                Log.d(TAG, "onSuccess: hello");
                final DocumentReference chapterRef = db.collection("chapters").document(userSnap.toObject(User.class).getChapter());
                chapterRef.update("users", FieldValue.arrayRemove(fuser.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: jello");
                        chapterRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                                Log.d(TAG, "onSuccess: bello");
                                if(snapshot.toObject(Chapter.class).getUsers().size() == 0) {
                                    deleteEventImages(chapterRef.collection("events"));
                                    chapterRef.delete();
                                    userRef.update("myEvents", new ArrayList<ChapterEvent>());
                                } else {
                                    if(snapshot.toObject(Chapter.class).getUsers().size() == 1) {
                                        db.collection("users").document(snapshot.toObject(Chapter.class).getUsers().get(0)).update("userType", UserType.ADVISOR);
                                    }
                                    removeFromEvents(chapterRef);
                                }
                                updateUser(id);
                            }
                        });
                    }
                });
            }
        });

    }

    private void deleteEventImages(CollectionReference ref) {
        Log.d(TAG, "deleteEventImages: deleteImages");
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot snap : queryDocumentSnapshots) {
                    if(snap.toObject(ChapterEvent.class).getPic() != null && !snap.toObject(ChapterEvent.class).getPic().equalsIgnoreCase(""))
                        storageReference.child(snap.getId()).delete();
                }
            }
        });
    }

    private void removeFromEvents(final DocumentReference chapterRef) {
        Log.d(TAG, "removeFromEvents: remove");
        chapterRef.collection("events").whereArrayContains("attendees", fuser.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                    chapterRef.collection("events").document(snap.getId()).update("attendees", FieldValue.arrayRemove(fuser.getUid()));
                }
            }
        });
    }

    private void updateUser(final String id) {
        Log.d(TAG, "updateUser: update");
        final DocumentReference userRef = db.collection("users").document(fuser.getUid());
        userRef.update("chapter", id).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                userRef.update("myEvents", new ArrayList<String>()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userRef.update("userType", UserType.MEMBER).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userRef.update("comps", new ArrayList<String>()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Adds the user to the database when a chapter is clicked
     *
     * @param snapshot the chapter object pulled from Firebase Firestore, formatted as a DocumentSnapshot
     * @param position the numbered position of snapshot in the full chapter list
     */
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        final String id = snapshot.getId();
        db.collection("users").document(fuser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                User user = snapshot.toObject(User.class);
                if (user.getChapter().equals("")) {
                    db.collection("users").document(fuser.getUid()).update("chapter", id).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.collection("chapters").document(id).update("users", FieldValue.arrayUnion(fuser.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                }
                            });
                        }
                    });
                } else if (user.getChapter().equals(id)) {
                    startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                } else {
                    changeChapter(id);
                }
            }
        });
    }
}