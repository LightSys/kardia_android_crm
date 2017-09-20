package org.lightsys.crmapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.fragments.SignUpListFragment;
import org.lightsys.crmapp.fragments.SignUpSheetInputFragment;

/**
 * Created by otter57 on 9/14/17.
 */

public class SignUpInputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String TAG = "SignUpInputAct";
        int FormId = getIntent().getIntExtra(SignUpListFragment.FORM_ID,-1);
        Log.d(TAG, "onCreate: " + FormId);

        Bundle bundle = new Bundle();
        bundle.putInt(SignUpListFragment.FORM_ID, getIntent().getIntExtra(SignUpListFragment.FORM_ID,-1));

        if(savedInstanceState == null) {
            SignUpSheetInputFragment fragment = new SignUpSheetInputFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_profile_input_container, fragment)
                    .commit();
        }

        setContentView(R.layout.activity_profile_input);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_input);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Enter your information");

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
