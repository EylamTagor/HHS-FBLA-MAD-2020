package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private Context context;
    private List<Chapter> chapterList;
    private FirebaseFirestore db;
    private FirebaseUser fuser;
    public ChapterAdapter(Context context, final List<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
        db = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
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
                db.collection("chapters").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if(snapshot.toObject(Chapter.class).getName().equalsIgnoreCase(chapterList.get(position).getName())) {
                                db.collection("users").document(fuser.getUid()).set(new User(fuser.getDisplayName(), snapshot.getId(), fuser.getEmail()));
                                db.collection("chapters").document(snapshot.getId()).update("users", FieldValue.arrayUnion(fuser.getUid()));
                                Intent intent = new Intent(context, HomeActivity.class);
                                context.startActivity(intent);
                                return;
                            }
                        }
                        Toast.makeText(context, "Error lol", Toast.LENGTH_LONG).show();
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
