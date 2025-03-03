//Done by: Juan Francisco Jacobo Rodriguez
package nc.prog1415.instafeed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ListView restaurantListView;
    private FusedLocationProviderClient fusedLocationClient;
    private Spinner restaurantSpinner;  // Spinner for filtering by name
    private Helper dbHelper;

    Button btnReview, btnHelp, btnAbout;
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnReview = this.findViewById(R.id.btnReview);
        btnHelp = this.findViewById(R.id.btnHelp);
        btnAbout = this.findViewById(R.id.btnAbout);

        dbHelper = new Helper(this, "RestaurantDB", null, 1);
        dbHelper.CreateSampleRecords();  // Ensure the database is populated

        restaurantListView = findViewById(R.id.restaurantListView);
        restaurantSpinner = findViewById(R.id.spRestaurant);  // Initialize the Spinner

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Fetch the restaurant names and set them in the Spinner
        setupRestaurantSpinner();

        // Fetch and display all restaurants initially
        displayRestaurants(null);

        // Register context menu
        final View view = this.findViewById(R.id.main);
        this.registerForContextMenu(view);

        // Button listeners for navigation to other activities
        btnReview.setOnClickListener(newView -> {
            intent = new Intent(MainActivity.this, ReviewActivity.class);
            startActivity(intent);
        });

        btnHelp.setOnClickListener(newView -> {
            intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        btnAbout.setOnClickListener(newView -> {
            intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        // Set item click listener for the ListView
        restaurantListView.setOnItemClickListener((parent, newView, position, id) -> {
            String restaurantId = ((HashMap<String, String>) parent.getItemAtPosition(position)).get("id");
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra("RESTAURANT_ID", Integer.parseInt(restaurantId));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayRestaurants(null);  // Refresh the list on resume
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int id = item.getItemId();
        String message = "";

        if (id == R.id.review) {
            intent = new Intent(MainActivity.this, ReviewActivity.class);
        } else if (id == R.id.aboutUs) {
            intent = new Intent(MainActivity.this, AboutActivity.class);
        } else if (id == R.id.help) {
            intent = new Intent(MainActivity.this, HelpActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
        return true;
    }

    // Function to display the list of restaurants
    private void displayRestaurants(String restaurantNameFilter) {
        Cursor cursor;
        if (restaurantNameFilter != null) {
            // Filter restaurants by the selected name
            cursor = dbHelper.getRestaurantByName(restaurantNameFilter);  // Filter by name
        } else {
            cursor = dbHelper.getAllRestaurants();  // Get all restaurants if no filter
        }

        if (cursor != null && cursor.moveToFirst()) {
            ArrayList<HashMap<String, String>> restaurantList = new ArrayList<>();
            do {
                HashMap<String, String> restaurantData = new HashMap<>();
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("ID"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("Name"));
                @SuppressLint("Range") double rating = cursor.getDouble(cursor.getColumnIndex("Rating"));
                @SuppressLint("Range") String details = cursor.getString(cursor.getColumnIndex("Details"));
                @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex("Latitude"));
                @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex("Longitude"));

                restaurantData.put("id", id);
                restaurantData.put("name", name);
                restaurantData.put("rating", "Rating: " + rating);
                restaurantData.put("location", "Location: " + latitude + ", " + longitude);
                restaurantData.put("details", "Details: " + details);

                restaurantList.add(restaurantData);
            } while (cursor.moveToNext());

            cursor.close();

            /*SimpleAdapter adapter = new SimpleAdapter(
                    this,
                    restaurantList,
                    android.R.layout.simple_list_item_2,
                    new String[]{"name", "rating"},
                    new int[]{android.R.id.text1, android.R.id.text2}
            );*/

            // Create a SimpleAdapter with the custom layout for the ListView
            SimpleAdapter adapter = new SimpleAdapter(
                    this,
                    restaurantList,
                    android.R.layout.simple_list_item_2,  // Use custom list item layout
                    new String[]{"name", "rating"},
                    new int[]{android.R.id.text1, android.R.id.text2}
            ) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text = view.findViewById(android.R.id.text1);
                    TextView text2 = view.findViewById(android.R.id.text2);
                    text.setTextColor(Color.parseColor("#E0E1DD"));  // Set text color for the ListView items
                    text2.setTextColor(Color.parseColor("#E0E1DD"));  // Set text color for the ListView items
                    return view;
                }
            };
            restaurantListView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No restaurants found.", Toast.LENGTH_LONG).show();
        }
    }

    // Method to populate the Spinner with restaurant names
    private void setupRestaurantSpinner() {
        // Get all restaurant names
        Cursor cursor = dbHelper.getAllRestaurants();
        ArrayList<String> restaurantNames = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("Name"));
                restaurantNames.add(name);
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Add a default option for "All Restaurants"
        restaurantNames.add(0, "All Restaurants");

        // Create an ArrayAdapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, restaurantNames) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view;
                text.setTextColor(Color.parseColor("#E0E1DD"));  // Set text color for the selected item
                return view;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view;
                text.setTextSize(18);  // Adjust text size for the dropdown items
                return view;
            }
        };
        // Set the adapter to the Spinner
        restaurantSpinner.setAdapter(adapter);

        // Set an item selected listener for the Spinner
        restaurantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedRestaurant = parentView.getItemAtPosition(position).toString();
                if (selectedRestaurant.equals("All Restaurants")) {
                    displayRestaurants(null);  // Show all restaurants
                } else {
                    displayRestaurants(selectedRestaurant);  // Filter by selected restaurant
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                displayRestaurants(null);  // Show all restaurants when nothing is selected
            }
        });
    }
}
