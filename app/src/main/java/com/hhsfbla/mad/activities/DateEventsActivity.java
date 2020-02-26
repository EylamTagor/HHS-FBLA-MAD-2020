package com.hhsfbla.mad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.adapters.EventAdapter;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a calendar of the year that shows users which events are on which days
 */
public class DateEventsActivity extends AppCompatActivity implements EventAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<ChapterEvent> events;
    private TextView noEvents;
    private String date;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     * with data of the events from the database
     * @param savedInstanceState the save state of the activity or page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_events);

        String year = getIntent().getStringExtra("year");
        String month = getIntent().getStringExtra("month");
        String day = getIntent().getStringExtra("day");

        final StringBuffer tempDate = new StringBuffer();
        tempDate.append(((month.length() == 1) ? "0" + month : month) + "/");
        tempDate.append(((day.length() == 1) ? "0" + day : day) + "/");
        tempDate.append(year);

        date = tempDate.toString();

        setTitle("Events on " + date);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        noEvents = findViewById(R.id.noEventsForDate);

        recyclerView = findViewById(R.id.dateEventsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        events = new ArrayList<>();
        adapter = new EventAdapter(events, this);
        adapter.setOnItemClickListener(this);

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);

                db.collection("chapters").document(currentUser.getChapter()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments())
                            if (snapshot.toObject(ChapterEvent.class).getDate().equals(date))
                                events.add(snapshot.toObject(ChapterEvent.class));

                        if (events.size() == 0) {
                            noEvents.setVisibility(View.VISIBLE);
                            return;
                        } else
                            noEvents.setVisibility(View.INVISIBLE);
                        adapter.setEvents(events);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }

    /**
     * Sends the user to a page with the details of the event that is t=on the day they clicked
     *
     * @param name the object pulled from Firebase Firestore, formatted as a DocumentSnapshot
     * @param position the numbered position of snapshot in the full item list
     */
    @Override
    public void onItemClick(String name, int position) {
        Intent intent = new Intent(DateEventsActivity.this, EventPageActivity.class);
        intent.putExtra("EVENT_POSITION", name);
        startActivity(intent);
    }
}
