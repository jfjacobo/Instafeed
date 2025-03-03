//Done by: Juan Francisco Jacobo Rodriguez
package nc.prog1415.instafeed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {
    //Declaring the widget elements
    private TextView txtName, txtRating, txtDetails, txtLatitude, txtLongitude,txtReviewDate;
    private ImageView imgRestaurant;
    //Declaring bacck button
    private Button btnBack;
    //Declaring an intent
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        // Initialize the UI elements
        txtName = findViewById(R.id.txtRestName);
        txtRating = findViewById(R.id.txtFinalRating);
        txtDetails = findViewById(R.id.txtDetails);
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);
        imgRestaurant = findViewById(R.id.restaurantImageView);
        txtReviewDate = findViewById(R.id.txtReviewDate);
        btnBack =findViewById(R.id.btnBackDet);

        //Get the restaurant ID from the intent
        intent = getIntent();
        int restaurantId = intent.getIntExtra("RESTAURANT_ID", -1);

        // Populate the restaurant details
        if (restaurantId != -1) {
            populateRestaurantDetails(restaurantId);
        }
        btnBack.setOnClickListener(v -> finish());
    }

    // Method to populate data in the UI
    private void populateRestaurantDetails(int restaurantId) {
        // Fetch the restaurant details from the database using the restaurant ID
        Helper dbHelper = new Helper(this, "RestaurantDB", null, 1);
        Cursor cursor = dbHelper.getRestaurantById(restaurantId);

        // Check if cursor is not null and contains data
        if (cursor != null && cursor.moveToFirst()) {
            // Safely extract data from the cursor
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("Name"));
            @SuppressLint("Range") double rating = cursor.isNull(cursor.getColumnIndex("Rating")) ? 0.0 : cursor.getDouble(cursor.getColumnIndex("Rating"));
            @SuppressLint("Range") String details = cursor.getString(cursor.getColumnIndex("Details"));
            @SuppressLint("Range") double latitude = cursor.isNull(cursor.getColumnIndex("Latitude")) ? 0.0 : cursor.getDouble(cursor.getColumnIndex("Latitude"));
            @SuppressLint("Range") double longitude = cursor.isNull(cursor.getColumnIndex("Longitude")) ? 0.0 : cursor.getDouble(cursor.getColumnIndex("Longitude"));
            @SuppressLint("Range") byte[] pictureBytes = cursor.getBlob(cursor.getColumnIndex("Picture"));

            // Set the data to UI elements with default values where applicable
            txtName.setText(name != null ? name : "N/A");
            txtRating.setText(String.format("Rating: %.1f", rating));
            txtDetails.setText(details != null && !details.isEmpty() ? details : "No details available.");
            txtLatitude.setText(String.format("Latitude: %.6f", latitude));
            txtLongitude.setText(String.format("Longitude: %.6f", longitude));

            // Display picture if available, otherwise hide the ImageView
            if (pictureBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);
                imgRestaurant.setImageBitmap(bitmap);
            } else {
                imgRestaurant.setVisibility(View.INVISIBLE); // Hide the image if not available
            }

            // Close the cursor after use
            cursor.close();
        }
    }
}
