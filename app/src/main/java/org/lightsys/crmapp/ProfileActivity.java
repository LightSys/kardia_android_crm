package org.lightsys.crmapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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
    public static final String EMAIL_KEY = "EXTRA_EMAIL";
    public static final String PHONE_KEY = "EXTRA_PHONE";
    public static final String ADDRESS_KEY = "EXTRA_ADDRESS";
    public static final String CITY_KEY = "EXTRA_CITY";
    public static final String STATE_KEY = "EXTRA_STATE";
    public static final String POSTALCODE_KEY = "EXTRA_POSTALCODE";
    public static final String FULLADDRESS_KEY = "EXTRA_FULLADDRESS";
    public static final String CELL_KEY = "EXTRA_CELL";
    public static final String SURNAME_KEY = "EXTRA_SURNAME";
    public static final String GIVEN_NAMES_KEY = "EXTRA_GIVEN_NAMES";

    public static final String BLOG_KEY = "EXTRA_BLOG";
    public static final String FAX_KEY = "EXTRA_FAX";
    public static final String FACEBOOK_KEY = "EXTRA_FAX";
    public static final String SKYPE_KEY = "EXTRA_SKYPE";
    public static final String TWITTER_KEY = "EXTRA_TWITTER";
    public static final String WEBSITE_KEY = "EXTRA_WEBSITE";


    public String mName;
    public String mPartnerId;

    public String mEmail;
    public String mPhone;
    public String mAddress;
    public String mCity;
    public String mState;
    public String mPostalCode;
    public String mFullAddress;
    public String mCell;
    public String mSurname;
    public String mGivenName;

    public String mBlog;
    public String mFax;
    public String mFacebook;
    public String mSkype;
    public String mTwitter;
    public String mWebsite;

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

            mEmail = extras.getString(EMAIL_KEY);
            mPhone = extras.getString(PHONE_KEY);
            mAddress = extras.getString(ADDRESS_KEY);
            mCity = extras.getString(CITY_KEY);
            mState = extras.getString(STATE_KEY);
            mPostalCode = extras.getString(POSTALCODE_KEY);
            mFullAddress = extras.getString(FULLADDRESS_KEY);
            mCell = extras.getString(CELL_KEY);
            mSurname = extras.getString(SURNAME_KEY);
            mGivenName = extras.getString(GIVEN_NAMES_KEY);

            mBlog = extras.getString(BLOG_KEY);
            mFax = extras.getString(FAX_KEY);
            mFacebook = extras.getString(FACEBOOK_KEY);
            mSkype = extras.getString(SKYPE_KEY);
            mTwitter = extras.getString(TWITTER_KEY);
            mWebsite = extras.getString(WEBSITE_KEY);


            Bundle arguments = new Bundle();
            arguments.putString(NAME_KEY, mName);
            arguments.putString(PARTNER_ID_KEY, mPartnerId);

            arguments.putString(EMAIL_KEY, mEmail);
            arguments.putString(PHONE_KEY, mPhone);
            arguments.putString(ADDRESS_KEY, mAddress);
            arguments.putString(CITY_KEY, mCity);
            arguments.putString(STATE_KEY, mState);
            arguments.putString(POSTALCODE_KEY, mPostalCode);
            arguments.putString(FULLADDRESS_KEY, mFullAddress);
            arguments.putString(CELL_KEY, mCell);
            arguments.putString(SURNAME_KEY, mSurname);
            arguments.putString(GIVEN_NAMES_KEY, mGivenName);

            arguments.putString(BLOG_KEY, mBlog);
            arguments.putString(FAX_KEY, mFax);
            arguments.putString(FACEBOOK_KEY, mFacebook);
            arguments.putString(SKYPE_KEY, mSkype);
            arguments.putString(TWITTER_KEY, mTwitter);
            arguments.putString(WEBSITE_KEY, mWebsite);


            ProfileFragment fragment = new ProfileFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_profile_container, fragment)
                    .commit();
        }
        else {
            mName = savedInstanceState.getString(NAME_KEY);
            mPartnerId = savedInstanceState.getString(PARTNER_ID_KEY);

            mEmail = savedInstanceState.getString(EMAIL_KEY);
            mPhone = savedInstanceState.getString(PHONE_KEY);
            mAddress = savedInstanceState.getString(ADDRESS_KEY);
            mCity = savedInstanceState.getString(CITY_KEY);
            mState = savedInstanceState.getString(STATE_KEY);
            mPostalCode = savedInstanceState.getString(POSTALCODE_KEY);
            mFullAddress = savedInstanceState.getString(FULLADDRESS_KEY);
            mCell = savedInstanceState.getString(CELL_KEY);
            mSurname = savedInstanceState.getString(SURNAME_KEY);
            mGivenName = savedInstanceState.getString(GIVEN_NAMES_KEY);

            mBlog = savedInstanceState.getString(BLOG_KEY);
            mFax = savedInstanceState.getString(FAX_KEY);
            mFacebook = savedInstanceState.getString(FACEBOOK_KEY);
            mSkype = savedInstanceState.getString(SKYPE_KEY);
            mTwitter = savedInstanceState.getString(TWITTER_KEY);
            mWebsite = savedInstanceState.getString(WEBSITE_KEY);

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

                i.putExtra(EMAIL_KEY, mEmail);
                i.putExtra(PHONE_KEY, mPhone);
                i.putExtra(ADDRESS_KEY, mAddress);
                i.putExtra(CITY_KEY, mCity);
                i.putExtra(STATE_KEY, mState);
                i.putExtra(POSTALCODE_KEY, mPostalCode);
                i.putExtra(FULLADDRESS_KEY, mFullAddress);
                i.putExtra(CELL_KEY, mCell);
                i.putExtra(SURNAME_KEY, mSurname);
                i.putExtra(GIVEN_NAMES_KEY, mGivenName);

                i.putExtra(BLOG_KEY, mBlog);
                i.putExtra(FAX_KEY, mFax);
                i.putExtra(FACEBOOK_KEY, mFacebook);
                i.putExtra(SKYPE_KEY, mSkype);
                i.putExtra(TWITTER_KEY, mTwitter);
                i.putExtra(WEBSITE_KEY, mWebsite);

                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(NAME_KEY, mName);
        savedInstanceState.putString(PARTNER_ID_KEY, mPartnerId);

        savedInstanceState.putString(EMAIL_KEY, mEmail);
        savedInstanceState.putString(PHONE_KEY, mPhone);
        savedInstanceState.putString(ADDRESS_KEY, mAddress);
        savedInstanceState.putString(CITY_KEY, mCity);
        savedInstanceState.putString(STATE_KEY, mState);
        savedInstanceState.putString(POSTALCODE_KEY, mPostalCode);
        savedInstanceState.putString(CELL_KEY, mCell);
        savedInstanceState.putString(SURNAME_KEY, mSurname);
        savedInstanceState.putString(GIVEN_NAMES_KEY, mGivenName);
        savedInstanceState.putString(BLOG_KEY, mBlog);
        savedInstanceState.putString(FAX_KEY, mFax);
        savedInstanceState.putString(FACEBOOK_KEY, mFacebook);
        savedInstanceState.putString(SKYPE_KEY, mSkype);
        savedInstanceState.putString(TWITTER_KEY, mTwitter);
        savedInstanceState.putString(WEBSITE_KEY, mWebsite);

        super.onSaveInstanceState(savedInstanceState);
    }
}
