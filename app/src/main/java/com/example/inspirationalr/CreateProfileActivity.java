package com.example.inspirationalr;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CreateProfileActivity extends AppCompatActivity {
    private static final String TAG = "CreateProfileActivity";
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private int MY_PERM_REQUEST_CODE = 12345;
    private static int MY_LOCATION_REQUEST_CODE = 329;
    private Location bestLocation;
    private boolean showingInfo = false;
    private ImageView profile_image, add_new_image_icon;
    private File currentImageFile;


    private EditText user_name, password, first_name,last_name, dept_name, position_name, your_story;
    private CheckBox admin_user;
    private TextView char_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        profile_image = findViewById(R.id.profile_image);
        add_new_image_icon = findViewById(R.id.add_new_image_icon);
        user_name = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        dept_name = findViewById(R.id.department_name);
        position_name = findViewById(R.id.position_name);
        your_story = findViewById(R.id.your_story);
        admin_user = findViewById(R.id.admin_user);
        char_count = findViewById(R.id.char_count);

        setTitle();
        int charCount = your_story.getText().toString().length();
        String countText = "( " + charCount + " of 360 )";
        char_count.setText(countText);

        your_story.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int charCount = s.toString().length();
                String countText = "( " + charCount + " of 360 )";
                char_count.setText(countText);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charCount = s.toString().length();
                String countText = "( " + charCount + " of 360 )";
                char_count.setText(countText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int charCount = s.toString().length();
                String countText = "( " + charCount + " of 360 )";
                char_count.setText(countText);
            }
        });

        checkGPSPermission();
    }

    private boolean checkPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED )) {
            // I do not yet have permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERM_REQUEST_CODE);

            Log.d(TAG, "checkPermission: READ_EXTERNAL_STORAGE Permission requested, awaiting response.");
            return false; // Do not yet have permission - but I just asked for it
        } else {
            Log.d(TAG, "checkPermission: Already have READ_EXTERNAL_STORAGE Permission for this app.");
            return true;  // I already have this permission
        }
    }

    private boolean checkGPSPermission() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED)) {
            // I do not yet have permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);

            Log.d(TAG, "checkPermission: ACCESS_FINE_LOCATION Permission requested, awaiting response.");
            return false; // Do not yet have permission - but I just asked for it
        } else {
            Log.d(TAG, "checkPermission: Already have ACCESS_FINE_LOCATION Permission for this app.");
            return true;  // I already have this permission
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == MY_PERM_REQUEST_CODE) {
            if (grantResults.length == 0) {
                Log.d(TAG, "onRequestPermissionsResult: Somehow I got an empty 'grantResults' array");
                return;
            }
            if( grantResults[0] == PERMISSION_GRANTED) {
                //User granted permissions!
                Log.d("TAG", "Fine location permission granted");
                openPhotoDialog();
            } else {
                //User denied Location permissions. Here you could warn the user that without
                //Location permissions the app is not usable.
                Toast.makeText(this, "Have a good day!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults.length == 0) {
                Log.d(TAG, "onRequestPermissionsResult: Somehow I got an empty 'grantResults' array");
                return;
            } else if (grantResults[0] == PERMISSION_GRANTED) {
                return;
            } else {
                //User denied Location permissions. Here you could warn the user that without
                //Location permissions the app is not usable.
                Toast.makeText(this, "Have a good day!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_profile_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_icon:
                onClickSaveIconDialog();
                return true;
            case android.R.id.home:
                createToast(CreateProfileActivity.this, "New Profile Not Created", Toast.LENGTH_SHORT);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doAsyncTaskCall() {

        UserDetails u = new UserDetails( "A20413890",
                first_name.getText().toString(),
                last_name.getText().toString(),
                user_name.getText().toString(),
                dept_name.getText().toString(),
                your_story.getText().toString(),
                position_name.getText().toString(),
                password.getText().toString(),
                1000,
                admin_user.isChecked(),
                getLocation(),
                BitMapToString(),
                new ArrayList<RewardsDetails>()
        );
        CreateProfileAsyncTask createProfileAsyncTask = new CreateProfileAsyncTask(u, this);
        createProfileAsyncTask.execute();
    }

    private String getLocation() {
        return doLocationName();
    }

    public String BitMapToString(){
        profile_image.buildDrawingCache();
        Bitmap bitmap = profile_image.getDrawingCache();
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public void setImage(View v) {
        boolean havePermission = checkPermission();
        if (havePermission) {
            //----- Open model
            openPhotoDialog();
        }
    }


    //==============Images===================//
    public void doCamera() {
        currentImageFile = new File(getExternalCacheDir(), "appimage_" + System.currentTimeMillis() + ".jpg");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
//        currentImageFile = new File(getExternalCacheDir(), "appimage_" + System.currentTimeMillis() + ".jpg");
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
//        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
    }
    public void doGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_FILE);
    }
    private void processCamera() {
        Uri selectedImage = Uri.fromFile(currentImageFile);
        profile_image.setImageURI(selectedImage);
        Bitmap bm = ((BitmapDrawable) profile_image.getDrawable()).getBitmap();

        currentImageFile.delete();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            try {
                processGallery(data);
            } catch (Exception e) {
                //Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            try {
                processCamera();
            } catch (Exception e) {
                //Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        profile_image.setImageBitmap(selectedImage);
    }

    public void setTitle() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.icon);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    public void redirectTOActivity(UserDetails userDetails) {
        createToast(CreateProfileActivity.this, "User Create Successful", Toast.LENGTH_SHORT);
        Intent intent = new Intent(  CreateProfileActivity.this, ViewProfileActivity.class);
        //---put Extras
        intent.putExtra("userDetails", userDetails);
        startActivity(intent);
    }

    public void openPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Picture");
        builder.setIcon(R.drawable.logo);
        builder.setMessage("Take picture from: ");
        builder.setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doGallery();
            }
        });
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doCamera();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFF018786);      //0xFF018786 GREEN
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFF018786);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xFFE6AF4B);     //0xFFE6AF4B  YELLOW-ORANGE
    }

    public void onClickSaveIconDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Changes?");
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

    public static void createToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, "" + message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(140, 75, 140, 75);
        tv.setTextColor(0xFFFFFFFF);
        toast.show();
    }


    public String doLocationName() {
        Criteria criteria;

        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String bestProvider = locationManager.getBestProvider(criteria, true);

            Location currentLocation = locationManager.getLastKnownLocation(bestProvider);
            if (currentLocation != null) {
//                       String latLong =   + ", " + ;
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses;
                    addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 10);
                    return displayAddresses(addresses);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "";
                }
            }
        }
        return "";
    }

    private String displayAddresses(List<Address> addresses) {

        for (Address ad : addresses) {

            String a = String.format("%s, %s",
                    (ad.getLocality() == null ? "" : ad.getLocality()),
                    (ad.getAdminArea() == null ? "" : ad.getAdminArea()));

            return a;
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        createToast(CreateProfileActivity.this, "New Profile Not Created", Toast.LENGTH_SHORT);
        finish();
    }

    public void redirectTOActivity(String status, String message) {
        createToast(CreateProfileActivity.this, message, Toast.LENGTH_SHORT);
    }
}
