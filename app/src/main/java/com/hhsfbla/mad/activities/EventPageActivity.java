package com.hhsfbla.mad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
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
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;
import com.squareup.picasso.Target;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EventPageActivity extends AppCompatActivity {

    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private LoginManager manager;
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class)) {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    private TextView title, date, time, location, desc, link;
    private ImageView dateIcon, timeIcon, locationIcon, eventImage;
    private ChapterEvent mainEvent;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Button joinButton;
    private Button unJoinButton;
    private Button shareButton, editButton;
    private static final String TAG = "Event Details Page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_event_page);
        setTitle("Event Details");

        title = findViewById(R.id.eventTitleDetail);
        date = findViewById(R.id.eventDateDetail);
        time = findViewById(R.id.eventTimeDetail);
        location = findViewById(R.id.eventLocationDetail);
        desc = findViewById(R.id.eventDescriptionDetail);
        link = findViewById(R.id.eventLinkDetail);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        mainEvent = new ChapterEvent();
        joinButton = findViewById(R.id.joinButton);
        unJoinButton = findViewById(R.id.unJoinButton);
        shareButton = findViewById(R.id.shareButton);
        editButton = findViewById(R.id.editButton);
        eventImage = findViewById(R.id.eventPicDetail);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                Log.d(TAG, error.toString());


            }
        });

        //this loginManager helps you eliminate adding a LoginButton to your UI
        manager = LoginManager.getInstance();

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if(currentUser.getUserType() == UserType.MEMBER) {
                    editButton.setVisibility(View.GONE);
                }
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

                                //set event details
                                title.setText(mainEvent.getName());
                                date.setText(mainEvent.getDate());
                                time.setText(mainEvent.getTime());
                                location.setText(mainEvent.getLocation());
                                desc.setText(mainEvent.getDescription());
                                link.setText(mainEvent.getFacebookLink());
                                link.setText(Html.fromHtml("<a href='" + link.getText().toString() + "'>Click Here For More Information</a>"));
                                link.setMovementMethod(LinkMovementMethod.getInstance());
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
