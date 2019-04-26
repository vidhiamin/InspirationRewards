package com.example.inspirationalr;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView create_profile_txt;
    private EditText login_name, login_password;
    private String NO_DATA_FOR_LOCATION = "No Data For Location";
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private LoginCredStorage loginCredStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        create_profile_txt = findViewById(R.id.create_profile_txt);

        login_name = findViewById(R.id.login_name);
        login_password = findViewById(R.id.login_password);
        checkBox = findViewById(R.id.checkBox);
        progressBar = findViewById(R.id.progressBar);

        setTitle();
        loadData();
        if(!checkConnection()){
            Toast.makeText(this, "Internet connection not found. Please check connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData() {
        loginCredStorage = new LoginCredStorage(this);
        boolean isChecked = loginCredStorage.getBooleanValue("CHECKED");
        if(isChecked) {
            login_name.setText(loginCredStorage.getValue(getString(R.string.user_name_txt)));
            login_password.setText(loginCredStorage.getValue(getString(R.string.password_txt)));
            checkBox.setChecked(true);
        }
    }

    public  void onClickCheckBox(View v) {
        if(checkBox.isChecked()) {
            loginCredStorage.setValue(getString(R.string.user_name_txt), login_name.getText().toString());
            loginCredStorage.setValue(getString(R.string.password_txt), login_password.getText().toString());
            loginCredStorage.setBooleanValue(getString(R.string.checkbox_txt), true);
        } else {
            loginCredStorage.setValue(getString(R.string.user_name_txt), "");
            loginCredStorage.setValue(getString(R.string.password_txt), "");
            loginCredStorage.setBooleanValue(getString(R.string.checkbox_txt), false);
        }
    }

    public void onClickLogin(View v) {
        progressBar.setVisibility(View.VISIBLE);
        String name = login_name.getText().toString();
        String password = login_password.getText().toString();
        String[] params = new String[]{name, password};
        LoginAsyncTask  loginAsyncTask = new LoginAsyncTask(this);
        loginAsyncTask.execute(params);

        //----- call service using asynctask
//        if(userName.equals("admin") && password.equals("admin")) {
//            Intent intent = new Intent(MainActivity.this, ViewProfileActivity.class);
//            startActivity(intent);
//        }
    }

    public void onClickCreateProfile(View v) {
        Intent intent = new Intent(MainActivity.this, CreateProfileActivity.class);
        startActivity(intent);

    }
    public void setTitle() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Rewards");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.icon);
    }

    public void redirectTOActivity(UserDetails userDetails,String status) {
        if(status.equalsIgnoreCase("SUCCESS")) {
            Intent intent = new Intent(MainActivity.this, ViewProfileActivity.class);
            //---put Extras
            intent.putExtra("userDetails", userDetails);
            progressBar.setVisibility(View.GONE);
            startActivity(intent);
        }
        else {
            progressBar.setVisibility(View.GONE);
            AwardActivity.createToast(MainActivity.this, "Invalid username/password combination. Please try again!", Toast.LENGTH_SHORT);

        }
    }


    public boolean checkConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = networkInfo.isConnected();
        boolean isAvailable = networkInfo.isAvailable();

        if( isConnected  || isAvailable || networkInfo != null ){
            return true;
        }
        return false;
    }
}