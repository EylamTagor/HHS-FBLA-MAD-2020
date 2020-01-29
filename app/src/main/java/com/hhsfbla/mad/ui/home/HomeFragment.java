package com.hhsfbla.mad.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hhsfbla.mad.R;
import com.hhsfbla.mad.recyclerview_stuff.EventAdapter;
import com.hhsfbla.mad.recyclerview_stuff.EventItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView chapter_nameTxtView;
    private ImageView imageView;

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
//        homeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        return root;
    }
}