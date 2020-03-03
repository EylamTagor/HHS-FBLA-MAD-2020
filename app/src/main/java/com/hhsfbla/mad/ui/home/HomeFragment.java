package com.hhsfbla.mad.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.AddEventActivity;
import com.hhsfbla.mad.activities.EventPageActivity;
import com.hhsfbla.mad.adapters.EventAdapter;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a fragment that displays all of the user's chapter's events with some details
 */
public class HomeFragment extends Fragment implements EventAdapter.OnItemClickListener {

    private TextView noEventsYet;
    private FloatingActionButton fab;
    private RecyclerView eventRecyclerView;
    private EventAdapter adapter;

    private List<ChapterEvent> events;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "fraghome";

    /**
     * Creates and inflates a new HomeFragment with the following parameters
     *
     * @param inflater           to inflate the fragment
     * @param container          ViewGroup into which the fragment is inflated
     * @param savedInstanceState used to save activity regarding this fragment
     * @return the inflated fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        noEventsYet = root.findViewById(R.id.noEventsYet);
        fab = root.findViewById(R.id.fab);
        eventRecyclerView = root.findViewById(R.id.eventFeed);
        eventRecyclerView.setHasFixedSize(true);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        events = new ArrayList<>();
        adapter = new EventAdapter(events, root.getContext());
        adapter.setOnItemClickListener(this);
        eventRecyclerView.setAdapter(adapter);

        initRecyclerView();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(root.getContext(), AddEventActivity.class));
            }
        });
        return root;
    }

    /**
     * Initializes the recyclerview with all the chapter's events, ordered by date from most recent to least recent
     */
    public void initRecyclerView() {
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if (currentUser.getUserType() != UserType.MEMBER) {
                    fab.setVisibility(View.VISIBLE);
                }
                db.collection("chapters").document(currentUser.getChapter()).collection("events").orderBy("priority", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        events = queryDocumentSnapshots.toObjects(ChapterEvent.class);
                        if (events.isEmpty()) {
                            noEventsYet.setVisibility(View.VISIBLE);
                        } else {
                            noEventsYet.setVisibility(View.INVISIBLE);
                        }
                        adapter.setEvents(events);
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }

    /**
     * Handles any clicking action done inside this fragment
     *
     * @param snapshot the object pulled from Firebase Firestore, formatted as a DocumentSnapshot
     * @param position the numbered position of snapshot in the full item list
     */
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Intent intent = new Intent(getContext(), EventPageActivity.class);
        intent.putExtra("EVENT_ID", snapshot.getId());
        getContext().startActivity(intent);
    }
}