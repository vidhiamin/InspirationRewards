package com.example.inspirationalr;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView user_name, position, score;
    public ImageView profile_image;
    public MyViewHolder(View view) {
        super(view);
        user_name = (TextView) view.findViewById(R.id.user_name);
        position = (TextView) view.findViewById(R.id.position);
        score = (TextView) view.findViewById(R.id.score);
        profile_image = (ImageView) view.findViewById(R.id.profile_image);
    }
}
