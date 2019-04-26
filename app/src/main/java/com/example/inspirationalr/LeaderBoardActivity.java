package com.example.inspirationalr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private List<UserDetails> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserAdapter mAdapter;
    private String userName,password, studentId;
    private ProgressBar progressBar;
    private UserDetails loggedInUserDetails;

    private LeaderBoardAsyncTask leaderBoardAsyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        setTitle();

        recyclerView = findViewById(R.id.recycler);
        progressBar = findViewById(R.id.progressBar2);

        mAdapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loggedInUserDetails = (UserDetails) getIntent().getSerializableExtra("loggedInUserDetails");
        userName = loggedInUserDetails.getUsername();
        password = loggedInUserDetails.getPassword();
        studentId = loggedInUserDetails.getStudentID();

        String[] params = new String[]{studentId, userName, password};
        leaderBoardAsyncTask = new LeaderBoardAsyncTask(LeaderBoardActivity.this);
        leaderBoardAsyncTask.execute(params);

        progressBar.setVisibility(View.VISIBLE);
    }

    public void setTitle() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Inspiration Leaderboard");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_with_logo);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
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


    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        UserDetails m = userList.get(pos);

//        Toast.makeText(v.getContext(), "SHORT " + m.toString(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LeaderBoardActivity.this, AwardActivity.class);
        intent.putExtra("targetedUserDetails", m);
        intent.putExtra("loggedInUserDetails", loggedInUserDetails);
        startActivityForResult(intent, 103);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 103) {
            if (resultCode == RESULT_OK) {
                userList.clear();
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.VISIBLE);

//                loggedInUserDetails = (UserDetails) data.getSerializableExtra("loggedInUserDetails");
                userName = loggedInUserDetails.getUsername();
                password = loggedInUserDetails.getPassword();
                studentId = loggedInUserDetails.getStudentID();
                String[] params = new String[]{studentId, userName, password};
                leaderBoardAsyncTask = new LeaderBoardAsyncTask(LeaderBoardActivity.this);
                leaderBoardAsyncTask.execute(params);
            } else {
            }

        } else {
        }
    }


    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void setLeaderBoardParams(List<UserDetails> uList) {
        userList.clear();
        if(uList!=null) {
            Collections.sort(uList);
        }

        userList.addAll(uList);

        mAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();

    }
}