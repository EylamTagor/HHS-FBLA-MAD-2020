package com.hhsfbla.mad.ui.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.DateEventsActivity;

/**
 * Represents a fragment consisting of a CalendarView with an option to pick specific dates to view events
 */
public class CalendarFragment extends Fragment {

    private FirebaseFirestore db;

    private CalendarView calendar;

    /**
     * Creates and inflates a new CalendarFragment with the following parameters
     *
     * @param inflater to inflate the fragment
     * @param container ViewGroup into which the fragment is inflated
     * @param savedInstanceState used to save activity regarding this fragment
     * @return the inflated fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        getActivity().setTitle("Calendar");

        db = FirebaseFirestore.getInstance();

        calendar = root.findViewById(R.id.calendar);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Intent intent = new Intent(getContext(), DateEventsActivity.class);
                intent.putExtra("year", year + "");
                intent.putExtra("month", (month + 1) + "");
                intent.putExtra("day", dayOfMonth + "");
                startActivity(intent);
            }
        });

        return root;
    }
}
