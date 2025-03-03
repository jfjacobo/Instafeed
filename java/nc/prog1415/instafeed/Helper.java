//Done by: Juan Francisco Jacobo Rodriguez
package nc.prog1415.instafeed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper extends SQLiteOpenHelper {

    // Define table schema
    private static final String RESTAURANT_TABLE = "CREATE TABLE IF NOT EXISTS Restaurant (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "Name TEXT NOT NULL," +
            "Rating DOUBLE," +
            "Details TEXT," +
            "Latitude DOUBLE," +
            "Longitude DOUBLE," +
            "Picture BLOB)";

    // Pass arguments to base
    public Helper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Create database tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RESTAURANT_TABLE);
    }

    // Concurrency control, not added yet
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For future updates, handle the schema upgrade here.
        db.execSQL("DROP TABLE IF EXISTS Restaurant");
        onCreate(db);
    }

    // Method to get all restaurant records
    public Cursor getAllRestaurants() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Restaurant", null);
    }

    // Method to get a specific restaurant by name (Will be used in the dropdown list when we filter by name)
    public Cursor getRestaurantByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("Restaurant", null, "Name=?", new String[]{name}, null, null, null);
    }

    // Method to find a restaurant by id (Used to get a restaurant details page)
    public Cursor getRestaurantById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("Restaurant", null, "ID=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    // Method to add a new restaurant to the list
    public void addRestaurant(String name, double rating, String details, double latitude, double longitude, Bitmap image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Rating", rating);
        values.put("Details", details);
        values.put("Latitude", latitude);
        values.put("Longitude", longitude);

        // Convert Bitmap to byte array
        if (image != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);  // Compress bitmap
            byte[] byteArray = stream.toByteArray();
            values.put("Picture", byteArray);
        }

        // Add the new record to the database
        db.insert("Restaurant", null, values);
    }

    // Helper method to convert Bitmap to byte array (for storing image as BLOB)
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // You can adjust compression quality
        return byteArrayOutputStream.toByteArray();
    }

    // Helper method to convert byte array back to Bitmap (for retrieving image from BLOB)
    public Bitmap convertByteArrayToBitmap(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    // Method to create sample records (testing purposes)
    public void CreateSampleRecords() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the table is empty
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Restaurant", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        // Only insert sample records if the table is empty
        if (count == 0) {
            ContentValues values = new ContentValues();


            // Insert Tim Hortons with review date
            values.put("Name", "Tim Hortons");
            values.put("Rating", 3.5);
            values.put("Details", "Coffee and donuts restaurant");
            values.put("Latitude", 45.4215);
            values.put("Longitude", -75.6972);
            db.insert("Restaurant", null, values);

            // Insert Burger King with review date
            values.put("Name", "Burger King");
            values.put("Rating", 4.0);
            values.put("Details", "Fast food chain");
            values.put("Latitude", 40.7128);
            values.put("Longitude", -74.0060);
            db.insert("Restaurant", null, values);
        }
    }
}
