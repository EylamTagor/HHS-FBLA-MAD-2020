package com.hhsfbla.mad.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.HomeActivity;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.adapters.EventAdapter;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView chapter_nameTxtView;
    private ImageView imageView;

    private RecyclerView eventRecyclerView;
    private RecyclerView.Adapter adapter;

    private List<ChapterEvent> events;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        eventRecyclerView = root.findViewById(R.id.eventFeed);
        eventRecyclerView.setHasFixedSize(true);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        events = HomeActivity.getEventList();

        adapter = new EventAdapter(events, getContext());
        eventRecyclerView.setAdapter(adapter);

        return root;
    }
}