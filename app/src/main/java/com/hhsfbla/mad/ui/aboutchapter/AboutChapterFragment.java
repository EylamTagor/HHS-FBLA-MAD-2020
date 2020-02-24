package com.hhsfbla.mad.ui.aboutchapter;

import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

import java.util.ArrayList;
import java.util.List;

public class AboutChapterFragment extends Fragment implements UserAdapter.OnItemClickListener{

    private AboutChapterViewModel mViewModel;

    public static AboutChapterFragment newInstance() {
        return new AboutChapterFragment();
    }

    private TextView noUsersYet, aboutChap, locaChap;
    private RecyclerView userRecyclerView;
    private UserAdapter adapter;
    private SearchView searchView;
    private List<User> users;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "fragaboutchap";
    private TextView chapLink;
    private ImageButton facebook, insta, twitter;
    private ProgressDialog progressDialog;
    private boolean isAdvisor = false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_about_chapter, container, false);
        progressDialog = new ProgressDialog(getContext());
        searchView = root.findViewById(R.id.userSearch);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        noUsersYet = root.findViewById(R.id.noUsersYet);
        aboutChap = root.findViewById(R.id.aboutChapTxtView);
        locaChap = root.findViewById(R.id.locaChapTxtView);
        userRecyclerView = root.findViewById(R.id.userFeed);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        users = new ArrayList<>();
        adapter = new UserAdapter(users, root.getContext());
        adapter.setOnItemClickListener(this);
        userRecyclerView.setAdapter(adapter);
        initRecyclerView();

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUSer = documentSnapshot.toObject(User.class);
                if(currentUSer.getUserType() == UserType.ADVISOR)
                    isAdvisor = true;
                db.collection("chapters").document(currentUSer.getChapter()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Chapter chapter = documentSnapshot.toObject(Chapter.class);
                        if(!(chapter.getDescription() == null || chapter.getDescription().isEmpty())) {
                            aboutChap.setText(chapter.getDescription());
                        }
                        if(!(chapter.getLocation() == null || chapter.getLocation().isEmpty())) {
                            locaChap.setText(chapter.getLocation());
                        }

                    }
                });
            }
        });


        chapLink = root.findViewById(R.id.chapWebLink);
        chapLink.setText(Html.fromHtml("<a href='http://hhsfbla.com'>Homestead FBLA Website</androidx.constraintlayout.widget.ConstraintLayout</a>"));
        chapLink.setMovementMethod(LinkMovementMethod.getInstance());
        chapLink.setTextColor(Color.BLACK);

        facebook = root.findViewById(R.id.facebookBtn);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/homestead8990")));
            }
        });

        insta = root.findViewById(R.id.instaBtn);
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/homesteadfbla/")));
            }
        });

        twitter = root.findViewById(R.id.twitterBtn);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/graemecrackerz")));
            }
        });


        return root;
    }

    public void initRecyclerView() {

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final String chap = documentSnapshot.get("chapter").toString();
                db.collection("users").whereEqualTo("chapter", chap).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        users.clear();
                        users.addAll(queryDocumentSnapshots.toObjects(User.class));
                        adapter.notifyDataSetChanged();
                        adapter.setUsers(users);
                        if(users.isEmpty()) {
                            noUsersYet.setVisibility(View.VISIBLE);
                        } else {
                            noUsersYet.setVisibility(View.GONE);
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
        mViewModel = ViewModelProviders.of(this).get(AboutChapterViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onItemClick(final DocumentSnapshot snapshot, View v, int position) {

        Log.d(TAG, "user clicked");
        final User thisuser = snapshot.toObject(User.class);
//        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot userSnap) {
//                if(userSnap.toObject(User.class).getUserType() != UserType.ADVISOR){
//                    return;
//                }  else {
        Log.d(TAG, isAdvisor ? "Advisor" : "non advisor");
        if(!isAdvisor) {
            return;
        }
                    PopupMenu menu = new PopupMenu(v.getContext(), v);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if(menuItem.getItemId() == R.id.promoteButton) {
                                progressDialog.setMessage("Promoting...");
                                progressDialog.show();
                                promoteUser(snapshot, thisuser);
                                return true;
                            } else if(menuItem.getItemId() == R.id.demoteButton) {
                                progressDialog.setMessage("Promoting...");
                                progressDialog.show();
                                demoteUser(snapshot, thisuser);
                                return true;
                            }
                            return false;
                        }
                    });
                    menu.inflate(R.menu.promotion_popup_menu);
                    menu.show();
//                }
//            }
//        });
    }

    private void promoteUser(DocumentSnapshot snapshot, User user) {
        UserType update = user.getUserType();
        if(update.equals(UserType.OFFICER)) {
            update = UserType.ADVISOR;
        } else if(update.equals(UserType.MEMBER)) {
            update = UserType.OFFICER;
        } else {
            Toast.makeText(getContext(), "User is already an advisor", Toast.LENGTH_LONG).show();
        }
        db.collection("users").document(snapshot.getId()).update("userType", update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "hi there");
                progressDialog.dismiss();
                initRecyclerView();
            }
        });
    }

    private void demoteUser(DocumentSnapshot snapshot, User user) {
        UserType update = user.getUserType();
        if(update.equals(UserType.OFFICER)) {
            update = UserType.MEMBER;
        } else if(update.equals(UserType.ADVISOR)) {
            update = UserType.OFFICER;
        } else {
            Toast.makeText(getContext(), "User is already an member", Toast.LENGTH_LONG).show();
        }
        db.collection("users").document(snapshot.getId()).update("userType", update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "hi there");
                progressDialog.dismiss();
                initRecyclerView();
            }
        });
    }
}
