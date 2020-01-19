package com.hhsfbla.mad.activities.ui.home;

import android.os.Bundle;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.recyclerview_stuff.EventAdapter;
import com.hhsfbla.mad.recyclerview_stuff.EventItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView eventRecyclerView;
    private RecyclerView.Adapter adapter;

    private List<EventItem> eventItems;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        eventRecyclerView = root.findViewById(R.id.eventFeed);
        eventRecyclerView.setHasFixedSize(true);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            EventItem eventItem = new EventItem("Event " + i, "13/32/20", "25:60pm", "21370 Homestead Road, Cupertino, CA", R.color.colorPrimaryDark);
            eventItems.add(eventItem);
        }

        adapter = new EventAdapter(eventItems, getContext());
        eventRecyclerView.setAdapter(adapter);

        return root;
    }
}