package org.lightsys.crmapp.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonIOException;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.activities.EditProfileActivity;
import org.lightsys.crmapp.activities.ProfileActivity;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.PatchJson;
import org.lightsys.crmapp.data.PostJson;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


/**
 * Created by cubemaster on 3/11/16.
 * Edited by Ca2br and Judah on 7/19/16
 *
 * Allows a user to edit a profile.
 *
 * ToDo find a way to patch to an object that doesn't yet exist
 */
public class ProfileInputFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = ProfileInputFragment.class.getName();
    private static final String DATA_ARRAY_KEY = "DATA_ARRAY_KEY";
    private static final int[] VIEW_IDS = new int[]{
            R.id.profile_input_name_first,
            R.id.profile_input_name_last,
            R.id.profile_input_country_code_text,
            R.id.profile_input_email,
            R.id.profile_input_street_address,
            R.id.profile_input_city,
            R.id.profile_input_state,
            R.id.profile_input_zip};

    // Values for authenticating the network.
    private AccountManager mAccountManager;
    private Account mAccount;

    // Values that store profile information.
    private String mName;
    private String mSurname;
    private String mGivenName;
    private String mPartnerId;
    private String mPhone;
    private String mCell;
    private String mCountryCode;
    private String mAreaCode;
    private String mPhoneNumber;
    private String mEmail;
    private String mAddress;
    private String mCity;
    private String mState;
    private String mPostalCode;

    TextView firstName;
    TextView lastName;
    TextView phone; //displays the main part of the phone number
    TextView countryCode; //displays country code
    TextView areaCode; //displays area code. phone is split like this because kardia tables. look at them, you'll understand.
    TextView email;
    TextView address;
    TextView city;
    TextView state;
    TextView postalCode;

    private String selectedPhone = "mobile";

    //These values were thought to be needed, but then it was realized that they were unnecessary.
    //They are still here in case they are needed in the future.
    private String mPhoneId;
    private String mCellId;
    private String mEmailId;

    //These store the url id for phone, email, address, and partner.
    //They are used for patching specific json objects.
    private String mPhoneJsonId;
    private String mCellJsonId;
    private String mEmailJsonId;
    private String mAddressJsonId;
    private String mPartnerJsonId;

    Spinner phoneType; // Switches between home and cell.
    boolean initializedView = false;

    private String[] mEditTextData;

    private String nextPartnerKey;
    boolean mNewProfile = true;
    JSONObject jsonDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_input, container, false);

        // Gets user account.
        mAccountManager = AccountManager.get(getContext());
        final Account[] accounts = mAccountManager.getAccounts();
        if(accounts.length > 0) {
            mAccount = accounts[0];
        }

        // Gets profile information from previous activity.
        Bundle arguments = getArguments();
        if (arguments != null) {
            mName = arguments.getString(ProfileActivity.NAME_KEY);
            mSurname = arguments.getString(EditProfileActivity.SURNAME_KEY);
            mGivenName = arguments.getString(EditProfileActivity.GIVEN_NAMES_KEY);
            mPartnerId = arguments.getString(ProfileActivity.PARTNER_ID_KEY);
            mPhone = arguments.getString(EditProfileActivity.PHONE_KEY);
            mCell = arguments.getString(EditProfileActivity.CELL_KEY);
            mEmail = arguments.getString(EditProfileActivity.EMAIL_KEY);
            mAddress = arguments.getString(EditProfileActivity.ADDRESS_KEY);
            mCity = arguments.getString(EditProfileActivity.CITY_KEY);
            mState = arguments.getString(EditProfileActivity.STATE_KEY);
            mPostalCode = arguments.getString(EditProfileActivity.POSTALCODE_KEY);
            mPhoneId = arguments.getString(EditProfileActivity.PHONE_ID_KEY);
            mCellId = arguments.getString(EditProfileActivity.CELL_ID_KEY);
            mEmailId = arguments.getString(EditProfileActivity.EMAIL_ID_KEY);
            mPhoneJsonId = arguments.getString(EditProfileActivity.PHONE_JSON_ID_KEY);
            mCellJsonId = arguments.getString(EditProfileActivity.CELL_JSON_ID_KEY);
            mEmailJsonId = arguments.getString(EditProfileActivity.EMAIL_JSON_ID_KEY);
            mAddressJsonId = arguments.getString(EditProfileActivity.ADDRESS_JSON_ID_KEY);
            mPartnerJsonId = arguments.getString(EditProfileActivity.PARTNER_JSON_ID_KEY);
            mNewProfile = false;
        }
        else {
            //TODO Fill in JsonId variables
            mPartnerJsonId = "";
            mAddressJsonId = "";
            mPhoneJsonId = "";
            mCellJsonId = "";
            mEmailJsonId = "";
            mNewProfile = true;
        }

        if(savedInstanceState != null) {
            mEditTextData = savedInstanceState.getStringArray(DATA_ARRAY_KEY);
        }
        else {
            mEditTextData = new String[VIEW_IDS.length];
        }

        System.out.println(mNewProfile ? "Creating a new profile!" : "Editing an existing profile!");

        //sets up profile picture
        Picasso.with(getActivity())
                .load(R.drawable.persona)
                .into(((ImageView) rootView.findViewById(R.id.profile_input_photo)));

        // Sets up views.
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.name_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        firstName = (TextView)rootView.findViewById(R.id.profile_input_name_first);
        lastName = (TextView)rootView.findViewById(R.id.profile_input_name_last);
        phone = (TextView)rootView.findViewById(R.id.profile_input_phone_text);
        countryCode = (TextView) rootView.findViewById(R.id.profile_input_country_code_text);
        areaCode = (TextView)rootView.findViewById(R.id.profile_input_area_code_text);
        email = (TextView)rootView.findViewById(R.id.profile_input_email);
        address = (TextView)rootView.findViewById(R.id.profile_input_street_address);
        city = (TextView)rootView.findViewById(R.id.profile_input_city);
        state = (TextView)rootView.findViewById(R.id.profile_input_state);
        postalCode = (TextView)rootView.findViewById(R.id.profile_input_zip);
        phoneType = (Spinner)rootView.findViewById(R.id.profile_input_phone_spinner);

        //Used to switch between home and mobile.
        phoneType.setOnItemSelectedListener(this);

        //Set values to views
        firstName.setText(mGivenName);
        lastName.setText(mSurname);
        email.setText(mEmail);
        address.setText(mAddress);
        city.setText(mCity);
        state.setText(mState);
        postalCode.setText(mPostalCode);

        Button submit = (Button) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    setCurrentDate();

                    AsyncTask<String, Void, String> uploadJson1;
                    AsyncTask<String, Void, String> uploadJson2;
                    AsyncTask<String, Void, String> uploadJson3;
                    AsyncTask<String, Void, String> uploadJson4;
                    AsyncTask<String, Void, String> uploadJson5;

                    if (mNewProfile)
                    {
                        nextPartnerKey = new GetPartnerKey().execute().get();
                        System.out.println("Retrieved New Partner Key: " + nextPartnerKey);

                        //urls for Posting to kardia
                        String partnerUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/apps/kardia/api/partner/Partners?cx__mode=rest&cx__res_format=attrs&cx__res_attrs=basic&cx__res_type=collection";

                        String addressUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/apps/kardia/api/partner/Partners/" + nextPartnerKey + "/Addresses?cx__mode=rest&cx__res_type=collection&cx__res_format=attrs&cx__res_attrs=basic";
                        String phoneUrl = selectedPhone.equals("home")
                                ? "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/apps/kardia/api/partner/Partners/" + nextPartnerKey + "/ContactInfo?cx__mode=rest&cx__res_type=collection&cx__res_format=attrs&cx__res_attrs=basic"
                                : null;
                        String cellUrl = selectedPhone.equals("mobile")
                                ? "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/apps/kardia/api/partner/Partners/" + nextPartnerKey + "/ContactInfo?cx__mode=rest&cx__res_format=attrs&cx__res_attrs=basic&cx__res_type=collection"
                                : null;
                        String emailUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/apps/kardia/api/partner/Partners/" + nextPartnerKey + "/ContactInfo?cx__mode=rest&cx__res_type=collection&cx__res_format=attrs&cx__res_attrs=basic";

                        JSONObject cellJson = createCellJson();
                        System.out.println(cellJson);
                        //set up POST json objects for patching
                        uploadJson1 = new PostJson(getContext(), partnerUrl, createPartnerJson(), mAccount);
                        uploadJson2 = new PostJson(getContext(), addressUrl, createAddressJson(), mAccount);
                        uploadJson3 = new PostJson(getContext(), phoneUrl, createPhoneJson(), mAccount);
                        uploadJson4 = new PostJson(getContext(), cellUrl, cellJson, mAccount);
                        uploadJson5 = new PostJson(getContext(), emailUrl, createEmailJson(), mAccount);
                    }
                    else
                    {
                        //urls for patching to kardia
                        String partnerUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/" + mPartnerJsonId + "&cx__res_type=element";
                        String addressUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/" + mAddressJsonId + "&cx__res_type=element";
                        String phoneUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/" + mPhoneJsonId + "&cx__res_type=element";
                        String cellUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/" + mCellJsonId + "&cx__res_type=element";
                        String emailUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/" + mEmailJsonId + "&cx__res_type=element";

                        //set up patch json objects for patching
                        uploadJson1 = new PatchJson(getContext(), partnerUrl, createPartnerJson(), mAccount);
                        uploadJson2 = new PatchJson(getContext(), addressUrl, createAddressJson(), mAccount);
                        uploadJson3 = new PatchJson(getContext(), phoneUrl, createPhoneJson(), mAccount);
                        uploadJson4 = new PatchJson(getContext(), cellUrl, createCellJson(), mAccount);
                        uploadJson5 = new PatchJson(getContext(), emailUrl, createEmailJson(), mAccount);
                    }

                    uploadJson1.execute();
                    uploadJson2.execute();
                    if(selectedPhone.equals("home")) {//if home phone is selected, patch home
                        System.out.println("POST Phone info");
                        uploadJson3.execute();
                    }
                    else if(selectedPhone.equals("mobile")) {//if mobile phone is selected, patch mobile
                        System.out.println("POST Mobile info");
                        uploadJson4.execute();
                    }
                    System.out.println("POST Email info");
                    uploadJson5.execute();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;//holy junk dude! was this all done in onCreateView??????!!!!!!
    }

    private void setCurrentDate()
    {
        //Get current date
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        try
        {
            //Create Date Json object of current datetime
            jsonDate = new JSONObject();
            jsonDate.put("month", cal.get(Calendar.MONTH));
            jsonDate.put("year", cal.get(Calendar.YEAR));
            jsonDate.put("day", cal.get(Calendar.DAY_OF_MONTH));
            jsonDate.put("minute", cal.get(Calendar.MINUTE));
            jsonDate.put("second", cal.get(Calendar.SECOND));
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
    }

    private JSONObject createEmailJson()
    {
        JSONObject emailJson = new JSONObject();

        try {
            if (mNewProfile)
            {
                emailJson.put("p_partner_key", nextPartnerKey);
                emailJson.put("s_created_by", "tparr");
                emailJson.put("s_modified_by", "tparr");
                emailJson.put("s_date_created", jsonDate);
                emailJson.put("s_date_modified", jsonDate);
                emailJson.put("p_contact_data", email.getText().toString());
                emailJson.put("p_contact_type", "E");
                emailJson.put("p_record_status_code", "A");
            }
            else
            {
                emailJson.put("contact_data", email.getText().toString());
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }

        return emailJson;
    }

    private JSONObject createCellJson()
    {
        JSONObject cellJson = new JSONObject();

        try {
            String countryCodeTemp = countryCode.getText().toString();
            String actualCountryCode = countryCodeTemp.equals("") ? "1" : countryCodeTemp.replace("+", "");

            if (mNewProfile)
            {
                cellJson.put("p_partner_key", nextPartnerKey);
                cellJson.put("s_created_by", "tparr");
                cellJson.put("s_modified_by", "tparr");
                cellJson.put("s_date_created", jsonDate);
                cellJson.put("s_date_modified", jsonDate);
                cellJson.put("p_contact_data", phone.getText().toString());
                cellJson.put("p_phone_country", actualCountryCode);
                cellJson.put("p_phone_area_city", areaCode.getText().toString());
                cellJson.put("p_contact_type", "C");
                cellJson.put("p_record_status_code", "A");
            }
            else
            {
                cellJson.put("contact_data", phone.getText().toString());
                cellJson.put("phone_country", actualCountryCode);
                cellJson.put("phone_area_city", areaCode.getText().toString());
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }

        return cellJson;
    }

    private JSONObject createPhoneJson()
    {
        JSONObject phoneJson = new JSONObject();

        try {

            String countryCodeTemp = countryCode.getText().toString();
            String actualCountryCode = countryCodeTemp.equals("") ? "1" : countryCodeTemp.replace("+", "");

            if(mNewProfile)
            {
                phoneJson.put("p_partner_key", nextPartnerKey);
                phoneJson.put("s_created_by", "tparr");
                phoneJson.put("s_modified_by", "tparr");
                phoneJson.put("s_date_created", jsonDate);
                phoneJson.put("s_date_modified", jsonDate);
                phoneJson.put("p_contact_data", phone.getText().toString());
                phoneJson.put("p_phone_country", actualCountryCode);
                phoneJson.put("p_phone_area_city", areaCode.getText().toString());
                phoneJson.put("p_contact_type", "P");
                phoneJson.put("p_record_status_code", "A");
            }
            else
            {
                phoneJson.put("contact_data", phone.getText().toString());
                phoneJson.put("phone_country", actualCountryCode);
                phoneJson.put("phone_area_city", areaCode.getText().toString());
            }

        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        return phoneJson;
    }

    private JSONObject createAddressJson()
    {
        JSONObject addressJson = new JSONObject();

        try {
            if (mNewProfile)
            {
                addressJson.put("p_partner_key", nextPartnerKey);
                addressJson.put("s_created_by", "tparr");
                addressJson.put("s_modified_by", "tparr");
                addressJson.put("s_date_created", jsonDate);
                addressJson.put("s_date_modified", jsonDate);
                addressJson.put("p_location_id", 0);
                addressJson.put("p_revision_id", 0);
                addressJson.put("p_record_status_code", "A");
                addressJson.put("p_address_1", address.getText().toString());
                addressJson.put("p_city", city.getText().toString());
                addressJson.put("p_state_province", state.getText().toString());
                addressJson.put("p_postal_code", postalCode.getText());
            }
            else
            {
                addressJson.put("location_id", 1);
                addressJson.put("address_1", address.getText().toString());
                addressJson.put("city", city.getText().toString());
                addressJson.put("state_province", state.getText().toString());
                addressJson.put("postal_code", postalCode.getText());
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        return addressJson;
    }

    private JSONObject createPartnerJson()
    {
        JSONObject partnerJson = new JSONObject();

        try
        {
            if (mNewProfile)
            {
                partnerJson.put("p_partner_key", nextPartnerKey);
                partnerJson.put("s_created_by", "tparr");
                partnerJson.put("s_modified_by", "tparr");
                partnerJson.put("p_creating_office", "100054");
                partnerJson.put("p_status_code", "A");
                partnerJson.put("p_partner_class", "123");
                partnerJson.put("p_record_status_code", "A");
                partnerJson.put("p_surname", lastName.getText().toString());
                partnerJson.put("p_given_name", firstName.getText().toString());
                partnerJson.put("s_date_created", jsonDate);
                partnerJson.put("s_date_modified", jsonDate);

            } else
            {
                partnerJson.put("surname", lastName.getText().toString());
                partnerJson.put("given_names", firstName.getText().toString());
            }
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        return partnerJson;
    }

    /**
     * Saves info.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putStringArray(DATA_ARRAY_KEY, mEditTextData);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        if (!initializedView)
        {
            initializedView = true;
            return;
        }
        String type = (String)phoneType.getSelectedItem();
        if (type.equals("Home")){

            if (mPhone == null) return;

            String[] phoneBits = mPhone.split(" ");//split up phone into its parts

            mCountryCode = phoneBits[0].replaceAll("[^0-9.]", "");//get country code and remove non numbers
            mAreaCode = phoneBits[1].replaceAll("[^0-9.]", "");//get area code and remove non numbers
            mPhoneNumber = phoneBits[2].replaceAll("[^0-9.]", "");//get phone number and remove non numbers

            //set phone number values to views
            phone.setText(mPhoneNumber);
//            countryCode.setCountryForPhoneCode(Integer.parseInt(mCountryCode));
            areaCode.setText(mAreaCode);

            selectedPhone = "home";//used to determine which type of phone to patch to
        }
        else if (type.equals("Mobile")){

            if (mCell == null)
                return;

            String[] phoneBits = mCell.split(" ");//split phone into its parts

            mCountryCode = phoneBits[0].replaceAll("[^0-9.]", "");//get country code and remove non numbers
            mAreaCode = phoneBits[1].replaceAll("[^0-9.]", "");//get area code and remove non numbers
            mPhoneNumber = phoneBits[2].replaceAll("[^0-9.]", "");//get phone number and remove non numbers

            //set phone number values to views
            phone.setText(mPhoneNumber);
//            countryCode.setCountryForPhoneCode(Integer.parseInt(mCountryCode));
            areaCode.setText(mAreaCode);

            selectedPhone = "mobile";//used to determine which type of phone to patch to
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }

    private class GetPartnerKey extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids)
        {
            KardiaFetcher fetcher = new KardiaFetcher(getContext());

            return fetcher.getNextPartnerKey(mAccount);
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            nextPartnerKey = s;
        }
    }
}
