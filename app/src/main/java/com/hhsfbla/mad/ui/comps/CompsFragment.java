package com.hhsfbla.mad.ui.comps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.adapters.CompsAdapter;
import com.hhsfbla.mad.data.CompType;
import com.hhsfbla.mad.data.Competition;

import java.util.ArrayList;
import java.util.List;

public class CompsFragment extends Fragment {

    private CompsViewModel mViewModel;
    private RecyclerView eventRecyclerView;
    private CompsAdapter adapter;
    private SearchView searchView;
    private List<Competition> comps;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "COMPS";

    public static final Competition[] competitions = {
            new Competition("3-D Animation", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Accounting 1", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Accounting 2", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Advertising", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Agribusiness", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("American Enterprise Project", "", CompType.PROJECT, R.drawable.project_icon),
            new Competition("Banking and Financial Systems", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Broadcast Journalism", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Business Calculations", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Business Communication", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Business Ethics", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Business Financial Plan", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Business Law", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Business Plan", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Client Service", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Coding and Programming", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Community Service Project", "", CompType.PROJECT, R.drawable.project_icon),
            new Competition("Computer Applications", "", CompType.PRODUCTION, R.drawable.production_icon),
            new Competition("Computer Game and Simulation Programming", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Computer Problem Solving", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Cyber Security", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Database Design and Applications", "", CompType.PRODUCTION, R.drawable.production_icon),
            new Competition("Digital Video Production", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("E-Business", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Economics", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Electronic Career Portfolio", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Emerging Business Issues", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Entrepreneurship", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Future Business Leader", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Global Business", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Graphic Design", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Health Care Administration", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Help Desk", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Hospitality Management", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Impromptu Speaking", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Insurance and Risk Management", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Business", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Business Communication", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Business Presentation", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Introduction to Business Procedures", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to FBLA", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Financial Math", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Information Technology", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Parliamentary Procedure", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Public Speaking", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Job Interview", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Journalism", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("LifeSmarts", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Local Chapter Annual Business Report", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Management Decision Making", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Management Information Systems", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Marketing", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Mobile Application Development", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Network Design", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Networking Concepts", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Organizational Leadership", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Parliamentary Procedure", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Partnership with Business Project", "", CompType.PROJECT, R.drawable.project_icon),
            new Competition("Personal Finance", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Political Science", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Public Service Announcement", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Public Speaking", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Publication Design", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Sales Presentation", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Securities and Investments", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Social Media Campaign", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Sports and Entertainment Management", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Spreadsheet Applications", "", CompType.PRODUCTION, R.drawable.production_icon),
            new Competition("Virtual Business Finance Challenge", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Virtual Business Management Challenge", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Website Design", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Word Processing", "", CompType.PRODUCTION, R.drawable.production_icon),

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
        searchView = root.findViewById(R.id.compsSearch);
        eventRecyclerView = root.findViewById(R.id.comps);
        eventRecyclerView.setHasFixedSize(true);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        comps = new ArrayList<Competition>();
        for(Competition comp : competitions) {
            comps.add(comp);
        }
        adapter = new CompsAdapter(comps, root.getContext());
        eventRecyclerView.setAdapter(adapter);
        for(Competition comp : comps) {
            db.collection("comps").document(comp.getName()).set(comp, SetOptions.merge());
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CompsViewModel.class);
        // TODO: Use the ViewModel
    }

}
