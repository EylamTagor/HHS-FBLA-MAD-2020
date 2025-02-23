package com.hhsfbla.mad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.ChapterEvent;
import com.hhsfbla.mad.utils.ImageRotator;

import java.util.List;

/**
 * Adapter for RecyclerViews that display a list of all existing events (and details) in a chapter for users to join
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<ChapterEvent> events;
    private Context context;
    private static final String TAG = "Event Adapter";
    private EventAdapter.OnItemClickListener listener;
    private FirebaseFirestore db;
    private FirebaseUser fuser;

    /**
     * Creates a new EventAdapter object with the following parameters
     *
     * @param context the Activity, Fragment, etc. hosting the RecyclerView that uses this adapter
     * @param events  a list of all existing events for users to pick from and join (each of which is an item in this adapter)
     */
    public EventAdapter(List<ChapterEvent> events, Context context) {
        this.events = events;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    /**
     * Creates and inflates a new item ViewHolder to be included in the corresponding RecyclerView
     *
     * @param parent the parent ViewGroup of the ViewHolder
     * @param viewType the type of view, represented by numeric coding
     * @return the ViewHolder object to be used in initializing the RecyclerView
     */
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Sets the appropriate parameters for each item according to its placement in the item list
     *
     * @param holder   the ViewHolder to contain all of the items
     * @param position the position of the corresponding item in the list (to order the item in the RecyclerView)
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ChapterEvent event = events.get(position);

        holder.title.setText(event.getName());
        holder.date.setText(event.getDate());
        holder.time.setText(event.getTime());
        holder.location.setText(event.getLocation());
        holder.limit.setText(event.getMemberLimit() == ChapterEvent.NO_LIMIT ? event.getAttendees().size() + "" : event.getAttendees().size() + "/" + event.getMemberLimit());
        if (event.getPic() != null && !event.getPic().equals("")) {
            ImageRotator.loadImageWrapContent(context, holder.pic, event.getPic());
        } else
            holder.pic.setVisibility(View.GONE);
    }

    /**
     * @return the amount of items in the item list
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * Updates the list of events using a new list
     *
     * @param events new list to replace the old list
     */
    public void setEvents(List<ChapterEvent> events) {
        this.events = events;
    }

    /**
     * Represents the container of all items to be included in the RecyclerView that utilizes EventAdapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * the title, date, time and location of the event, to be displayed
         */
        public TextView title, date, time, location, limit;
        /**
         * the picture of the event, to be displayed
         */
        public ImageView pic;

        /**
         * the parent layout of the RecyclerView that utilizes EventAdapter
         */
        public ConstraintLayout constraintLayout;

        /**
         * Creates a new ViewHolder object with the following parameters
         *
         * @param itemView the item that will be contained in this object
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.eventTitle);
            date = itemView.findViewById(R.id.eventDate);
            time = itemView.findViewById(R.id.eventTime);
            location = itemView.findViewById(R.id.eventLocation);
            limit = itemView.findViewById(R.id.eventMemberLimit);
            pic = itemView.findViewById(R.id.eventPic);
            constraintLayout = itemView.findViewById(R.id.eventConstraintLayout);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        final ChapterEvent event = events.get(getAdapterPosition());
                        db.collection("users").document(fuser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                                db.collection("chapters").document(snapshot.get("chapter").toString()).collection("events").whereEqualTo("name", event.getName()).whereEqualTo("description", event.getDescription()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        listener.onItemClick(queryDocumentSnapshots.getDocuments().get(0), getAdapterPosition());
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * Used to specify action after clicking on the RecyclerView that utilizes this adapter
     */
    public interface OnItemClickListener {
        /**
         * Specifies the action after clicking on the RecyclerView that utilizes this adapter
         *
         * @param snapshot the object pulled from Firebase Firestore, formatted as a DocumentSnapshot
         * @param position the numbered position of snapshot in the full item list
         */
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    /**
     * Determines the object to listen to and manage clicking actions on the RecyclerView that utilizes this adapter
     *
     * @param listener the object to listen to and manage clicking actions on the RecyclerView that utilizes this adapter
     */
    public void setOnItemClickListener(EventAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
