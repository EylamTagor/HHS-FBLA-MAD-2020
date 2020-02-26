package com.hhsfbla.mad.ui.comps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.CompDetailActivity;
import com.hhsfbla.mad.adapters.CompsAdapter;
import com.hhsfbla.mad.data.Competition;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class CompsFragment extends Fragment implements CompsAdapter.OnItemClickListener{

    private RecyclerView eventRecyclerView;
    private CompsAdapter adapter;
    private SearchView searchView;
    private List<Competition> comps;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "COMPS";

    public static CompsFragment newInstance() {
        return new CompsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_comps, container, false);
        getActivity().setTitle("Competitions");
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        searchView = root.findViewById(R.id.compsSearch);
        eventRecyclerView = root.findViewById(R.id.comps);
        eventRecyclerView.setHasFixedSize(true);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        comps = new ArrayList<>();
        adapter = new CompsAdapter(comps, root.getContext());
        adapter.setOnItemClickListener(this);
        eventRecyclerView.setAdapter(adapter);
        db.collection("comps").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                comps = queryDocumentSnapshots.toObjects(Competition.class);
                adapter.setEvents(comps);
                adapter.notifyDataSetChanged();
            }
        });


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
    }

    @Override
    public void onItemClick(String id, int position) {
        Intent intent = new Intent(getContext(), CompDetailActivity.class);
        intent.putExtra("COMP_POSITION", id);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }
}
