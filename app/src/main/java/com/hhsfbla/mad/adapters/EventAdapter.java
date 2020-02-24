package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.EventPageActivity;
import com.hhsfbla.mad.data.ChapterEvent;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<ChapterEvent> events;
    private Context context;
    private static final String TAG = "Event Adapter";
    private EventAdapter.OnItemClickListener listener;

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
        if(event.getPic() != "" && event.getPic() != null) {
            Picasso.get().load(Uri.parse(event.getPic())).fit().centerCrop().into(holder.pic);
        } else
            holder.pic.setVisibility(View.GONE);
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
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "helllo");
                    if(getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(events.get(getAdapterPosition()).getName(), getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String name, int position);
    }

    public void setOnItemClickListener(EventAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
