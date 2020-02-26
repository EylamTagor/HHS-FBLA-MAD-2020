package com.hhsfbla.mad.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.adapters.UserAdapter;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.Competition;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;
import com.hhsfbla.mad.ui.comps.CompsFragment;
import com.hhsfbla.mad.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class CompDetailActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener{

    private FirebaseFirestore db;
    private FirebaseUser fuser;
    private TextView name, description, type, noCompetitorsYet;
    private ImageButton back;
    private Button joinButton;
    private Button unJoinButton;
    private RecyclerView userRecyclerView;
    private ArrayList<User> competitors;
    private UserAdapter adapter;
    private static final String TAG = "COMPDETAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comp_detail);
        setTitle("Competition Detail");
        db = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        final String compName = getIntent().getStringExtra("COMP_POSITION");

        noCompetitorsYet = findViewById(R.id.noCompetitorsYet);
        joinButton = findViewById(R.id.compJoinButton);
        unJoinButton = findViewById(R.id.compUnJoinButton);
        back = findViewById(R.id.doneBtn3);
        name = findViewById(R.id.compNameDetail);
        description = findViewById(R.id.compDescriptionDetail);
        type = findViewById(R.id.compTypeDetail);
        competitors = new ArrayList<>();
        userRecyclerView = findViewById(R.id.competitors);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new UserAdapter(competitors, getApplicationContext());
        adapter.setOnItemClickListener(this);
        userRecyclerView.setAdapter(adapter);

        initRecyclerView(compName);

        db.collection("comps").document(compName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(compName);
                description.setText(documentSnapshot.get("description").toString());
                type.setText(documentSnapshot.get("compType").toString());
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompDetailActivity.this, HomeActivity.class);
                intent.putExtra("fragmentToLoad", "CompsFragment");
                startActivity(intent);
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(fuser.getUid()).update("comps", FieldValue.arrayUnion(compName)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                        startActivity(getIntent());
                    }
                });

            }
        });
        unJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(fuser.getUid()).update("comps", FieldValue.arrayRemove(compName)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                        startActivity(getIntent());
                    }
                });

            }
        });

    }

    public void initRecyclerView(final String compName) {
        db.collection("users").document(fuser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.toObject(User.class).getComps().contains(compName)) {
                    unJoinButton.setVisibility(View.VISIBLE);
                } else {
                    joinButton.setVisibility(View.VISIBLE);
                }
                if(documentSnapshot.toObject(User.class).getUserType().equals(UserType.ADVISOR)) {
                    unJoinButton.setVisibility(View.GONE);
                    joinButton.setVisibility(View.GONE);
                }
                final String chap = documentSnapshot.get("chapter").toString();
                db.collection("users").whereEqualTo("chapter", chap).whereArrayContains("comps", compName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        competitors.clear();
                        competitors.addAll(queryDocumentSnapshots.toObjects(User.class));
                        adapter.notifyDataSetChanged();
                        adapter.setUsers(competitors);
                        if(competitors.isEmpty()) {
                            noCompetitorsYet.setVisibility(View.VISIBLE);
                        } else {
                            noCompetitorsYet.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }
    @Override
    public void onItemClick(DocumentSnapshot snapshot, View v, int position) {

    }
}
