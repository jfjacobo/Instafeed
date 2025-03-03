//Done by: Juan Francisco Jacobo Rodriguez
package nc.prog1415.instafeed;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ReviewActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    // Declaring UI elements
    Button btnBack, btnSubmit, imgCapture;
    EditText txtName, txtDetails;
    RatingBar txtRating;
    ImageView imgPreview;
    Helper helper;

    private FusedLocationProviderClient fusedLocationClient;
    private double latitude, longitude;
    private Bitmap capturedImageBitmap; // To store captured image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize UI elements
        btnBack = findViewById(R.id.btnRvReturn);
        btnSubmit = findViewById(R.id.btnRvSubmit);
        txtName = findViewById(R.id.txtName);
        txtRating = findViewById(R.id.rbRating);
        txtDetails = findViewById(R.id.txtExtraInfo);
        imgCapture = findViewById(R.id.btnCaptureImage);
        imgPreview = findViewById(R.id.imgPreview);

        // Initialize the database helper and location services
        helper = new Helper(this, "RestaurantDB", null, 1);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set listeners
        imgCapture.setOnClickListener(v -> openCamera());
        btnSubmit.setOnClickListener(view -> onSubmitClick());
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Request location permissions and fetch location
        checkPermissionsAndFetchLocation();
    }

    // Method to handle submit button click
    private void onSubmitClick() {
        // Capture user input
        String name = txtName.getText().toString();
        float rating = txtRating.getRating();
        String details = txtDetails.getText().toString();

        // Validate the input
        if (name.isEmpty() || details.isEmpty()) {
            Toast.makeText(ReviewActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }



        // Check if location is available
        if (latitude == 0 || longitude == 0) {
            Toast.makeText(ReviewActivity.this, "Unable to get location. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the image is captured, if so, pass it; otherwise, pass null
        Bitmap imageToSave = capturedImageBitmap != null ? capturedImageBitmap : null;

        // Insert into database
        try {
            helper.addRestaurant(name, rating, details, latitude, longitude, imageToSave);
            Toast.makeText(ReviewActivity.this, "Restaurant added successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(ReviewActivity.this, "Failed to add restaurant.", Toast.LENGTH_SHORT).show();
        }

        // Finish the activity and go back to MainActivity
        Intent intent = new Intent(ReviewActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Method to check location permissions and fetch the current location
    private void checkPermissionsAndFetchLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    // Handle location permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Fetch the current location using FusedLocationProviderClient
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } else {
                Toast.makeText(ReviewActivity.this, "Unable to get current location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Open the camera and capture an image
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    // Handle the result from the camera activity (image capture)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the captured image
            capturedImageBitmap = (Bitmap) data.getExtras().get("data");

            // Display the image in the ImageView
            imgPreview.setImageBitmap(capturedImageBitmap);
        }
    }

}

