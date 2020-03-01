package com.hhsfbla.mad.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.adapters.UserAdapter;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;
import com.hhsfbla.mad.dialogs.DeleteEventDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Represents a page where users can view further details about an event such as who is attending and join or leave the event
 * Officers and advisors also have the abitlity to share events to facebook, delete events, or edit existing events
 */
public class EventPageActivity extends AppCompatActivity implements DeleteEventDialog.DeleteEventDialogListener, UserAdapter.OnItemClickListener {

    private StorageReference storageReference;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private LoginManager manager;
    private TextView title, date, time, location, desc, link, memberCount;
    private ImageView eventImage;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Button joinButton;
    private Button unJoinButton;
    private ImageButton back;
    private Button shareButton, editButton, deleteButton;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ArrayList<User> users;
    private static final String TAG = "Event Details Page";
    private String id;

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     * with data of the existing event from the database
     *
     * @param savedInstanceState the save state of the activity or page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_event_page);
        setTitle("Event Details");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting...");
        storageReference = FirebaseStorage.getInstance().getReference("images").child("events");
        title = findViewById(R.id.eventTitleDetail);
        date = findViewById(R.id.eventDateDetail);
        time = findViewById(R.id.eventTimeDetail);
        location = findViewById(R.id.eventLocationDetail);
        desc = findViewById(R.id.eventDescriptionDetail);
        link = findViewById(R.id.eventLinkDetail);
        memberCount = findViewById(R.id.eventUserCount);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        joinButton = findViewById(R.id.joinButton);
        unJoinButton = findViewById(R.id.unJoinButton);
        shareButton = findViewById(R.id.shareButton);
        editButton = findViewById(R.id.editButton);
        back = findViewById(R.id.doneBtn4);
        deleteButton = findViewById(R.id.deleteEvent);
        eventImage = findViewById(R.id.eventPicDetail);
        recyclerView = findViewById(R.id.attendeeRecylerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        users = new ArrayList<>();
        adapter = new UserAdapter(users, this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        //this loginManager helps you eliminate adding a LoginButton to your UI
        manager = LoginManager.getInstance();
        id = getIntent().getStringExtra("EVENT_ID");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventPageActivity.this, HomeActivity.class));
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference userdoc = db.collection("users").document(user.getUid());
                userdoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot userSnap) {
                        userdoc.update("myEvents", FieldValue.arrayUnion(id));
                        db.collection("chapters").document(userSnap.toObject(User.class).getChapter()).collection("events").document(id).update("attendees", FieldValue.arrayUnion(user.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                                startActivity(getIntent());
                            }
                        });
                    }
                });

            }
        });

        unJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference userdoc = db.collection("users").document(user.getUid());
                userdoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot userSnap) {
                        userdoc.update("myEvents", FieldValue.arrayRemove(id));
                        db.collection("chapters").document(userSnap.toObject(User.class).getChapter()).collection("events").document(id).update("attendees", FieldValue.arrayRemove(user.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                                startActivity(getIntent());
                            }
                        });
                    }
                });

            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventPageActivity.this, EditEventActivity.class);
                intent.putExtra("EVENT_ID", id);
                startActivity(intent);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder().build();
                    shareDialog.show(linkContent);
                }
            }
        });

        initPage();
    }

    private void initPage() {
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if (currentUser.getUserType() == UserType.MEMBER) {
                    editButton.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.GONE);
                }
                db.collection("chapters").document(currentUser.getChapter()).collection("events").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        final ChapterEvent event = snapshot.toObject(ChapterEvent.class);
                        if (event.getAttendees().contains(user.getUid())) {
                            joinButton.setVisibility(View.GONE);
                            unJoinButton.setVisibility(View.VISIBLE);
                        } else if (event.getMemberLimit() == ChapterEvent.NO_LIMIT || event.getAttendees().size() < event.getMemberLimit()) {
                            joinButton.setVisibility(View.VISIBLE);
                            unJoinButton.setVisibility(View.GONE);
                        }
                        //set event details
                        title.setText(event.getName());
                        date.setText(event.getDate());
                        time.setText(event.getTime());
                        location.setText(event.getLocation());
                        desc.setText(event.getDescription());

                        if (event.getFacebookLink() == null || event.getFacebookLink().equals(""))
                            link.setVisibility(View.GONE);
                        else {
                            link.setText(event.getFacebookLink());
                            link.setText(Html.fromHtml("<a href='" + link.getText().toString() + "'>Click here for more information</a>"));
                            link.setMovementMethod(LinkMovementMethod.getInstance());
                        }

                        if (event.getPic() != null && event.getPic() != "") {
                            Picasso.get().load(Uri.parse(event.getPic())).fit().centerCrop().into(eventImage);
                        } else {
                            eventImage.setVisibility(View.GONE);
                        }

                        db.collection("users").whereArrayContains("myEvents", id).orderBy("name").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                users.addAll(queryDocumentSnapshots.toObjects(User.class));
                                adapter.setUsers(users);
                                if (event.getMemberLimit() == ChapterEvent.NO_LIMIT) {
                                    memberCount.setText(users.size() + "");
                                } else {
                                    memberCount.setText(users.size() + "/" + event.getMemberLimit());
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                        return;
                    }
                });
            }
        });
    }

    /**
     * Deletes the image of the event from the storage, as well as the event itself from the database
     */
    public void deleteInDB() {
        progressDialog.show();
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final User user = documentSnapshot.toObject(User.class);
                db.collection("chapters").document(user.getChapter()).collection("events").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot chapterEvent) {
                        final ChapterEvent event = chapterEvent.toObject(ChapterEvent.class);
                        db.collection("chapters").document(user.getChapter()).collection("events").document(id).delete();
                        db.collection("users").whereArrayContains("myEvents", id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                    db.collection("users").document(snap.getId()).update("myEvents", FieldValue.arrayRemove(snap.getId()));
                                }
                                progressDialog.dismiss();
                                if (!event.getPic().equals("")) {
                                    storageReference.child(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(EventPageActivity.this, HomeActivity.class));
                                        }
                                    });
                                } else {
                                    startActivity(new Intent(EventPageActivity.this, HomeActivity.class));
                                }
                            }
                        });
                    }
                });
            }
        });

    }

    private void openDialog() {
        DeleteEventDialog dialog = new DeleteEventDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    /**
     * This method gets called after an action to get data from the user
     * handles callback for facebook sharing
     *
     * @param requestCode the request code of the request
     * @param resultCode  a code representing the state of the result of the action
     * @param data        the data gained from the activity
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Gets the confirmation of whether or not to delete the event from the dialog and acts accordingly
     *
     * @param confirm whether or not to delete the event
     */
    @Override
    public void sendConfirmation(boolean confirm) {
        if (confirm) {
            deleteInDB();
        }
    }

    /**
     * Describes what to do when a user from the attendees recycler view is clicked, in this case nothing is necessary
     *
     * @param snapshot the object pulled from Firebase Firestore, formatted as a DocumentSnapshot
     * @param v        the View that will contain the click action
     * @param position the numbered position of snapshot in the full item list
     */
    @Override
    public void onItemClick(DocumentSnapshot snapshot, View v, int position) {

    }
}