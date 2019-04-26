package com.example.inspirationalr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AwardActivity extends AppCompatActivity {
    private UserDetails userDetails, loggedInUserDetails;
    private String userName,password, studentId;
    private TextView user_name, total_points_received, department_name, position, story, char_count;
    private EditText score, comments;
    private ImageView user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award);

        userDetails = (UserDetails) getIntent().getSerializableExtra("targetedUserDetails");
        loggedInUserDetails = (UserDetails) getIntent().getSerializableExtra("loggedInUserDetails");

        userName = loggedInUserDetails.getUsername();
        password = loggedInUserDetails.getPassword();
        studentId = loggedInUserDetails.getStudentID();

        setTitle();
        setParams();
    }

    private void setParams() {
        user_name = findViewById(R.id.user_name);
        total_points_received =findViewById(R.id.total_points_received);
        department_name = findViewById(R.id.department_name);
        position = findViewById(R.id.position);
        story = findViewById(R.id.story);
        user_image = findViewById(R.id.user_image);
        char_count = findViewById(R.id.char_count);

        score = findViewById(R.id.score);
        comments = findViewById(R.id.comments);

        user_name.setText(userDetails.getLastname() + ", " + userDetails.getFirstname());
        total_points_received.setText(String.valueOf(userDetails.getPoints()));
        department_name.setText(userDetails.getDepartment());
        position.setText(userDetails.getPosition());
        story.setText(userDetails.getStory());
        Bitmap bitmap = StringToBitMap(userDetails.getImage());
        user_image.setImageBitmap(bitmap);


        comments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int charCount = s.toString().length();
                String countText = "( " + charCount + " of 80 )";
                char_count.setText(countText);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charCount = s.toString().length();
                String countText = "( " + charCount + " of 80 )";
                char_count.setText(countText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int charCount = s.toString().length();
                String countText = "( " + charCount + " of 80 )";
                char_count.setText(countText);
            }
        });

    }

    public void setTitle() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(userDetails.getFirstname() + " " + userDetails.getLastname());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_with_logo);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_profile_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_icon:
                if(checkPoints())
                    onClickSaveIconDialog();
                else
                    createToast(this, "You do not have sufficient points to give.", Toast.LENGTH_SHORT);
                return true;
            case android.R.id.home:
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkPoints() {
        if(loggedInUserDetails.getPointstoaward() > Integer.parseInt(score.getText().toString()))
            return true;
        return false;
    }

    public void doAsyncTaskCall() {
        String targetStudentId = userDetails.getStudentID();
        String targetUsername = userDetails.getUsername();
        String targetName = loggedInUserDetails.getFirstname() + " " + loggedInUserDetails.getLastname();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String targetDate = simpleDateFormat.format(new Date());
        String targetNote = comments.getText().toString();

        String targetValue = score.getText().toString();
        String[] param = new String[] {targetStudentId,targetUsername,targetName,
                targetDate,targetNote,targetValue,
                userName, password, studentId};

        AwardAsyncTask awardAsyncTask = new AwardAsyncTask(this);
        awardAsyncTask.execute(param);
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

    public static void createToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, "" + message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(140, 75, 140, 75);
        tv.setTextColor(0xFFFFFFFF);
        toast.show();
    }


    public void redirectTOActivity(String connectionResult) {
        Intent i = new Intent();
        i.putExtra("loggedInUserDetails", loggedInUserDetails);
        setResult(RESULT_OK, i);
        createToast(this,"Add Reward succeeded", Toast.LENGTH_SHORT);
        finish();
    }

    public void onClickSaveIconDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String body = "Add rewards for " + userDetails.getFirstname() + " " + userDetails.getLastname() + "?";
        builder.setTitle("Add Rewards Points?");
        builder.setMessage(body);
        builder.setIcon(R.drawable.logo);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doAsyncTaskCall();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFF018786);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFF018786);
    }


}