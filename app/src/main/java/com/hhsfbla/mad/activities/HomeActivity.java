package com.hhsfbla.mad.activities;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.ui.AboutChapterFragment;
import com.hhsfbla.mad.activities.ui.AboutFragment;
import com.hhsfbla.mad.activities.ui.CalendarFragment;
import com.hhsfbla.mad.activities.ui.CompsFragment;
import com.hhsfbla.mad.activities.ui.ContactFragment;
import com.hhsfbla.mad.activities.ui.HomeFragment;
import com.hhsfbla.mad.activities.ui.MyEventsFragment;
import com.hhsfbla.mad.activities.ui.OfficerFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageButton;

import static java.security.AccessController.getContext;

public class HomeActivity extends AppCompatActivity {
    private FirebaseUser user;
    private AppBarConfiguration mAppBarConfiguration;
    private ImageButton profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        profileButton = findViewById(R.id.profileButton);
//        profileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
//            }
//        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about, R.id.nav_about_chapter,
                R.id.nav_calendar, R.id.nav_contact, R.id.nav_comps, R.id.nav_my_events, R.id.nav_officer)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // user setup
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // TODO set the ImageView to the user's pfp, TextViews below it to name/email
        } else {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
//        Once you have the id for each fragment, put HomeFragment ID here instead of R.id.teams (also put in title)
//        navigationView.setCheckedItem(R.id.teams);
//        setTitle("My Teams");
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

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            setTitle("About");

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.placeholder_fragment, new AboutFragment());
            ft.commit();
        } else if (id == R.id.nav_calendar) {
            setTitle("Calendar");

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.placeholder_fragment, new CalendarFragment());
            ft.commit();
        }
        else if (id == R.id.nav_home) {
            setTitle("Homepage");

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.placeholder_fragment, new HomeFragment());
            ft.commit();
        }
        else if (id == R.id.nav_comps) {
            setTitle("FBLA Comps");

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.placeholder_fragment, new CompsFragment());
            ft.commit();
        }
        else if (id == R.id.nav_about_chapter) {
            setTitle("About Chapter");

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.placeholder_fragment, new AboutChapterFragment());
            ft.commit();
        }
        else if (id == R.id.nav_contact) {
            setTitle("Contact Us");

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.placeholder_fragment, new ContactFragment());
            ft.commit();
        }
        else if (id == R.id.nav_my_events) {
            setTitle("My Events");

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.placeholder_fragment, new MyEventsFragment());
            ft.commit();
        }
        else if (id == R.id.nav_officer) {
            setTitle("Officer Team");

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.placeholder_fragment, new OfficerFragment());
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        user = null;
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    }
                });
    }


}
