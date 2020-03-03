package com.hhsfbla.mad.ui.calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
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
import com.hhsfbla.mad.activities.DateEventsActivity;
import com.hhsfbla.mad.activities.EventPageActivity;
import com.hhsfbla.mad.adapters.EventAdapter;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a fragment consisting of a CalendarView with an option to pick specific dates to view events
 */
public class CalendarFragment extends Fragment implements EventAdapter.OnItemClickListener {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser fuser;

    private CalendarView calendar;
    private RecyclerView recyclerView;
    private EventAdapter adapter;

    private TextView noEventsYet;

    private List<ChapterEvent> events;

    private String date;

    /**
     * Creates and inflates a new CalendarFragment with the following parameters
     *
     * @param inflater           to inflate the fragment
     * @param container          ViewGroup into which the fragment is inflated
     * @param savedInstanceState used to save activity regarding this fragment
     * @return the inflated fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        getActivity().setTitle("Calendar");

        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        fuser = auth.getCurrentUser();

        recyclerView = root.findViewById(R.id.calendarEventRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        events = new ArrayList<>();
        adapter = new EventAdapter(events, root.getContext());
        adapter.setOnItemClickListener(this);

        noEventsYet = root.findViewById(R.id.noEventsYet2);

        calendar = root.findViewById(R.id.calendar);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        date = sdf.format(new Date(calendar.getDate()));
        initRecyclerView();

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String y = year + "";
                String m = (month + 1) + "";
                String d = dayOfMonth + "";

                final StringBuffer tempDate = new StringBuffer();
                tempDate.append(((m.length() == 1) ? "0" + m : m) + "/");
                tempDate.append(((d.length() == 1) ? "0" + d : d) + "/");
                tempDate.append(y);

                date = tempDate.toString();

                initRecyclerView();
            }
        });

        return root;
    }

    private void initRecyclerView() {
        events.clear();
        adapter.setEvents(events);
        recyclerView.setAdapter(adapter);

        if (date != null && !date.equals("")) {
            db.collection("users").document(fuser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);

                    db.collection("chapters").document(currentUser.getChapter()).collection("events").whereEqualTo("date", date).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            events.addAll(queryDocumentSnapshots.toObjects(ChapterEvent.class));

                            if (events.size() == 0) {
                                noEventsYet.setVisibility(View.VISIBLE);
                                return;
                            } else
                                noEventsYet.setVisibility(View.INVISIBLE);
                            adapter.setEvents(events);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Intent intent = new Intent(getContext(), EventPageActivity.class);
        intent.putExtra("EVENT_ID", snapshot.getId());
        intent.putExtra("FROM_FRAGMENT", "Calendar");
        getContext().startActivity(intent);
    }
}