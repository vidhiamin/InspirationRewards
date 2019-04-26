package com.example.inspirationalr;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    private static final String TAG = "HistoryAdapter";
    private List<RewardsDetails> historyList;
    private ViewProfileActivity viewProfileActivity;

    public HistoryAdapter(List<RewardsDetails> historyList, ViewProfileActivity viewProfileActivity) {
        this.historyList = historyList;
        this.viewProfileActivity = viewProfileActivity;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_entry, parent, false);

        itemView.setOnClickListener(viewProfileActivity);
        itemView.setOnLongClickListener(viewProfileActivity);

        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        RewardsDetails r = historyList.get(position);
        holder.history_date.setText(r.getHistoryDate());
        holder.reward_provider.setText(r.getRewardProvider());
        holder.points.setText(r.getPoints());
        holder.comments.setText(r.getComments());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
}
