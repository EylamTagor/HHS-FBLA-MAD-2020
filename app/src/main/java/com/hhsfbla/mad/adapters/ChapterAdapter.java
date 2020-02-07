package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.HomeActivity;
import com.hhsfbla.mad.data.Chapter;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> implements Filterable {

    private Context context;
    private List<Chapter> chapterList;
    private List<Chapter> fullList;

    public ChapterAdapter(Context context, final List<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
        this.fullList = new ArrayList<>(chapterList);
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
                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra("CHAPTER_POSITION", position);
                context.startActivity(intent);
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

            if (constraint == null || constraint.length() == 0)
                filteredList.addAll(fullList);
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Chapter c : fullList)
                    if (c.getName().toLowerCase().contains(filterPattern) || c.getLocation().toLowerCase().contains(filterPattern))
                        filteredList.add(c);
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
