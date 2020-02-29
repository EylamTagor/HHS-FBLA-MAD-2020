package com.hhsfbla.mad.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.hhsfbla.mad.data.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Represents the page that users see as soon as they log in, has a list of chapter events
 */
public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser user;
    private CircleImageView profileImage;
    private Button profileButton;
    private TextView name;
    private TextView email;
    private FirebaseFirestore db;

    private static final String TAG = "DASHBOARD";

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     * with data of the existing event from the database
     * @param savedInstanceState the save state of the activity or page
     */
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

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_my_events, R.id.nav_calendar,
                R.id.nav_comps, R.id.nav_mycomps, R.id.nav_about_chapter, R.id.nav_officer, R.id.nav_about, R.id.nav_contact)
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
                    User thisuser = documentSnapshot.toObject(User.class);
                    name.setText(thisuser.getName());
                    if(thisuser.getPic() != null && !thisuser.getPic().equalsIgnoreCase("")) {
                        Picasso.get().load(Uri.parse(thisuser.getPic())).into(profileImage);
                    } else {
                        Picasso.get().load(user.getPhotoUrl()).into(profileImage);
                    }
                }
            });

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

    /**
     * Sets up navigation characteristics
     * @return whether navigation was successful
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
