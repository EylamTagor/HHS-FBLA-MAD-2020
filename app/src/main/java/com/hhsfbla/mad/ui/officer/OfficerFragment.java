package com.hhsfbla.mad.ui.officer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.adapters.OfficerAdapter;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a fragment that consists of a list of all of the user's chapter's officers with details
 */
public class OfficerFragment extends Fragment implements OfficerAdapter.OnItemClickListener{

    private RecyclerView officerRecyclerView;
    private OfficerAdapter adapter;
    private List<User> officers;
    private SearchView searchView;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private TextView noOfficersYet;
    private static final String TAG = "OFFICERPAGE";

    /**
     * Creates and inflates a new AboutFragment with the following parameters
     *
     * @param inflater to inflate the fragment
     * @param container ViewGroup into which the fragment is inflated
     * @param savedInstanceState used to save activity regarding this fragment
     * @return the inflated fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_officer, container, false);
        getActivity().setTitle("Officers");
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

    /**
     * Initializes the fragment's RecyclerView of officers by pulling officer data from Firebase Firestore
     *
     * @param type the rank of users wanted to be in the RecyclerView (i.e. officers only)
     */
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

    /**
     * Handles actions upon successful creation of the parent activity
     *
     * @param savedInstanceState used to save activity regarding this fragment
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Handles any clicking action done inside this fragment
     *
     * @param snapshot the object pulled from Firebase Firestore, formatted as a DocumentSnapshot
     * @param position the numbered position of snapshot in the full item list
     */
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        // nothing (yet?)
    }
}
