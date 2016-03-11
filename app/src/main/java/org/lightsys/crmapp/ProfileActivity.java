package org.lightsys.crmapp;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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
    public CollapsingToolbarLayout mCollapsingToolbarLayout;

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
                    .replace(R.id.fragment_profile_container, fragment)
                    .commit();
        }
        else {
            mName = savedInstanceState.getString(NAME_KEY);
            mPartnerId = savedInstanceState.getString(PARTNER_ID_KEY);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbarlayout_profile);
        if(mName != null) {
            mCollapsingToolbarLayout.setTitle(mName);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(NAME_KEY, mName);
        savedInstanceState.putString(PARTNER_ID_KEY, mPartnerId);

        super.onSaveInstanceState(savedInstanceState);
    }
}
