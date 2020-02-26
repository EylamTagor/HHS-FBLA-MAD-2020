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

import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.CompType;
import com.hhsfbla.mad.data.Competition;

import java.util.ArrayList;
import java.util.List;

public class CompsAdapter extends RecyclerView.Adapter<CompsAdapter.ViewHolder> implements Filterable {

    private List<Competition> comps;
    private List<Competition> allItems;
    private Context context;
    private static final String TAG = "Event Adapter";
    private CompsAdapter.OnItemClickListener listener;

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView pic;
        public ConstraintLayout constraintLayout;

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
                        listener.onItemClick(comps.get(getAdapterPosition()).getName(), getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String id, int position);
    }

    public void setOnItemClickListener(CompsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
