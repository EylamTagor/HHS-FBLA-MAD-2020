package com.hhsfbla.mad.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.HomeActivity;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.adapters.EventAdapter;
import com.hhsfbla.mad.data.User;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView chapter_nameTxtView;
    private ImageView imageView;
    private TextView noEventsYet;
    private RecyclerView eventRecyclerView;
    private EventAdapter adapter;

    private List<ChapterEvent> events;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "fraghome";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        noEventsYet = root.findViewById(R.id.noEventsYet);
        eventRecyclerView = root.findViewById(R.id.eventFeed);
        eventRecyclerView.setHasFixedSize(true);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(events, root.getContext());

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                db.collection("chapters").document(currentUser.getChapter()).collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        events = queryDocumentSnapshots.toObjects(ChapterEvent.class);
                        Log.d(TAG, "here");
                        Log.d(TAG, events.toString());
                        if (events.isEmpty()) {
                            noEventsYet.setVisibility(View.VISIBLE);
                        } else {
                            noEventsYet.setVisibility(View.INVISIBLE);
                        }
//                        events.add(new ChapterEvent("example", "999", "888", "hello", "details", R.color.colorPrimaryDark));
                        adapter.setEvents(events);
                        eventRecyclerView.setAdapter(adapter);
                    }
                });

            }});
        return root;
    }
}