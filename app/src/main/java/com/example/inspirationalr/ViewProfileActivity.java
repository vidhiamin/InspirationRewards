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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewProfileActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {

    private List<RewardsDetails> historyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HistoryAdapter mAdapter;
    private TextView user_name, user_id, location, points_awarded, department_name, position_name;
    private TextView points_to_award, story, reward_history_title;
    private ImageView profile_image;
    private UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        user_name = findViewById(R.id.user_name);
        user_id = findViewById(R.id.user_id);
        location = findViewById(R.id.location);
        points_awarded = findViewById(R.id.points_awarded);
        department_name = findViewById(R.id.department_name);
        position_name = findViewById(R.id.position_name);
        points_to_award = findViewById(R.id.points_to_award);
        story = findViewById(R.id.story);
        reward_history_title = findViewById(R.id.reward_history_title);
        profile_image = findViewById(R.id.profile_image);



        setTitle();
//        reward_history_title = findViewById(R.id.reward_history_title);

        recyclerView = findViewById(R.id.history_recycler);
        mAdapter = new HistoryAdapter(historyList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userDetails = (UserDetails) getIntent().getSerializableExtra("userDetails");
        setUserProfile(userDetails);
        if(userDetails != null)
            historyList.addAll(userDetails.getRewards());
    }

    private void setUserProfile(UserDetails userDetails) {

        user_name.setText(userDetails.getLastname() + ", " +userDetails.getFirstname());
        user_id.setText("(" + userDetails.getUsername() + ")");
        location.setText(userDetails.getLocation());
        points_awarded.setText(String.valueOf(userDetails.getPoints()));
        department_name.setText(userDetails.getDepartment());
        position_name.setText(userDetails.getPosition());
        points_to_award.setText(String.valueOf(userDetails.getPointstoaward()));
        story.setText(userDetails.getStory());
        reward_history_title.setText("Reward History("+ userDetails.getRewards().size()+"): ");
        try {
            Bitmap bitmap = StringToBitMap(userDetails.getImage());
            profile_image.setImageBitmap(bitmap);
        } catch (Exception e){

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_profile_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.edit_icon:
                i = new Intent(  ViewProfileActivity.this, EditProfileActivity.class);
                i.putExtra("userDetails", userDetails);
                startActivityForResult(i, 102);
                return true;
            case R.id.leaderboard_menu_icon:
                i = new Intent(  ViewProfileActivity.this, LeaderBoardActivity.class);
                i.putExtra("loggedInUserDetails", userDetails);
                startActivityForResult(i, 101);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setTitle() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Your Profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.icon);
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                LoginAsyncTask loginAsyncTask = new LoginAsyncTask(ViewProfileActivity.this);
                String[] param = new String[]{userDetails.getUsername(), userDetails.getPassword()};
                loginAsyncTask.execute(param);

            } else {
            }

        } else if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                LoginAsyncTask loginAsyncTask = new LoginAsyncTask(ViewProfileActivity.this);
                String[] param = new String[]{userDetails.getUsername(), userDetails.getPassword()};
                loginAsyncTask.execute(param);
            } else {
            }

        } else {
        }

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

    public void resetUserDetails(UserDetails userDetails, String status) {
        if(status.equalsIgnoreCase("SUCCESS")) {
            this.userDetails = userDetails;
            setUserProfile(userDetails);
        } else {
            AwardActivity.createToast(ViewProfileActivity.this, "Invalid username/password combination. Please try again!", Toast.LENGTH_SHORT);

        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }



}