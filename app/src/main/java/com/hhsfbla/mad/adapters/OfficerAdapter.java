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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.User;

import java.util.ArrayList;
import java.util.List;

public class OfficerAdapter extends RecyclerView.Adapter<OfficerAdapter.ViewHolder> {
    private FirebaseFirestore db;
    private FirebaseUser fuser;
    private List<User> officers;
    private List<User> allOffs;
    private Context context;
    private static final String TAG = "Officer Adapter";

    public OfficerAdapter(List<User> officers, Context context) {
        this.officers = officers;
        this.context = context;
        allOffs = new ArrayList<>(officers);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.officer_item, parent, false);
        return new OfficerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = officers.get(position);

        holder.name.setText(user.getName());
        holder.pos.setText(user.getOfficerPos());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, pos;
        public ImageView pic;
        public ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.officerNameDetail);
            pos = itemView.findViewById(R.id.officerPosition);
            pic = itemView.findViewById(R.id.officerImageDetail);
            constraintLayout = itemView.findViewById(R.id.officerConstraintLayout);
        }
    }
}
