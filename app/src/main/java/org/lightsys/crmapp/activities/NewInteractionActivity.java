package org.lightsys.crmapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;

import org.lightsys.crmapp.R;

import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by Daniel Garcia on 26/07/2017.
 *
 * This doesn't do anything except show the Interaction Layout yet.
 * Remaining features still to be added.
 *
 */

public class NewInteractionActivity extends AppCompatActivity {

    public Spinner typeSpinner;
    public Spinner specificContactSpinner;
    public int date;
    //public Date todaysDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.interaction_detail);

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        specificContactSpinner = (Spinner) findViewById(R.id.specificContactSpinner);
        date = R.id.dateText;
    }

}
