package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.CompDetailActivity;
import com.hhsfbla.mad.data.CompType;
import com.hhsfbla.mad.data.Competition;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class CompsAdapter extends RecyclerView.Adapter<CompsAdapter.ViewHolder> implements Filterable {

    private List<Competition> comps;
    private List<Competition> allItems;
    private Context context;
    private static final String TAG = "Event Adapter";

    public CompsAdapter(List<Competition> comps, Context context) {
        this.comps = comps;
        allItems = new ArrayList<Competition>(comps);
        this.context = context;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Competition comp = comps.get(position);

        holder.name.setText(comp.getName());

        holder.pic.setImageResource(comp.getPic());
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "event clicked");
                Intent intent = new Intent(context, CompDetailActivity.class);
                intent.putExtra("COMP_POSITION", comps.get(position).getName());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comps.size();
    }

    public void setEvents(List<Competition> events) {
        this.comps = events;
        this.allItems.clear();
        allItems = new ArrayList<>(comps);
    }

    @Override
    public Filter getFilter() {
        return compFilter;
    }

    private Filter compFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Competition> filteredComps = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredComps.addAll(allItems);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Competition comp : allItems) {
                    if(comp.getName().toLowerCase().startsWith(filterPattern)) {
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
            comps.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView pic;
        public ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.competitionName);
            pic = itemView.findViewById(R.id.compIcon);
            constraintLayout = itemView.findViewById(R.id.competitionConstraintLayout);
        }
    }
}
