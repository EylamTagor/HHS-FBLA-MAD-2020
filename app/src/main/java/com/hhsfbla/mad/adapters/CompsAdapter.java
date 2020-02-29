package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.CompType;
import com.hhsfbla.mad.data.Competition;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for RecyclerViews that display a list of all existing Competitive Events (name and type) for users to enlist in
 */
public class CompsAdapter extends RecyclerView.Adapter<CompsAdapter.ViewHolder> implements Filterable {

    private List<Competition> comps;
    private List<Competition> allItems;
    private Context context;
    private static final String TAG = "Event Adapter";
    private CompsAdapter.OnItemClickListener listener;
    private FirebaseFirestore db;

    /**
     * Creates a new CompsAdapter object with the following parameters
     *
     * @param context the Activity, Fragment, etc. hosting the RecyclerView that uses this adapter
     * @param comps a list of all existing chapters for users to pick from and join (each of which is an item in this adapter)
     */
    public CompsAdapter(List<Competition> comps, Context context) {
        this.comps = comps;
        allItems = new ArrayList<Competition>(comps);
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates and inflates a new competition item ViewHolder to be included in the corresponding RecyclerView
     *
     * @param parent the parent ViewGroup of the ViewHolder
     * @param viewType the type of view, represented by numeric coding
     * @return the ViewHolder object to be used in initializing the RecyclerView
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Sets the appropriate parameters for each competition item according to its placement in the competition list
     *
     * @param holder the ViewHolder to contain all of the competition items
     * @param position the position of the corresponding competition item in the competition list (to order the compeititon item in the RecyclerView)
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Competition comp = comps.get(position);

        holder.name.setText(comp.getName());

        int pic = 0;
        if (comp.getCompType().equals(CompType.WRITTEN))
            pic = R.drawable.written_icon;
        else if (comp.getCompType().equals(CompType.TECH))
            pic = R.drawable.tech_icon;
        else if (comp.getCompType().equals(CompType.SPEAKING))
            pic = R.drawable.speaking_icon;
        else if (comp.getCompType().equals(CompType.CASESTUDY))
            pic = R.drawable.casestudy_icon;
        else if (comp.getCompType().equals(CompType.PRODUCTION))
            pic = R.drawable.production_icon;
        else if (comp.getCompType().equals(CompType.PROJECT))
            pic = R.drawable.project_icon;

        holder.pic.setImageResource(pic);
    }

    /**
     * @return the amount of items in the list of comps
     */
    @Override
    public int getItemCount() {
        return comps.size();
    }

    /**
     * Updates the list of competitive events using a new list
     *
     * @param events new list to replace the old chapter list
     */
    public void setEvents(List<Competition> events) {
        this.comps = events;
        this.allItems.clear();
        allItems = new ArrayList<>(comps);
    }

    /**
     * @return the Filter object, used to search for a specific comp in the full list of comps
     */
    @Override
    public Filter getFilter() {
        return compFilter;
    }

    private Filter compFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Competition> filteredComps = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredComps.addAll(allItems);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Competition comp : allItems) {
                    if (comp.getName().toLowerCase().startsWith(filterPattern)) {
                        filteredComps.add(comp);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredComps;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            comps.clear();
            comps.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    /**
     * Represents the container of all comp items to be included in the RecyclerView that utilizes CompsAdapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * the name of the comp, to be displayed
         */
        public TextView name;

        /**
         * the icon representing the type of comp
         */
        public ImageView pic;

        /**
         * the parent layout of the RecyclerView that utilizes ChapterAdapter
         */
        public ConstraintLayout constraintLayout;

        /**
         * Creates a new ViewHolder object with the following parameters
         *
         * @param itemView the comp item that will be contained in this object
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.competitionName);
            pic = itemView.findViewById(R.id.compIcon);
            constraintLayout = itemView.findViewById(R.id.competitionConstraintLayout);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        Log.d(TAG, comps.get(getAdapterPosition()).getName());
                        String id = comps.get(getAdapterPosition()).getName();
                        db.collection("comps").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                listener.onItemClick(documentSnapshot, getAdapterPosition());
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
         * @param snapshot the comp object pulled from Firebase Firestore, formatted as a DocumentSnapshot
         * @param position the numbered position of snapshot in the full comp list
         */
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    /**
     * Determines the object to listen to and manage clicking actions on the RecyclerView that utilizes this adapter
     *
     * @param listener the object to listen to and manage clicking actions on the RecyclerView that utilizes this adapter
     */
    public void setOnItemClickListener(CompsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
