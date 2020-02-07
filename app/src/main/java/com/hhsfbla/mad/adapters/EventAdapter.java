package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.hhsfbla.mad.activities.EventPageActivity;
import com.hhsfbla.mad.data.ChapterEvent;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<ChapterEvent> events;
    private Context context;
    private static final String TAG = "Event Adapter";
    public EventAdapter(List<ChapterEvent> events, Context context) {
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ChapterEvent event = events.get(position);

        holder.title.setText(event.getName());
        holder.date.setText(event.getDate());
        holder.time.setText(event.getTime());
        holder.location.setText(event.getLocation());
        holder.pic.setImageResource(event.getPic());
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "event clicked");
                Intent intent = new Intent(context, EventPageActivity.class);
                intent.putExtra("EVENT_POSITION", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<ChapterEvent> events) {
        this.events = events;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, date, time, location;
        public ImageView pic;
        public ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.eventTitle);
            date = itemView.findViewById(R.id.eventDate);
            time = itemView.findViewById(R.id.eventTime);
            location = itemView.findViewById(R.id.eventLocation);
            pic = itemView.findViewById(R.id.eventPic);
            constraintLayout = itemView.findViewById(R.id.eventConstraintLayout);
        }
    }
}
