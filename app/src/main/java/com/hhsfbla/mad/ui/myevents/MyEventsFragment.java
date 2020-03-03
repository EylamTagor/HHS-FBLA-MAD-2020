package com.hhsfbla.mad.ui.myevents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.EventPageActivity;
import com.hhsfbla.mad.adapters.EventAdapter;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Represents a fragment that includes all of the events a user is signed up for, in a RecyclerView
 */
public class MyEventsFragment extends Fragment implements EventAdapter.OnItemClickListener {

    private TextView noEventsYet;
    private RecyclerView eventRecyclerView;
    private EventAdapter adapter;

    private List<ChapterEvent> events;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "MYEVENTS";

    /**
     * Creates and inflates a new MyEventsFragment with the following parameters
     *
     * @param inflater           to inflate the fragment
     * @param container          ViewGroup into which the fragment is inflated
     * @param savedInstanceState used to save activity regarding this fragment
     * @return the inflated fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_my_events, container, false);
        getActivity().setTitle("My Events");
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        noEventsYet = root.findViewById(R.id.noSignups);
        eventRecyclerView = root.findViewById(R.id.myEvents);
        eventRecyclerView.setHasFixedSize(true);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        events = new ArrayList<>();
        adapter = new EventAdapter(events, root.getContext());
        adapter.setOnItemClickListener(this);
        eventRecyclerView.setAdapter(adapter);

        initRecyclerView();

        return root;
    }

    private void initRecyclerView() {
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final User currentUser = documentSnapshot.toObject(User.class);
                db.collection("chapters").document(currentUser.getChapter()).collection("events").whereArrayContains("attendees", user.getUid()).orderBy("priority", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        events.addAll(queryDocumentSnapshots.toObjects(ChapterEvent.class));
                        if (events == null || events.size() == 0) {
                            noEventsYet.setVisibility(View.VISIBLE);
                            return;
                        } else {
                            noEventsYet.setVisibility(View.INVISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    /**
     * Handles any action upon successful creation of the host activity
     *
     * @param savedInstanceState used to save activity regarding this fragment
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        intent.putExtra("FROM_FRAGMENT", "MyEvents");
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
    }
}
