package com.hhsfbla.mad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.ui.comps.CompsFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser user;
    private CircleImageView profileImage;
    private Button profileButton;
    private TextView name;
    private TextView email;
    private FirebaseFirestore db;

    private static final String TAG = "DASHBOARD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about, R.id.nav_about_chapter,
                R.id.nav_calendar, R.id.nav_contact, R.id.nav_comps, R.id.nav_my_events, R.id.nav_officer)
                .setDrawerLayout(drawer)
                .build();
        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (getIntent().getStringExtra("fragmentToLoad") != null && getIntent().getStringExtra("fragmentToLoad").equals("CompsFragment"))
            navController.navigate(R.id.comps_action);

        View header = navigationView.getHeaderView(0);
        profileImage = header.findViewById(R.id.profileImage);
        profileButton = header.findViewById(R.id.profileButton);
        email = header.findViewById(R.id.email_drawer);
        name = header.findViewById(R.id.name_drawer);

        // user setup
        if (user != null) {
            email.setText(user.getEmail());
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    name.setText(documentSnapshot.get("name").toString());
                }
            });

            Picasso.get().load(user.getPhotoUrl()).into(profileImage);
        } else {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
