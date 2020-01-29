package com.hhsfbla.mad.recyclerview_stuff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hhsfbla.mad.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<EventItem> eventItems;
    private Context context;

    public EventAdapter(List<EventItem> eventItems, Context context) {
        this.eventItems = eventItems;
        this.context = context;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventItem eventItem = eventItems.get(position);

        holder.title.setText(eventItem.getTitle());
        holder.date.setText(eventItem.getDate());
        holder.time.setText(eventItem.getTime());
        holder.location.setText(eventItem.getLocation());
        holder.pic.setImageResource(eventItem.getPic());
    }

    @Override
    public int getItemCount() {
        return eventItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, date, time, location;
        public ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.eventTitle);
            date = itemView.findViewById(R.id.eventDate);
            time = itemView.findViewById(R.id.eventTime);
            location = itemView.findViewById(R.id.eventLocation);
            pic = itemView.findViewById(R.id.eventPic);
        }
    }
}
