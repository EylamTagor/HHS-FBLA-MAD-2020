package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {

    private FirebaseFirestore db;
    private FirebaseUser fuser;
    private List<User> users;
    private List<User> allItems;
    private Context context;
    private static final String TAG = "User Adapter";
    private UserAdapter.OnItemClickListener listener;

    public UserAdapter(List<User> users, Context context) {
        this.users = users;
        allItems = new ArrayList<>(users);
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
    public void onBindViewHolder(final @NonNull UserAdapter.ViewHolder holder, final int position) {
        final User user = users.get(position);
        holder.name.setText(user.getName());
        holder.rank.setText(user.getUserType().toString());
        if(users.get(position).getPic() != "" && users.get(position).getPic() != null) {
            Picasso.get().load(Uri.parse(users.get(position).getPic())).into(holder.pic);
        } else {
            Picasso.get().load(R.drawable.com_facebook_profile_picture_blank_square).into(holder.pic);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        Log.d(TAG, allItems.toString());
        this.users = users;
        allItems.clear();
        this.allItems.addAll(users);
        Log.d(TAG, allItems.toString());

    }

    @Override
    public Filter getFilter() {
        Log.d(TAG, "hello");
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<User> filteredUsers = new ArrayList<>();
            Log.d(TAG, charSequence.toString());
            Log.d(TAG, allItems.toString());
            if(charSequence == null || charSequence.length() == 0) {

                filteredUsers.addAll(allItems);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(User user : allItems) {
                    if(user.getName().toLowerCase().startsWith(filterPattern)) {
                        filteredUsers.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredUsers;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            users.clear();
            users.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, rank;
        public CircleImageView pic;
        public ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userNameDetail);
            rank = itemView.findViewById(R.id.rank);
            pic = itemView.findViewById(R.id.userImageDetail);
            constraintLayout = itemView.findViewById(R.id.userConstraintLayout);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d(TAG, "hellllo");
//                }
//            });
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot snap : queryDocumentSnapshots) {
                                    if(snap.toObject(User.class).getName().equalsIgnoreCase(users.get(getAdapterPosition()).getName())) {
                                        listener.onItemClick(snap, view,  getAdapterPosition());
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot snapshot, View v, int position);
    }

    public void setOnItemClickListener(UserAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
