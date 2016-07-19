package org.lightsys.crmapp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by cubemaster on 3/11/16.
 */
public class EditProfileActivity extends AppCompatActivity {
    public static final String LOG_TAG = EditProfileActivity.class.getName();
    public static final String NAME_KEY = "EXTRA_NAME";
    public static final String PARTNER_ID_KEY = "EXTRA_PARTNER_ID";
    public static final String SURNAME_KEY = "EXTRA_SURNAME";
    public static final String GIVEN_NAMES_KEY = "EXTRA_GIVEN_NAMES";
    public static final String PHONE_KEY = "EXTRA_PHONE";
    public static final String CELL_KEY = "EXTRA_CELL";
    public static final String EMAIL_KEY = "EXTRA_EMAIL";
    public static final String ADDRESS_KEY = "EXTRA_ADDRESS";
    public static final String CITY_KEY = "EXTRA_CITY";
    public static final String STATE_KEY = "EXTRA_STATE";
    public static final String POSTALCODE_KEY = "EXTRA_POSTALCODE";

    private String mName;
    private String mSurname;
    private String mGivenName;
    private String mPartnerId;
    private String mPhone;
    private String mCell;
    private String mEmail;
    private String mAddress;
    private String mCity;
    private String mState;
    private String mPostalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            mName = extras.getString(NAME_KEY);
            mSurname = extras.getString(SURNAME_KEY);
            mGivenName = extras.getString(GIVEN_NAMES_KEY);
            mPartnerId = extras.getString(PARTNER_ID_KEY);
            mPhone = extras.getString(PHONE_KEY);
            mCell = extras.getString(CELL_KEY);
            mEmail = extras.getString(EMAIL_KEY);
            mAddress = extras.getString(ADDRESS_KEY);
            mCity = extras.getString(CITY_KEY);
            mState = extras.getString(STATE_KEY);
            mPostalCode = extras.getString(POSTALCODE_KEY);



            Bundle arguments = new Bundle();
            arguments.putString(NAME_KEY, mName);
            arguments.putString(SURNAME_KEY, mSurname);
            arguments.putString(GIVEN_NAMES_KEY, mGivenName);
            arguments.putString(PARTNER_ID_KEY, mPartnerId);
            arguments.putString(PHONE_KEY, mPhone);
            arguments.putString(CELL_KEY, mCell);
            arguments.putString(EMAIL_KEY, mEmail);
            arguments.putString(ADDRESS_KEY, mAddress);
            arguments.putString(CITY_KEY, mCity);
            arguments.putString(STATE_KEY, mState);
            arguments.putString(POSTALCODE_KEY, mPostalCode);

            ProfileInputFragment fragment = new ProfileInputFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_profile_input_container, fragment)
                    .commit();
        }
        else {
            mName = savedInstanceState.getString(NAME_KEY);
            mSurname = savedInstanceState.getString(SURNAME_KEY);
            mGivenName = savedInstanceState.getString(GIVEN_NAMES_KEY);
            mPartnerId = savedInstanceState.getString(PARTNER_ID_KEY);
            mPhone = savedInstanceState.getString(PHONE_KEY);
            mCell = savedInstanceState.getString(CELL_KEY);
            mEmail = savedInstanceState.getString(EMAIL_KEY);
            mAddress = savedInstanceState.getString(ADDRESS_KEY);
            mCity = savedInstanceState.getString(CITY_KEY);
            mState = savedInstanceState.getString(STATE_KEY);
            mPostalCode = savedInstanceState.getString(POSTALCODE_KEY);
        }

        setContentView(R.layout.activity_profile_input);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_input);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit profile");

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(NAME_KEY, mName);
        savedInstanceState.putString(SURNAME_KEY, mSurname);
        savedInstanceState.putString(GIVEN_NAMES_KEY, mGivenName);
        savedInstanceState.putString(PARTNER_ID_KEY, mPartnerId);
        savedInstanceState.putString(PHONE_KEY, mPhone);
        savedInstanceState.putString(CELL_KEY, mCell);
        savedInstanceState.putString(EMAIL_KEY, mEmail);
        savedInstanceState.putString(ADDRESS_KEY, mAddress);
        savedInstanceState.putString(CITY_KEY, mCity);
        savedInstanceState.putString(STATE_KEY, mState);
        savedInstanceState.putString(POSTALCODE_KEY, mPostalCode);

        super.onSaveInstanceState(savedInstanceState);
    }
}
