package com.hhsfbla.mad.ui.officer;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.adapters.OfficerAdapter;
import com.hhsfbla.mad.adapters.UserAdapter;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

import java.util.ArrayList;
import java.util.List;

public class OfficerFragment extends Fragment implements OfficerAdapter.OnItemClickListener{

    private OfficerViewModel mViewModel;
    private RecyclerView officerRecyclerView;
    private OfficerAdapter adapter;
    private List<User> officers;
    private SearchView searchView;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private TextView noOfficersYet;
    private static final String TAG = "OFFICERPAGE";

    public static OfficerFragment newInstance() {
        return new OfficerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_officer, container, false);
        searchView = root.findViewById(R.id.officerSearch);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        officerRecyclerView = root.findViewById(R.id.officers);
        noOfficersYet = root.findViewById(R.id.noOfficersYet);
        officerRecyclerView.setHasFixedSize(true);
        officerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        officers = new ArrayList<>();
        adapter = new OfficerAdapter(officers, root.getContext());
        adapter.setOnItemClickListener(this);
        officerRecyclerView.setAdapter(adapter);
        initRecyclerView(UserType.OFFICER);

        return root;
    }

    public void initRecyclerView(final UserType type) {
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final String chap = documentSnapshot.get("chapter").toString();
                db.collection("users").whereEqualTo("chapter", chap).whereEqualTo("userType", type).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        officers.clear();
                        officers.addAll(queryDocumentSnapshots.toObjects(User.class));
                        adapter.notifyDataSetChanged();
                        adapter.setOfficers(officers);
                        if(officers.isEmpty()) {
                            noOfficersYet.setVisibility(View.VISIBLE);
                        } else {
                            noOfficersYet.setVisibility(View.GONE);
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
                    }
                });
            }
        });

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(OfficerViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, View v, int position) {
//        Intent intent = new Intent(getContext(), OfficerEditActivity.class);
//        intent.putExtra("SELECTED_OFFICER", );
//        TODO
    }
}
