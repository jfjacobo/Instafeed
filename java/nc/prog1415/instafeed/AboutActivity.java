//Done by: Juan Francisco Jacobo Rodriguez
package nc.prog1415.instafeed;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    //Declaring the button
    Button btnReturn;
    //Declaring the intent
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //Getting the button id
        btnReturn = (Button) this.findViewById(R.id.btnAReturn);
        //Adding click event, going back and forward from Main Activity
        btnReturn.setOnClickListener(newView ->{
           //go back to the main page and finish the current activity
            finish();
        });
    }
}
