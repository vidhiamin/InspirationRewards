package com.example.inspirationalr;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    public TextView history_date, reward_provider, points, comments;

    public HistoryViewHolder(@NonNull View view) {
        super(view);
        history_date = (TextView) view.findViewById(R.id.history_date);
        reward_provider = (TextView) view.findViewById(R.id.reward_provider);
        points = (TextView) view.findViewById(R.id.points);
        comments = (TextView) view.findViewById(R.id.comments);
    }
}
