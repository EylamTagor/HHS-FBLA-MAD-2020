package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for RecyclerViews that display a list of all existing users (and ranks) in a chapter
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {

    private FirebaseFirestore db;
    private List<User> users;
    private List<User> allItems;
    private Context context;
    private static final String TAG = "User Adapter";
    private UserAdapter.OnItemClickListener listener;

    /**
     * Creates a new EventAdapter object with the following parameters
     *
     * @param context the Activity, Fragment, etc. hosting the RecyclerView that uses this adapter
     * @param users   a list of all existing users (each of which is an item in this adapter)
     */
    public UserAdapter(List<User> users, Context context) {
        this.users = users;
        allItems = new ArrayList<>(users);
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    /**
     * Creates and inflates a new item ViewHolder to be included in the corresponding RecyclerView
     *
     * @param parent the parent ViewGroup of the ViewHolder
     * @param viewType the type of view, represented by numeric coding
     * @return the ViewHolder object to be used in initializing the RecyclerView
     */
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    /**
     * Sets the appropriate parameters for each item according to its placement in the item list
     *
     * @param holder   the ViewHolder to contain all of the items
     * @param position the position of the corresponding item in the list (to order the item in the RecyclerView)
     */
    @Override
    public void onBindViewHolder(final @NonNull UserAdapter.ViewHolder holder, final int position) {
        final User user = users.get(position);
        holder.name.setText(user.getName());
        holder.rank.setText(user.getUserType().toString());
        if (users.get(position).getPic() != null && !users.get(position).getPic().equals("")) {
            Picasso.get().load(Uri.parse(users.get(position).getPic())).into(holder.pic);
        } else {
            Picasso.get().load(R.drawable.com_facebook_profile_picture_blank_square).into(holder.pic);
        }
    }

    /**
     * @return the amount of items in the item list
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * Updates the list of users using a new list
     *
     * @param users new list to replace the old list
     */
    public void setUsers(List<User> users) {
        this.users = users;
        allItems.clear();
        this.allItems.addAll(users);

    }

    /**
     * @return the Filter object used to search for specific users in a chapter
     */
    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<User> filteredUsers = new ArrayList<>();
            Log.d(TAG, charSequence.toString());
            Log.d(TAG, allItems.toString());
            if (charSequence == null || charSequence.length() == 0) {

                filteredUsers.addAll(allItems);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (User user : allItems) {
                    if (user.getName().toLowerCase().startsWith(filterPattern)) {
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
            users.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    /**
     * Represents the container of all items to be included in the RecyclerView that utilizes UserAdapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * the name and rank of the user, to be displayed
         */
        public TextView name, rank;

        /**
         * the profile picture of the user, to be displayed
         */
        public CircleImageView pic;

        /**
         * the parent layout of the RecyclerView that utilizes UserAdapter
         */
        public ConstraintLayout constraintLayout;

        /**
         * Creates a new ViewHolder object with the following parameters
         *
         * @param itemView the item that will be contained in this object
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userNameDetail);
            rank = itemView.findViewById(R.id.rank);
            pic = itemView.findViewById(R.id.userImageDetail);
            constraintLayout = itemView.findViewById(R.id.userConstraintLayout);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        db.collection("users").whereEqualTo("email", users.get(getAdapterPosition()).getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                listener.onItemClick(queryDocumentSnapshots.getDocuments().get(0), view, getAdapterPosition());

                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * Used to specify action after clicking on the RecyclerView that utilizes this adapter
     */
    public interface OnItemClickListener {
        /**
         * Specifies the action after clicking on the RecyclerView that utilizes this adapter
         *
         * @param snapshot the object pulled from Firebase Firestore, formatted as a DocumentSnapshot
         * @param position the numbered position of snapshot in the full item list
         * @param v        the View that will contain the click action
         */
        void onItemClick(DocumentSnapshot snapshot, View v, int position);
    }

    /**
     * Determines the object to listen to and manage clicking actions on the RecyclerView that utilizes this adapter
     *
     * @param listener the object to listen to and manage clicking actions on the RecyclerView that utilizes this adapter
     */
    public void setOnItemClickListener(UserAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
