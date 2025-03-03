//Done by: Juan Francisco Jacobo Rodriguez
package nc.prog1415.instafeed;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    //Variable that will store the 5 seconds of loading screen
    private static final int SPLASH_TIME_OUT = 5000; //time in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler to wait for 5 seconds, after that, launch the Main Activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Launch MainActivity
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                // Close the splash activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
