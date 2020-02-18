package com.hhsfbla.mad.ui.aboutchapter;

import androidx.lifecycle.ViewModelProviders;

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
import com.hhsfbla.mad.adapters.UserAdapter;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

import java.util.ArrayList;
import java.util.List;

public class AboutChapterFragment extends Fragment {

    private AboutChapterViewModel mViewModel;

    public static AboutChapterFragment newInstance() {
        return new AboutChapterFragment();
    }

    private TextView noUsersYet;
    private RecyclerView userRecyclerView;
    private UserAdapter adapter;
    private SearchView searchView;
    private List<User> users;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "fragaboutchap";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_about_chapter, container, false);
        searchView = root.findViewById(R.id.userSearch);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        noUsersYet = root.findViewById(R.id.noUsersYet);
        userRecyclerView = root.findViewById(R.id.userFeed);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        users = new ArrayList<>();
        initRecyclerView(UserType.MEMBER, root);


        return root;
    }

    public void initRecyclerView(final UserType type, final View root) {
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final String chap = documentSnapshot.get("chapter").toString();
                db.collection("users").whereEqualTo("userType", type).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, chap);
                        for(DocumentSnapshot l : queryDocumentSnapshots) {
                            Log.d(TAG, l.getId());
                        }
                        db.collection("chapters").document(chap).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                List<String> chapUsers = (List<String>)documentSnapshot.get("users");
                                Log.d(TAG, chapUsers.toString());
                                for(DocumentSnapshot snap : queryDocumentSnapshots) {
                                    if(chapUsers.contains(snap.getId())) {
                                        users.add(snap.toObject(User.class));
                                    }
                                }
                                adapter = new UserAdapter(users, root.getContext());
                                userRecyclerView.setAdapter(adapter);
                                if(users.isEmpty()) {
                                    noUsersYet.setVisibility(View.VISIBLE);
                                } else {
                                    noUsersYet.setVisibility(View.GONE);
                                }
                                Toast.makeText(getContext(), "Ready", Toast.LENGTH_LONG).show();
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
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AboutChapterViewModel.class);
        // TODO: Use the ViewModel
    }

}
