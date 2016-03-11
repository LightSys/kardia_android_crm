package org.lightsys.crmapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

/**
 * Created by cubemaster on 3/10/16.
 */
public class ProfileActivity extends AppCompatActivity {
    public static final String LOG_TAG = ProfileActivity.class.getName();
    public static final String NAME_KEY = "EXTRA_NAME";
    public static final String PARTNER_ID_KEY = "EXTRA_PARTNER_ID";

    public String mName;
    public String mPartnerId;
    public Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            mName = extras.getString(NAME_KEY);
            mPartnerId = extras.getString(PARTNER_ID_KEY);

            Bundle arguments = new Bundle();
            arguments.putString(NAME_KEY, mName);
            arguments.putString(PARTNER_ID_KEY, mPartnerId);

            ProfileFragment fragment = new ProfileFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_profile_container, fragment)
                    .commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.collapsingtoolbar_profile);
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(mName);
    }
}
