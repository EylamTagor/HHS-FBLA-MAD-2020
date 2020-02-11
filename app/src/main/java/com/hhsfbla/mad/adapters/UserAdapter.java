package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;

import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private FirebaseFirestore db;
    private FirebaseUser fuser;
    private List<User> users;
    private Context context;
    private static final String TAG = "Event Adapter";

    public UserAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, final int position) {
        final User user = users.get(position);
        holder.name.setText(user.getName());
        String photo = String.valueOf(fuser.getPhotoUrl());
        Picasso.get().load(photo).into(holder.pic);
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "user clicked");
                db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot snap : queryDocumentSnapshots) {
                            if(snap.getId().equalsIgnoreCase(fuser.getUid())) {
                                if(snap.toObject(User.class).getUserType() != UserType.ADVISOR) {
                                    return;
                                }
                                break;
                            }
                        }
                        PopupMenu menu = new PopupMenu(context, v);
                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if(menuItem.getItemId() == R.id.promoteButton) {
                                    for(DocumentSnapshot snap : queryDocumentSnapshots) {
                                        User thisuser = snap.toObject(User.class);
                                        if(user.getName().equalsIgnoreCase(thisuser.getName())) {
                                            UserType update = thisuser.getUserType();
                                            if(update.equals(UserType.OFFICER)) {
                                                update = UserType.ADVISOR;
                                            } else if(update.equals(UserType.MEMBER)) {
                                                update = UserType.OFFICER;
                                            } else {
                                                Toast.makeText(context, "User is already an advisor", Toast.LENGTH_LONG).show();
                                            }
                                            db.collection("users").document(snap.getId()).update("userType", update);
                                        }
                                    }
                                    return true;
                                } else if(menuItem.getItemId() == R.id.demoteButton) {
                                    for(DocumentSnapshot snap : queryDocumentSnapshots) {
                                        User thisuser = snap.toObject(User.class);
                                        if(user.getName().equalsIgnoreCase(thisuser.getName())) {
                                            UserType update = thisuser.getUserType();
                                            if(update.equals(UserType.OFFICER)) {
                                                update = UserType.MEMBER;
                                            } else if(update.equals(UserType.ADVISOR)) {
                                                update = UserType.OFFICER;
                                            } else {
                                                Toast.makeText(context, "User is already an member", Toast.LENGTH_LONG).show();
                                            }
                                            db.collection("users").document(snap.getId()).update("userType", update);
                                        }
                                    }
                                    return true;
                                }
                                //TODO REFRESH
                                return false;
                            }
                        });
                        menu.inflate(R.menu.promotion_popup_menu);
                        menu.show();

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView pic;
        public ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userNameDetail);
            pic = itemView.findViewById(R.id.userImageDetail);
            constraintLayout = itemView.findViewById(R.id.userConstraintLayout);
        }
    }
}
