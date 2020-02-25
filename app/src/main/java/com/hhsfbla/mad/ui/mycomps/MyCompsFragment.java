package com.hhsfbla.mad.ui.mycomps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.CompDetailActivity;
import com.hhsfbla.mad.adapters.CompsAdapter;
import com.hhsfbla.mad.data.Competition;
import com.hhsfbla.mad.data.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MyCompsFragment extends Fragment implements CompsAdapter.OnItemClickListener{
    private TextView noCompsYet;
    private RecyclerView compsRecyclerView;
    private CompsAdapter adapter;

    private List<Competition> comps;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "mycompsfrag";

    public static MyCompsFragment newInstance() {
        return new MyCompsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_comps, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        noCompsYet = root.findViewById(R.id.noSignupsComps);
        compsRecyclerView = root.findViewById(R.id.myCompsRecyclerView);
        compsRecyclerView.setHasFixedSize(true);
        compsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        comps = new ArrayList<>();
        adapter = new CompsAdapter(comps, root.getContext());
        adapter.setOnItemClickListener(this);
        compsRecyclerView.setAdapter(adapter);
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final User currentUser = documentSnapshot.toObject(User.class);

                for (String id : currentUser.getComps()) {
                    db.collection("comps").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            comps.add(documentSnapshot.toObject(Competition.class));
                        }
                    });
                }
                if (comps.size() == 0) {
                    noCompsYet.setVisibility(View.VISIBLE);
                    return;
                } else {
                    noCompsYet.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
            }
        });
        return root;
    }

    @Override
    public void onItemClick(String id, int position) {
        Log.d(TAG, "event clicked");
        Intent intent = new Intent(getContext(), CompDetailActivity.class);
        intent.putExtra("COMP_POSITION", id);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }
}
