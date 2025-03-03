//PROG1415
//Done by: Juan Francisco Jacobo Rodriguez
package nc.prog1415.instafeed;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {
    //Declaring the button
    Button btnBack;
    //Declaring the intent
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        //getting the id for the button
        btnBack = (Button) this.findViewById(R.id.btnHpBack);
        //On click listener for return button, going back and forward
        btnBack.setOnClickListener(newView ->{
            //Close help layout and go back to main activity
            finish();
        });

    }

}
