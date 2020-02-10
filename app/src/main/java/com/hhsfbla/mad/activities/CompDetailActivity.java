package com.hhsfbla.mad.activities;

import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.data.User;

import java.util.ArrayList;
import java.util.List;

public class CompDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser fuser;
    private TextView name, description;
    private ImageView pic;
    private Button joinButton;
    private Button unJoinButton;
    private ArrayList<String> competitors;
    private static final String TAG = "COMPDETAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comp_detail);
        setTitle("Competition Detail");
        db = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        final String compName = getIntent().getStringExtra("COMP_POSITION");
        competitors = new ArrayList<String>();
        joinButton = findViewById(R.id.compJoinButton);
        unJoinButton = findViewById(R.id.compUnJoinButton);
        name = findViewById(R.id.compNameDetail);
        description = findViewById(R.id.compDescriptionDetail);
        pic = findViewById(R.id.compPicDetail);

        db.collection("users").document(fuser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, competitors.toString());
                User temp = documentSnapshot.toObject(User.class);
                if(temp.getComps().contains(compName)) {
                    joinButton.setVisibility(View.GONE);
                    unJoinButton.setVisibility(View.VISIBLE);
                } else {
                    joinButton.setVisibility(View.VISIBLE);
                    unJoinButton.setVisibility(View.GONE);
                }
                db.collection("chapters").document(documentSnapshot.get("chapter").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot snap) {
                        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(final QuerySnapshot users) {
                                for(DocumentSnapshot user : users) {
                                    for(String member : (List<String>)snap.get("users")) {
                                        if(member.equalsIgnoreCase(user.getId())) {
                                            if(user.toObject(User.class).getComps().contains(compName)) {
                                                competitors.add(member);
                                            }
                                        }
                                    }
                                }
                                //TODO set competitor names
                                if(competitors == null || competitors.size() > 3) {
                                    return;
                                }

                            }
                        });
                    }
                });
            }
        });

        db.collection("comps").document(compName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(compName);
                description.setText(documentSnapshot.get("description").toString());
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

}
