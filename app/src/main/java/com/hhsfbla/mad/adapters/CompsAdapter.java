package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.CompDetailActivity;
import com.hhsfbla.mad.data.CompType;
import com.hhsfbla.mad.data.Competition;
import java.util.List;

public class CompsAdapter extends RecyclerView.Adapter<CompsAdapter.ViewHolder> {

    private List<Competition> comps;
    private Context context;
    private static final String TAG = "Event Adapter";

    public CompsAdapter(List<Competition> comps, Context context) {
        this.comps = comps;
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
    }
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
