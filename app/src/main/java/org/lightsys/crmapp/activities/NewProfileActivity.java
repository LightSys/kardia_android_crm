package org.lightsys.crmapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.fragments.ProfileInputFragment;

/**
 * Created by cubemaster on 3/11/16.
 */
public class NewProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            ProfileInputFragment fragment = new ProfileInputFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_profile_input_container, fragment)
                    .commit();
        }

        setContentView(R.layout.activity_profile_input);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_input);
        toolbar.setTitle("Add new profile");
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);
    }
}
