package com.example.locationreminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<ReminderModel> reminderList;
    private OnReminderDeleteListener deleteListener;

    // Interface to handle the "Off" (Delete) button click in MainActivity
    public interface OnReminderDeleteListener {
        void onDelete(ReminderModel reminder);
    }

    public ReminderAdapter(List<ReminderModel> reminderList, OnReminderDeleteListener listener) {
        this.reminderList = reminderList;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your custom card layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int p) {
        ReminderModel item = reminderList.get(p);

        h.title.setText(item.getTitle());

        // Show formatted Date and Time on the card
        String info = item.getDate() + " | " + item.getTime();
        h.dateTime.setText(info);

        // Handle the Delete/Off button click
        h.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList != null ? reminderList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, dateTime;
        ImageButton btnDelete;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
            dateTime = v.findViewById(R.id.tvDateTime);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}