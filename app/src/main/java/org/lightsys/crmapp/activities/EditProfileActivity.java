package org.lightsys.crmapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.fragments.ProfileInputFragment;

/**
 * Created by cubemaster on 3/11/16.
 *
 * Commented by Judah on 7/26/16.
 * this class pretty much just receives a lot of information and passes it on to the input fragment
 */
public class EditProfileActivity extends AppCompatActivity {

    //lots of constants used for retrieving stuff from bundles
    public static final String LOG_TAG = EditProfileActivity.class.getName();
    private static final String NAME_KEY = "EXTRA_NAME";
    private static final String PARTNER_ID_KEY = "EXTRA_PARTNER_ID";
    public static final String SURNAME_KEY = "EXTRA_SURNAME";
    public static final String GIVEN_NAMES_KEY = "EXTRA_GIVEN_NAMES";
    public static final String PHONE_KEY = "EXTRA_PHONE";
    public static final String CELL_KEY = "EXTRA_CELL";
    public static final String EMAIL_KEY = "EXTRA_EMAIL";
    public static final String ADDRESS_KEY = "EXTRA_ADDRESS";
    public static final String CITY_KEY = "EXTRA_CITY";
    public static final String STATE_KEY = "EXTRA_STATE";
    public static final String POSTALCODE_KEY = "EXTRA_POSTALCODE";

    //these things may not be needed, but are still here just in case
    public static final String PHONE_ID_KEY = "EXTRA_PHONE_ID";
    public static final String CELL_ID_KEY = "EXTRA_CELL_ID";
    public static final String EMAIL_ID_KEY = "EXTRA_EMAIL_ID";

    public static final String PHONE_JSON_ID_KEY = "EXTRA_PHONE_JSON_ID";
    public static final String CELL_JSON_ID_KEY = "EXTRA_CELL_JSON_ID";
    public static final String EMAIL_JSON_ID_KEY = "EXTRA_EMAIL_JSON_ID";
    public static final String ADDRESS_JSON_ID_KEY = "EXTRA_ADDRESS_JSON_ID";
    public static final String PARTNER_JSON_ID_KEY = "EXTRA_PARTNER_JSON_ID";
    public static final String TYPE_JSON_ID_KEY = "EXTRA_TYPE_JSON_ID";

    //these store the stuff after they are retrieved from a bundle
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

    //these may not be needed, but are still here just in case
    private String mPhoneId;
    private String mCellId;
    private String mEmailId;

    private String mPhoneJsonId;
    private String mCellJsonId;
    private String mEmailJsonId;
    private String mAddressJsonId;
    private String mPartnerJsonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mTypeJsonId;
        if(savedInstanceState == null) {
            //retrieve all the things from the extras
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

            mPhoneJsonId = extras.getString(PHONE_JSON_ID_KEY);
            mCellJsonId = extras.getString(CELL_JSON_ID_KEY);
            mEmailJsonId = extras.getString(EMAIL_JSON_ID_KEY);
            mAddressJsonId = extras.getString(ADDRESS_JSON_ID_KEY);
            mPartnerJsonId = extras.getString(PARTNER_JSON_ID_KEY);
            mTypeJsonId = extras.getString(TYPE_JSON_ID_KEY);

            //create a bundle to send on the the input frag
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

            arguments.putString(PHONE_JSON_ID_KEY, mPhoneJsonId);
            arguments.putString(CELL_JSON_ID_KEY, mCellJsonId);
            arguments.putString(EMAIL_JSON_ID_KEY, mEmailJsonId);
            arguments.putString(ADDRESS_JSON_ID_KEY, mAddressJsonId);
            arguments.putString(PARTNER_JSON_ID_KEY, mPartnerJsonId);
            arguments.putString(TYPE_JSON_ID_KEY, mTypeJsonId);

            //start profile input frag and send it everything
            Log.d("EditProfileActivity", "onCreate: " + mName);
            ProfileInputFragment fragment = new ProfileInputFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_profile_input_container, fragment)
                    .commit();
        }
        else {
            //same thing as before, but this time we have a saved instance
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

            mPhoneJsonId = savedInstanceState.getString(PHONE_JSON_ID_KEY);
            mCellJsonId = savedInstanceState.getString(CELL_JSON_ID_KEY);
            mEmailJsonId = savedInstanceState.getString(EMAIL_JSON_ID_KEY);
            mAddressJsonId = savedInstanceState.getString(ADDRESS_JSON_ID_KEY);
            mPartnerJsonId = savedInstanceState.getString(PARTNER_JSON_ID_KEY);
            mTypeJsonId = savedInstanceState.getString(TYPE_JSON_ID_KEY);
        }

        setContentView(R.layout.activity_profile_input);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_input);
        toolbar.setTitle("Edit profile");
        setSupportActionBar(toolbar);

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

        savedInstanceState.putString(PHONE_JSON_ID_KEY, mPhoneJsonId);
        savedInstanceState.putString(CELL_JSON_ID_KEY, mCellJsonId);
        savedInstanceState.putString(EMAIL_JSON_ID_KEY, mEmailJsonId);
        savedInstanceState.putString(ADDRESS_JSON_ID_KEY, mAddressJsonId);
        savedInstanceState.putString(PARTNER_JSON_ID_KEY, mPartnerJsonId);
        //savedInstanceState.putString(TYPE_JSON_ID_KEY, mTypeJsonId);

        super.onSaveInstanceState(savedInstanceState);
    }
}
