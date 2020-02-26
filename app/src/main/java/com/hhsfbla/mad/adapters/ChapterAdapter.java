package com.hhsfbla.mad.adapters;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.Chapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for RecyclerViews that display a list of all existing Chapters (names and locations) for users to join
 */
public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> implements Filterable {

    private Context context;
    private List<Chapter> chapterList;
    private List<Chapter> fullList;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private FirebaseUser fuser;
    private ChapterAdapter.OnItemClickListener listener;

    /**
     * Creates a new ChapterAdapter object with the following parameters
     *
     * @param context the Activity, Fragment, etc. hosting the RecyclerView that uses this adapter
     * @param chapterList a list of all existing chapters for users to pick from and join (each of which is an item in this adapter)
     */
    public ChapterAdapter(Context context, final List<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
        fullList = new ArrayList<>(chapterList);
        db = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(context.getApplicationContext());
        storageReference = FirebaseStorage.getInstance().getReference("images").child("pfps");
    }

    /**
     * Creates and inflates a new Chapter item ChapterViewHolder to be included in the corresponding RecyclerView
     *
     * @param parent the parent ViewGroup of the ChapterViewHolder
     * @param viewType the type of view, represented by numeric coding
     * @return the ChapterViewHolder object to be used in initializing the RecyclerView
     */
    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_item, parent, false);
        return new ChapterAdapter.ChapterViewHolder(view);
    }

    /**
     * Sets the appropriate parameters for each chapter item according to its placement in the chapter list
     *
     * @param holder the ChapterViewHolder to contain all of the chapter items
     * @param position the position of the corresponding chapter item in the chapter list (to order the chapter item in the RecyclerView)
     */
    @Override
    public void onBindViewHolder(@NonNull final ChapterViewHolder holder, final int position) {
        Chapter chapter = chapterList.get(position);

        holder.name.setText(chapter.getName());
        if (chapter.getLocation().equals("")) {
            holder.location.setText("Somewhere in space (no location set)");
        } else
            holder.location.setText(chapter.getLocation());

    }

    /**
     * @return the amount of items in the list of chapters
     */
    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    /**
     * Updates the list of chapters using a new list
     *
     * @param chapters new list to replace the old chapter list
     */
    public void setChapterList(List<Chapter> chapters) {
        chapterList = chapters;
        fullList.clear();
        fullList.addAll(chapters);
    }

    /**
     * @return the Filter object, used to search for a specific chapter in the full list of existing chapters
     */
    @Override
    public Filter getFilter() {
        return chapterFilter;
    }

    private Filter chapterFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Chapter> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Chapter c : fullList) {
                    if (c.getName().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(c);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            chapterList.clear();
            chapterList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    /**
     * Represents the container of all chapter items to be included in the RecyclerView that utilizes ChapterAdapter
     */
    class ChapterViewHolder extends RecyclerView.ViewHolder {

        /**
         * the name and location of the chapter, to be displayed
         */
        public TextView name, location;

        /**
         * the parent layout of the RecyclerView that utilizes ChapterAdapter
         */
        public ConstraintLayout constraintLayout;

        /**
         * Creates a new ChapterViewHolder object with the following parameters
         *
         * @param itemView the chapter item that will be contained in this object
         */
        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.chapterName);
            location = itemView.findViewById(R.id.chapterLocation);
            constraintLayout = itemView.findViewById(R.id.chapterConstraintLayout);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {

                        db.collection("chapters").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (final DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    if (snapshot.toObject(Chapter.class).getName().equalsIgnoreCase(chapterList.get(getAdapterPosition()).getName())) {
                                        listener.onItemClick(snapshot, getAdapterPosition());
                                    }
                                }
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
         * @param snapshot the chapter object pulled from Firebase Firestore, formatted as a DocumentSnapshot
         * @param position the numbered position of snapshot in the full chapter list
         */
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    /**
     * Determines the object to listen to and manage clicking actions on the RecyclerView that utilizes this adapter
     *
     * @param listener the object to listen to and manage clicking actions on the RecyclerView that utilizes this adapter
     */
    public void setOnItemCLickListener(ChapterAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
