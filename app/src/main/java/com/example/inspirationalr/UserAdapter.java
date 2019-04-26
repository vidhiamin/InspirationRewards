package com.example.inspirationalr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "UserAdapter";
    private List<UserDetails> userList;
    private LeaderBoardActivity leaderBoardActivity;

    public UserAdapter(List<UserDetails> userList, LeaderBoardActivity leaderBoardActivity) {
        this.userList = userList;
        this.leaderBoardActivity = leaderBoardActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leader_entry, parent, false);

        itemView.setOnClickListener(leaderBoardActivity);
        itemView.setOnLongClickListener(leaderBoardActivity);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserDetails u = userList.get(position);
        holder.user_name.setText(u.getLastname() + ", " + u.getFirstname());
        holder.position.setText(u.getPosition());
        holder.score.setText(String.valueOf(u.getPoints()));
        Bitmap bitmap = StringToBitMap(u.getImage());
        holder.profile_image.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
