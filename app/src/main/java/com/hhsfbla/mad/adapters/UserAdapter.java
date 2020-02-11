package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.CompDetailActivity;
import com.hhsfbla.mad.data.Competition;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private FirebaseUser fuser;
    private List<User> users;
    private Context context;
    private static final String TAG = "Event Adapter";

    public UserAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
        fuser = FirebaseAuth.getInstance().getCurrentUser();
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
            public void onClick(View v) {
                Log.d(TAG, "user clicked");
                //TODO add menu on click for promotion
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
