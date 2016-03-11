package org.lightsys.crmapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit_profile:
                Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
                i.putExtra(NAME_KEY, mName);
                i.putExtra(PARTNER_ID_KEY, mPartnerId);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(NAME_KEY, mName);
        savedInstanceState.putString(PARTNER_ID_KEY, mPartnerId);

        super.onSaveInstanceState(savedInstanceState);
    }
}
