package org.lightsys.crmapp;


import android.accounts.Account;
import android.accounts.AccountManager;
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

import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.lightsys.crmapp.data.PatchJson;


/**
 * Created by cubemaster on 3/11/16.
 * Edited by Ca2br and Judah on 7/19/16
 *
 * Allows a user to edit a profile.
 *
 * ToDo find a way to patch to an object that doesn't yet exist
 * ToDo find a way to add a new partner without trashing everything
 */
public class ProfileInputFragment extends Fragment {
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


    private String[] mEditTextData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_input, container, false);

        boolean mNewProfile;

        // Gets user account.
        mAccountManager = AccountManager.get(getContext());
        Account[] accounts = mAccountManager.getAccounts();
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
            mNewProfile = true;
        }

        if(savedInstanceState != null) {
            mEditTextData = savedInstanceState.getStringArray(DATA_ARRAY_KEY);
        }
        else {
            mEditTextData = new String[VIEW_IDS.length];
        }

        Log.d(LOG_TAG, mNewProfile ? "Creating a new profile!" : "Editing an existing profile!");

        //sets up profile picture
        Picasso.with(getActivity())
                .load(R.drawable.persona)
                .into(((ImageView) rootView.findViewById(R.id.profile_input_photo)));

        // Sets up views.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.name_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        firstName = (TextView)rootView.findViewById(R.id.profile_input_name_first);
        lastName = (TextView)rootView.findViewById(R.id.profile_input_name_last);
        phone = (TextView)rootView.findViewById(R.id.profile_input_phone_text);
        countryCode = (TextView)rootView.findViewById(R.id.profile_input_country_code_text);
        areaCode = (TextView)rootView.findViewById(R.id.profile_input_area_code_text);
        email = (TextView)rootView.findViewById(R.id.profile_input_email);
        address = (TextView)rootView.findViewById(R.id.profile_input_street_address);
        city = (TextView)rootView.findViewById(R.id.profile_input_city);
        state = (TextView)rootView.findViewById(R.id.profile_input_state);
        postalCode = (TextView)rootView.findViewById(R.id.profile_input_zip);
        phoneType = (Spinner)rootView.findViewById(R.id.profile_input_phone_spinner);

        //Used to switch between home and mobile.
        phoneType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String type = (String)phoneType.getSelectedItem();
                if (type.equals("Home")){

                    String[] phoneBits = mPhone.split(" ");//split up phone into its parts

                    mCountryCode = phoneBits[0].replaceAll("[^0-9.]", "");//get country code and remove non numbers
                    mAreaCode = phoneBits[1].replaceAll("[^0-9.]", "");//get area code and remove non numbers
                    mPhoneNumber = phoneBits[2].replaceAll("[^0-9.]", "");//get phone number and remove non numbers

                    //set phone number values to views
                    phone.setText(mPhoneNumber);
                    countryCode.setText(mCountryCode);
                    areaCode.setText(mAreaCode);

                    selectedPhone = "home";//used to determine which type of phone to patch to
                }
                else if (type.equals("Mobile")){

                    String[] phoneBits = mCell.split(" ");//split phone into its parts

                    mCountryCode = phoneBits[0].replaceAll("[^0-9.]", "");//get country code and remove non numbers
                    mAreaCode = phoneBits[1].replaceAll("[^0-9.]", "");//get area code and remove non numbers
                    mPhoneNumber = phoneBits[2].replaceAll("[^0-9.]", "");//get phone number and remove non numbers

                    //set phone number values to views
                    phone.setText(mPhoneNumber);
                    countryCode.setText(mCountryCode);
                    areaCode.setText(mAreaCode);

                    selectedPhone = "mobile";//used to determine which type of phone to patch to
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {//default method
                // your code here
            }
        });

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

            /**
             * ToDo: finish putting updated info into Kardia
             *
             */
            @Override
            public void onClick(View v) {
                try {//try statement because jsons and what not

                    //partner json object to patch to kardia
                    JSONObject partnerJson =new JSONObject();
                    partnerJson.put("surname", lastName.getText().toString());
                    partnerJson.put("given_names", firstName.getText().toString());

                    //address json object to patch to kardia
                    JSONObject addressJson = new JSONObject();
                    addressJson.put("location_id", 1);
                    addressJson.put("address_1", address.getText().toString());
                    addressJson.put("city", city.getText().toString());
                    addressJson.put("state_province", state.getText().toString());
                    addressJson.put("postal_code", mPostalCode);

                    //phone json object to patch to kardia
                    JSONObject phoneJson = new JSONObject();
                    phoneJson.put("contact_data", phone.getText().toString());
                    phoneJson.put("phone_country", countryCode.getText().toString());
                    phoneJson.put("phone_area_city", areaCode.getText().toString());

                    //cell json object to patch to kardia
                    JSONObject cellJson = new JSONObject();
                    cellJson.put("contact_data", phone.getText().toString());
                    phoneJson.put("phone_country", countryCode.getText().toString());
                    phoneJson.put("phone_area_city", areaCode.getText().toString());

                    //email json object to patch to kardia
                    JSONObject emailJson = new JSONObject();
                    emailJson.put("contact_data", email.getText().toString());

                    //urls for patching to kardia
                    String partnerUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/" + mPartnerJsonId + "&cx__res_type=element";
                    String addressUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/" + mAddressJsonId + "&cx__res_type=element";
                    String phoneUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/" + mPhoneJsonId + "&cx__res_type=element";
                    String cellUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/" + mCellJsonId + "&cx__res_type=element";
                    String emailUrl = "http://" + mAccountManager.getUserData(mAccount, "server") + ":800/" + mEmailJsonId + "&cx__res_type=element";

                    //set up patch json objects for patching
                    PatchJson patchJson1 = new PatchJson(getContext(), partnerUrl, partnerJson, mAccount);
                    PatchJson patchJson2 = new PatchJson(getContext(), addressUrl, addressJson, mAccount);
                    PatchJson patchJson3 = new PatchJson(getContext(), phoneUrl, phoneJson, mAccount);
                    PatchJson patchJson4 = new PatchJson(getContext(), cellUrl, cellJson, mAccount);
                    PatchJson patchJson5 = new PatchJson(getContext(), emailUrl, emailJson, mAccount);

                    //execute path jsons
                    patchJson1.execute();
                    patchJson2.execute();
                    if(selectedPhone.equals("home")) {//if home phone is selected, patch home
                       patchJson3.execute();
                    }
                    else if(selectedPhone.equals("mobile")) {//if mobile phone is selected, patch mobile
                        patchJson4.execute();
                    }
                    patchJson5.execute();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;//holy junk dude! was this all done in onCreateView??????!!!!!!
    }

    /**
     * Saves info.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putStringArray(DATA_ARRAY_KEY, mEditTextData);
    }

    //I have no idea why this is here
    //probs can be kill
    private int getIndexById(int id) {
        for(int i = 0; i < VIEW_IDS.length; i++) {
            if(id == VIEW_IDS[i]) {
                return i;
            }
        }
        return -1;
    }

}
