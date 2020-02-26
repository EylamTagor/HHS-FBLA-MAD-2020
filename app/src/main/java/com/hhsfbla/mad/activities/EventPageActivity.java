package com.hhsfbla.mad.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
import java.util.Arrays;
import java.util.List;

public class EventPageActivity extends AppCompatActivity implements DeleteEventDialog.DeleteEventDialogListener, UserAdapter.OnItemClickListener {

//    private StorageReference storageReference;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private LoginManager manager;
    private TextView title, date, time, location, desc, link, memberCount;
//    private ImageView eventImage;
    private ChapterEvent mainEvent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_event_page);
        setTitle("Event Details");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting...");
//        storageReference = FirebaseStorage.getInstance().getReference("images").child("events");
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
        Log.d("hello", user.getPhotoUrl().toString());
        mainEvent = new ChapterEvent();
        joinButton = findViewById(R.id.joinButton);
        unJoinButton = findViewById(R.id.unJoinButton);
        shareButton = findViewById(R.id.shareButton);
        editButton = findViewById(R.id.editButton);
        back = findViewById(R.id.doneBtn4);
        deleteButton = findViewById(R.id.deleteEvent);
//        eventImage = findViewById(R.id.eventPicDetail);
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

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if(currentUser.getUserType() == UserType.MEMBER) {
                    editButton.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.GONE);
                }
                db.collection("chapters").document(currentUser.getChapter()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<ChapterEvent> events = queryDocumentSnapshots.toObjects(ChapterEvent.class);
                        String name = getIntent().getStringExtra("EVENT_POSITION");
                        for(final ChapterEvent event : events) {
                            if(event.getName().equals(name)) {
                                if(event.getAttendees().contains(user.getUid())) {
                                    joinButton.setVisibility(View.GONE);
                                    unJoinButton.setVisibility(View.VISIBLE);
                                } else {
                                    joinButton.setVisibility(View.VISIBLE);
                                    unJoinButton.setVisibility(View.GONE);
                                }
                                mainEvent = event;

                                //set event details
                                title.setText(mainEvent.getName());
                                date.setText(mainEvent.getDate());
                                time.setText(mainEvent.getTime());
                                location.setText(mainEvent.getLocation());
                                desc.setText(mainEvent.getDescription());
                                link.setText(mainEvent.getFacebookLink());
                                link.setText(Html.fromHtml("<a href='" + link.getText().toString() + "'>Click here for more information</a>"));
                                link.setMovementMethod(LinkMovementMethod.getInstance());
//                                if(mainEvent.getPic() != null && mainEvent.getPic() != "") {
//                                    Log.d(TAG, mainEvent.getPic());
//                                    Picasso.get().load(Uri.parse(mainEvent.getPic())).fit().centerCrop().into(eventImage);
//                                } else
//                                    eventImage.setVisibility(View.GONE);

                                db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(DocumentSnapshot user : queryDocumentSnapshots) {
                                            if(event.getAttendees().contains(user.getId())) {
                                                users.add(user.toObject(User.class));
                                            }
                                        }
                                        adapter.setUsers(users);
                                        memberCount.setText(users.size() + "");
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                return;
                            }
                        }
                    }
                });
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventPageActivity.this, HomeActivity.class));
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
        Log.d(TAG, "HELLOOO ");
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
                String name = getIntent().getStringExtra("EVENT_POSITION");
                intent.putExtra("EVENT_NAME", name);
                startActivity(intent);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android")).setImageUrl(Uri.parse("https://marvelcinematicuniverse.fandom.com/wiki/Spider-Man?file=Spider-Man_FFH_Profile.jpg"))
                            .build();
                    SharePhoto p = new SharePhoto.Builder().setImageUrl(Uri.parse("https://marvelcinematicuniverse.fandom.com/wiki/Spider-Man?file=Spider-Man_FFH_Profile.jpg")).build();
                    SharePhotoContent heh = new SharePhotoContent.Builder().addPhoto(p).setContentUrl(Uri.parse("https://marvelcinematicuniverse.fandom.com/wiki/Spider-Man?file=Spider-Man_FFH_Profile.jpg")).build();
                    shareDialog.show(linkContent);
                }
            }
        });
    }

    private void deleteInDB() {
        Log.d(TAG, "hello");
        progressDialog.show();
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final User user = documentSnapshot.toObject(User.class);
                db.collection("chapters").document(user.getChapter()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<ChapterEvent> events = queryDocumentSnapshots.toObjects(ChapterEvent.class);
                        String name = getIntent().getStringExtra("EVENT_POSITION");
                        for(final DocumentSnapshot snap : queryDocumentSnapshots) {
                            ChapterEvent event = snap.toObject(ChapterEvent.class);

                            if(event.getName().equals(name)) {
//                                if(event.getPic() == null || event.getPic() == "") {
                                    Log.d(TAG, "event has no pic");
                                    db.collection("chapters").document(user.getChapter()).collection("events").document(snap.getId()).delete();
                                    Log.d(TAG, "deleting in db");
                                    db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(DocumentSnapshot snap : queryDocumentSnapshots) {
                                                if(snap.toObject(User.class).getMyEvents().contains(snap.getId())) {
                                                    Log.d(TAG, "hithere");
                                                    db.collection("users").document(snap.getId()).update("myEvents", FieldValue.arrayRemove(snap.getId()));
                                                }
                                            }
                                            progressDialog.dismiss();
                                            startActivity(new Intent(EventPageActivity.this, HomeActivity.class));
                                        }
                                    });
//                                    return;
//                                }
//                                StorageReference storageRef = storageReference.child(snap.getId());
//                                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Log.d(TAG, "deleted in storage");
//                                        db.collection("chapters").document(user.getChapter()).collection("events").document(snap.getId()).delete();
//                                        Log.d(TAG, "deleting in db");
//                                        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                            @Override
//                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                                for(DocumentSnapshot snap : queryDocumentSnapshots) {
//                                                    if(snap.toObject(User.class).getMyEvents().contains(snap.getId())) {
//                                                        Log.d(TAG, "hithere");
//                                                        db.collection("users").document(snap.getId()).update("myEvents", FieldValue.arrayRemove(snap.getId()));
//                                                    }
//                                                }
//                                                progressDialog.dismiss();
//                                                startActivity(new Intent(EventPageActivity.this, HomeActivity.class));
//                                            }
//                                        });
//                                    }
//                                });
                            }
                        }
                    }
                });
            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cR.getType(uri));
    }

    private void facebookPost() {
        final List<String> permissionNeeds = Arrays.asList("publish_pages");
        final Activity lol = this;
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                manager.logInWithPublishPermissions(lol, permissionNeeds);
                Bitmap image = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher);
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(image).setImageUrl(Uri.parse("https://marvelcinematicuniverse.fandom.com/wiki/Spider-Man?file=Spider-Man_FFH_Profile.jpg"))
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)

                        .build();
                ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result)
                    {
                        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel()
                    {
                        Log.d("FACEBOOK_TEST", "share api cancel");
                    }

                    @Override
                    public void onError(FacebookException e)
                    {
                        Log.d("FACEBOOK_TEST", "share api error " + e);
                    }
                });
            }

            @Override
            public void onCancel()
            {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception)
            {
                System.out.println("onError");
            }
        });
    }

    private void openDialog() {
        DeleteEventDialog dialog = new DeleteEventDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void sendConfirmation(boolean confirm) {
        if(confirm) {
            deleteInDB();
        }
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, View v, int position) {

    }
}
