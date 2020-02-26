package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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

/**
 * Adapter for RecyclerViews that display a list of all officers (and details) in a chapter
 */
public class OfficerAdapter extends RecyclerView.Adapter<OfficerAdapter.ViewHolder> implements Filterable {
    private FirebaseFirestore db;
    private List<User> officers;
    private List<User> allOffs;
    private Context context;
    private static final String TAG = "Officer Adapter";
    private OfficerAdapter.OnItemClickListener listener;

    /**
     * Creates a new OfficerAdapter object with the following parameters
     *
     * @param context the Activity, Fragment, etc. hosting the RecyclerView that uses this adapter
     * @param officers a list of all officers (each of which is an item in this adapter)
     */
    public OfficerAdapter(List<User> officers, Context context) {
        this.officers = officers;
        this.context = context;
        allOffs = new ArrayList<>(officers);
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.officer_item, parent, false);
        return new OfficerAdapter.ViewHolder(view);
    }

    /**
     * Sets the appropriate parameters for each item according to its placement in the item list
     *
     * @param holder the ViewHolder to contain all of the items
     * @param position the position of the corresponding item in the list (to order the item in the RecyclerView)
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = officers.get(position);

        holder.name.setText(user.getName());
        holder.pos.setText(user.getOfficerPos());
        holder.blurb.setText(user.getBlurb());

        if (user.getPic() != null && !user.getPic().equals(""))
            Picasso.get().load(Uri.parse(user.getPic())).into(holder.pic);
        else
            Picasso.get().load(R.drawable.com_facebook_profile_picture_blank_square);
    }

    /**
     * @return the amount of items in the item list
     */
    @Override
    public int getItemCount() {
        return officers.size();
    }

    /**
     * Updates the list of events using a new list
     *
     * @param users new list to replace the old list
     */
    public void setOfficers(List<User> users) {
        officers = users;
        allOffs.clear();
        allOffs.addAll(users);
    }

    /**
     * @return the Filter object used to search for specific officers
     */
    public Filter getFilter() {
        return officerFilter;
    }

    private Filter officerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<User> filteredUsers = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {

                filteredUsers.addAll(allOffs);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (User user : allOffs) {
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
            officers.clear();
            officers.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    /**
     * Represents the container of all items to be included in the RecyclerView that utilizes OfficerAdapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * the name, officer position, and blurb of the officer, to be displayed
         */
        public TextView name, pos, blurb;
        /**
         * the profile picture of the officer, to be displayed
         */
        public ImageView pic;

        /**
         * the parent layout of the RecyclerView that utilizes OfficerAdapter
         */
        public ConstraintLayout constraintLayout;

        /**
         * Creates a new ViewHolder object with the following parameters
         *
         * @param itemView the item that will be contained in this object
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.officerNameDetail);
            pos = itemView.findViewById(R.id.officerPosition);
            blurb = itemView.findViewById(R.id.officerBlurb);
            pic = itemView.findViewById(R.id.officerImageDetail);
            constraintLayout = itemView.findViewById(R.id.officerConstraintLayout);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot snap : queryDocumentSnapshots)
                                    if (snap.toObject(User.class).getName().equalsIgnoreCase(officers.get(getAdapterPosition()).getName()))
                                        listener.onItemClick(snap, v, getAdapterPosition());
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
         * @param v the view to host the click action
         */
        void onItemClick(DocumentSnapshot snapshot, View v, int position);
    }

    /**
     * Determines the object to listen to and manage clicking actions on the RecyclerView that utilizes this adapter
     *
     * @param listener the object to listen to and manage clicking actions on the RecyclerView that utilizes this adapter
     */
    public void setOnItemClickListener(OfficerAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
