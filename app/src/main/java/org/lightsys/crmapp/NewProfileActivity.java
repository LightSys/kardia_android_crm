package org.lightsys.crmapp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by cubemaster on 3/11/16.
 */
public class NewProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProfileInputFragment fragment = new ProfileInputFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_new_profile_container, fragment)
                .commit();

        setContentView(R.layout.activity_new_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add new profile");

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
