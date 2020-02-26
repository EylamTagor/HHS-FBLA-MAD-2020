package com.hhsfbla.mad.ui.mychapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hhsfbla.mad.activities.EditChapterActivity;
import com.hhsfbla.mad.adapters.UserAdapter;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a fragment that includes resources and information about the user's chapter
 */
public class MyChapterFragment extends Fragment implements UserAdapter.OnItemClickListener {

    private Button editChapter;
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
    private ImageButton facebook, insta;
    private ProgressDialog progressDialog;
    private boolean isAdvisor = false;

    /**
     * Creates and inflates a new MyChapterFragment with the following parameters
     *
     * @param inflater to inflate the fragment
     * @param container ViewGroup into which the fragment is inflated
     * @param savedInstanceState used to save activity regarding this fragment
     * @return the inflated fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_my_chapter, container, false);
        getActivity().setTitle("My Chapter");
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
        chapLink = root.findViewById(R.id.chapWebLink);
        facebook = root.findViewById(R.id.facebookBtn);
        insta = root.findViewById(R.id.instaBtn);
        editChapter = root.findViewById(R.id.editChapter);
        editChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), EditChapterActivity.class));
            }
        });
        initRecyclerView();

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUSer = documentSnapshot.toObject(User.class);
                if (currentUSer.getUserType() == UserType.ADVISOR) {
                    isAdvisor = true;
                }
                if(currentUSer.getUserType() != UserType.MEMBER) {
                    editChapter.setVisibility(View.VISIBLE);
                }
                db.collection("chapters").document(currentUSer.getChapter()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final Chapter chapter = documentSnapshot.toObject(Chapter.class);
                        if (!(chapter.getDescription() == null || chapter.getDescription().isEmpty())) {
                            aboutChap.setText(chapter.getDescription());
                        } else {
                            aboutChap.setText("No Description");
                        }
                        if (!(chapter.getLocation() == null || chapter.getLocation().equalsIgnoreCase(""))) {
                            locaChap.setText(chapter.getLocation());
                        } else {
                            locaChap.setText("No Location");
                        }
                        if (!(chapter.getFacebookPage() == null || chapter.getFacebookPage().equalsIgnoreCase(""))) {
                            facebook.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + chapter.getFacebookPage())));
                                }
                            });
                        } else {
                            facebook.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getContext(), "No facebook account set", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        if (!(chapter.getInstagramTag() == null || chapter.getInstagramTag().equalsIgnoreCase(""))) {
                            insta.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/" + chapter.getInstagramTag())));
                                }
                            });
                        } else {
                            insta.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getContext(), "No instagram account set", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        if (!(chapter.getWebsite() == null || chapter.getWebsite().equalsIgnoreCase(""))) {
                            chapLink.setText(Html.fromHtml("<a href=" + chapter.getWebsite() + ">" + chapter.getName().trim() + " Website</androidx.constraintlayout.widget.ConstraintLayout</a>"));
                            chapLink.setMovementMethod(LinkMovementMethod.getInstance());
                        } else {
                            chapLink.setText("No Website");
                        }


                    }
                });
            }
        });

        chapLink.setTextColor(Color.BLACK);
        return root;
    }

    /**
     * Initializes the RecyclerView of the chapter's members, retrieving data from Firebase Firestore
     */
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
                        if (users.isEmpty()) {
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

    /**
     * Handles any action upon successful initialization of this fragment's parent activity
     *
     * @param savedInstanceState used to save activity ragarding this fragment or its parent activity
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Handles any clicking action done inside this fragment
     *
     * @param snapshot the object pulled from Firebase Firestore, formatted as a DocumentSnapshot
     * @param v the View that will contain the click action
     * @param position the numbered position of snapshot in the full item list
     */
    @Override
    public void onItemClick(final DocumentSnapshot snapshot, View v, int position) {

        Log.d(TAG, "user clicked");
        final User thisuser = snapshot.toObject(User.class);
        Log.d(TAG, isAdvisor ? "Advisor" : "non advisor");
        if (!isAdvisor) {
            return;
        }
        PopupMenu menu = new PopupMenu(v.getContext(), v);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.promoteButton) {
                    progressDialog.setMessage("Promoting...");
                    progressDialog.show();
                    promoteUser(snapshot, thisuser);
                    return true;
                } else if (menuItem.getItemId() == R.id.demoteButton) {
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

    }

    private void promoteUser(DocumentSnapshot snapshot, User user) {
        UserType update = user.getUserType();
        if (update.equals(UserType.OFFICER)) {
            update = UserType.ADVISOR;
        } else if (update.equals(UserType.MEMBER)) {
            update = UserType.OFFICER;
        } else {
            Toast.makeText(getContext(), "User is already an advisor", Toast.LENGTH_LONG).show();
        }
        db.collection("users").document(snapshot.getId()).update("userType", update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                initRecyclerView();
            }
        });
    }

    private void demoteUser(DocumentSnapshot snapshot, User user) {
        UserType update = user.getUserType();
        if (update.equals(UserType.OFFICER)) {
            update = UserType.MEMBER;
        } else if (update.equals(UserType.ADVISOR)) {
            update = UserType.OFFICER;
        } else {
            Toast.makeText(getContext(), "User is already an member", Toast.LENGTH_LONG).show();
        }
        db.collection("users").document(snapshot.getId()).update("userType", update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                initRecyclerView();
            }
        });
    }
}
