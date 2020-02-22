package com.hhsfbla.mad.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.HomeActivity;
import com.hhsfbla.mad.data.Chapter;
import com.hhsfbla.mad.data.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> implements Filterable {

    private Context context;
    private List<Chapter> chapterList;
    private List<Chapter> fullList;

    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private FirebaseUser fuser;
    public ChapterAdapter(Context context, final List<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
        fullList = new ArrayList<>(chapterList);
        db = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_item, parent, false);
        return new ChapterAdapter.ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChapterViewHolder holder, final int position) {
        Chapter chapter = chapterList.get(position);

        holder.name.setText(chapter.getName());
        if (chapter.getLocation().equals("")) {
            holder.location.setText("Somewhere in space (no location set)");
        } else
            holder.location.setText(chapter.getLocation());

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Joining...");
                progressDialog.show();
                db.collection("chapters").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(final DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if(snapshot.toObject(Chapter.class).getName().equalsIgnoreCase(chapterList.get(position).getName())) {
//                                db.collection("users").document(fuser.getUid()).set(new User(fuser.getDisplayName(), snapshot.getId(), fuser.getEmail()));
                                db.collection("users").document(fuser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user = documentSnapshot.toObject(User.class);
                                        if(user == null) {
                                            user = new User(fuser.getDisplayName(), snapshot.getId(), fuser.getEmail());
                                            db.collection("users").document(fuser.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    db.collection("chapters").document(snapshot.getId()).update("users", FieldValue.arrayUnion(fuser.getUid()));
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(context, HomeActivity.class);
                                                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                                    context.startActivity(intent);
                                                    return;
                                                }
                                            });
                                        } else {
                                            db.collection("users").document(fuser.getUid()).update("chapter", snapshot.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    db.collection("chapters").document(snapshot.getId()).update("users", FieldValue.arrayUnion(fuser.getUid()));
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(context, HomeActivity.class);
                                                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                                    context.startActivity(intent);
                                                    return;
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }


    public void setChapterList(List<Chapter> chapters) {
        this.chapterList = chapters;
    }

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
            }
            else {
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

    class ChapterViewHolder extends RecyclerView.ViewHolder {
        public TextView name, location;
        public ConstraintLayout constraintLayout;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.chapterName);
            location = itemView.findViewById(R.id.chapterLocation);
            constraintLayout = itemView.findViewById(R.id.chapterConstraintLayout);
        }
    }
}
