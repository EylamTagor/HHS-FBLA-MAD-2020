package com.hhsfbla.mad.recyclerview_stuff;

import android.content.Context;
import android.util.Log;
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
    private OnEventListener eventListener;
    private static final String TAG = "adapter";

    public EventAdapter(List<EventItem> eventItems, OnEventListener eventListener) {
        Log.d(TAG, "construction");
        this.eventItems = eventItems;
        this.eventListener = eventListener;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "on create");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view, eventListener);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView title, date, time, location;
        public ImageView pic;
        public OnEventListener onEventListener;

        public ViewHolder(@NonNull View itemView, OnEventListener onEventListener) {
            super(itemView);

            title = itemView.findViewById(R.id.eventTitle);
            date = itemView.findViewById(R.id.eventDate);
            time = itemView.findViewById(R.id.eventTime);
            location = itemView.findViewById(R.id.eventLocation);
            pic = itemView.findViewById(R.id.eventPic);
            this.onEventListener = onEventListener;
            itemView.setOnClickListener(this);
            Log.d(TAG, "this thing");
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "adapter onclick");
            onEventListener.onEventClick(getAdapterPosition());
        }
    }

    public interface  OnEventListener {
        void onEventClick(int position);
    }
}
