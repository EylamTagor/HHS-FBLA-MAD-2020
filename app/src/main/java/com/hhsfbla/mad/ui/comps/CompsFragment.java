package com.hhsfbla.mad.ui.comps;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.adapters.CompsAdapter;
import com.hhsfbla.mad.adapters.EventAdapter;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.CompType;
import com.hhsfbla.mad.data.Competition;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.ui.myevents.MyEventsViewModel;

import java.util.ArrayList;
import java.util.List;

public class CompsFragment extends Fragment {

    private CompsViewModel mViewModel;
    private RecyclerView eventRecyclerView;
    private CompsAdapter adapter;

    private List<Competition> comps;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "COMPS";

    private static final Competition[] competitions = {
            new Competition("3-D Animation", "", CompType.TECH, 0),
            new Competition("Accounting 1", "", CompType.WRITTEN, 0),
            new Competition("Accounting 2", "", CompType.WRITTEN, 0),
            new Competition("Advertising", "", CompType.WRITTEN, 0),
            new Competition("Agribusiness", "", CompType.WRITTEN, 0),
            new Competition("American Enterprise Project", "", CompType.PROJECT, 0),
            new Competition("Banking and Financial Systems", "", CompType.CASESTUDY, 0),
            new Competition("Broadcast Journalism", "", CompType.SPEAKING, 0),
            new Competition("Business Calculations", "", CompType.WRITTEN, 0),
            new Competition("Business Communication", "", CompType.WRITTEN, 0),
            new Competition("Business Ethics", "", CompType.SPEAKING, 0),
            new Competition("Business Financial Plan", "", CompType.SPEAKING, 0),
            new Competition("Business Law", "", CompType.WRITTEN, 0),
            new Competition("Business Plan", "", CompType.SPEAKING, 0),
            new Competition("Client Service", "", CompType.SPEAKING, 0),
            new Competition("Coding and Programming", "", CompType.TECH, 0),
            new Competition("Community Service Project", "", CompType.PROJECT, 0),
            new Competition("Computer Applications", "", CompType.PRODUCTION, 0),
            new Competition("Computer Game and Simulation Programming", "", CompType.TECH, 0),
            new Competition("Computer Problem Solving", "", CompType.WRITTEN, 0),
            new Competition("Cyber Security", "", CompType.WRITTEN, 0),
            new Competition("Database Design and Applications", "", CompType.PRODUCTION, 0),
            new Competition("Digital Video Production", "", CompType.TECH, 0),
            new Competition("E-Business", "", CompType.TECH, 0),
            new Competition("Economics", "", CompType.WRITTEN, 0),
            new Competition("Electronic Career Portfolio", "", CompType.TECH, 0),
            new Competition("Emerging Business Issues", "", CompType.SPEAKING, 0),
            new Competition("Entrepreneurship", "", CompType.CASESTUDY, 0),
            new Competition("Future Business Leader", "", CompType.SPEAKING, 0),
            new Competition("Global Business", "", CompType.CASESTUDY, 0),
            new Competition("Graphic Design", "", CompType.TECH, 0),
            new Competition("Health Care Administration", "", CompType.WRITTEN, 0),
            new Competition("Help Desk", "", CompType.CASESTUDY, 0),
            new Competition("Hospitality Management", "", CompType.CASESTUDY, 0),
            new Competition("Impromptu Speaking", "", CompType.SPEAKING, 0),
            new Competition("Insurance and Risk Management", "", CompType.WRITTEN, 0),
            new Competition("Introduction to Business", "", CompType.WRITTEN, 0),
            new Competition("Introduction to Business Communication", "", CompType.WRITTEN, 0),
            new Competition("Introduction to Business Presentation", "", CompType.SPEAKING, 0),
            new Competition("Introduction to Business Procedures", "", CompType.WRITTEN, 0),
            new Competition("Introduction to FBLA", "", CompType.WRITTEN, 0),
            new Competition("Introduction to Financial Math", "", CompType.WRITTEN, 0),
            new Competition("Introduction to Information Technology", "", CompType.WRITTEN, 0),
            new Competition("Introduction to Parliamentary Procedure", "", CompType.WRITTEN, 0),
            new Competition("Introduction to Public Speaking", "", CompType.SPEAKING, 0),
            new Competition("Job Interview", "", CompType.SPEAKING, 0),
            new Competition("Journalism", "", CompType.WRITTEN, 0),
            new Competition("LifeSmarts", "", CompType.WRITTEN, 0),
            new Competition("Local Chapter Annual Business Report", "", CompType.SPEAKING, 0),
            new Competition("Management Decision Making", "", CompType.CASESTUDY, 0),
            new Competition("Management Information Systems", "", CompType.CASESTUDY, 0),
            new Competition("Marketing", "", CompType.CASESTUDY, 0),
            new Competition("Mobile Application Development", "", CompType.TECH, 0),
            new Competition("Network Design", "", CompType.CASESTUDY, 0),
            new Competition("Networking Concepts", "", CompType.WRITTEN, 0),
            new Competition("Organizational Leadership", "", CompType.WRITTEN, 0),
            new Competition("Parliamentary Procedure", "", CompType.SPEAKING, 0),
            new Competition("Partnership with Business Project", "", CompType.PROJECT, 0),
            new Competition("Personal Finance", "", CompType.WRITTEN, 0),
            new Competition("Political Science", "", CompType.WRITTEN, 0),
            new Competition("Public Service Announcement", "", CompType.SPEAKING, 0),
            new Competition("Public Speaking", "", CompType.SPEAKING, 0),
            new Competition("Publication Design", "", CompType.TECH, 0),
            new Competition("Sales Presentation", "", CompType.SPEAKING, 0),
            new Competition("Securities and Investments", "", CompType.WRITTEN, 0),
            new Competition("Social Media Campaign", "", CompType.SPEAKING, 0),
            new Competition("Sports and Entertainment Management", "", CompType.CASESTUDY, 0),
            new Competition("Spreadsheet Applications", "", CompType.PRODUCTION, 0),
            new Competition("Virtual Business Finance Challenge", "", CompType.SPEAKING, 0),
            new Competition("Virtual Business Management Challenge", "", CompType.SPEAKING, 0),
            new Competition("Website Design", "", CompType.TECH, 0),
            new Competition("Word Processing", "", CompType.PRODUCTION, 0),

    };

    public static CompsFragment newInstance() {
        return new CompsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_comps, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        eventRecyclerView = root.findViewById(R.id.comps);
        eventRecyclerView.setHasFixedSize(true);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        comps = new ArrayList<Competition>();
        for(Competition comp : competitions) {
            comps.add(comp);
        }
        adapter = new CompsAdapter(comps, root.getContext());
        eventRecyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CompsViewModel.class);
        // TODO: Use the ViewModel
    }

}
