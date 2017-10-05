package org.lightsys.crmapp.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.activities.MainActivity;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.data.PostJson;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by otter57 on 9/14/17.
 *
 * Allows a user to edit/create a signUpList.
 *
 */
public class SignUpFragment extends Fragment{
    private static final String LOG_TAG = SignUpFragment.class.getName();
    private static final String DATA_ARRAY_KEY = "DATA_ARRAY_KEY";
    private static final int[] VIEW_IDS = new int[]{
            R.id.profile_input_name_first,
            R.id.profile_input_name_last,
            R.id.profile_input_email,
            R.id.input_grad_year,
            R.id.profile_input_internship,
            R.id.profile_input_one_year,
            R.id.profile_input_long_term,
            R.id.profile_input_spring_break};


    TextView firstName, lastName, email, gradYear;
    CheckBox longTerm, internship, oneYear, springBreak;
    String mFirstName, mLastName, mEmail, mGradYear;
    int formId;

    //For adding person to server
    private String nextPartnerKey;
    private JSONObject jsonDate;
    private AccountManager mAccountManager;
    private Account mAccount;
    private String mEmailJsonId;
    private String mAddressJsonId;
    private String mPartnerJsonId;
    private String mTypeJsonId;
    private boolean mNewProfile;
    Spinner collabType; //Chooses user role.
    private int collabTypeNumber; //Used to turn role into a number.
    private String[] mEditTextData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        formId = getArguments().getInt(FormFragment.FORM_ID);
        View rootView = inflater.inflate(R.layout.fragment_signup_sheet_input, container, false);
        getActivity().setTitle("Sign up");

        // Sets up views.
        firstName = (TextView)rootView.findViewById(R.id.profile_input_name_first);
        lastName = (TextView)rootView.findViewById(R.id.profile_input_name_last);
        email = (TextView)rootView.findViewById(R.id.profile_input_email);
        gradYear = (TextView)rootView.findViewById(R.id.input_grad_year);
        internship = (CheckBox)rootView.findViewById(R.id.profile_input_internship);
        oneYear = (CheckBox)rootView.findViewById(R.id.profile_input_one_year);
        longTerm = (CheckBox)rootView.findViewById(R.id.profile_input_long_term);
        springBreak = (CheckBox)rootView.findViewById(R.id.profile_input_spring_break);

        Button submit = (Button) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mEmail = email.getText().toString();
                mFirstName = firstName.getText().toString();
                mLastName = lastName.getText().toString();
                mGradYear = gradYear.getText().toString();

                if (checkValidInput()) {
                    //todo submitOnClick();
                    addUserToDB();
                    openForm(formId);

                }
            }
        });

        ((MainActivity) getActivity()).showNavButton(false);

        if(savedInstanceState != null) {
            mEditTextData = savedInstanceState.getStringArray(DATA_ARRAY_KEY);
        }
        else {
            mEditTextData = new String[VIEW_IDS.length];
        }

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        return rootView;
    }

    @Override
    public void onDestroyView(){

        ((MainActivity) getActivity()).showNavButton(true);
        super.onDestroyView();
    }

    private boolean checkValidInput(){
        String output = "";
        boolean valid = true;

        if (mFirstName == null || mLastName == null || mFirstName.length()<=1 || mLastName.length()<=1){
            output = "Error: Please enter your full name";
            valid = false;
        }else if (mLastName.matches(".*\\d+.*") || mFirstName.matches(".*\\d+.*")){
            output = "Error: Please enter a name containing only letters";
            valid = false;
        }else if (mEmail == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()){
            output = "Error: Please enter valid email address";
            valid = false;
        }else if (gradYear.getText().length()!=4 || !gradYear.getText().toString().matches("[0-9]+")){
            output = "Error: Please enter valid graduation year (i.e. 2020)";
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this.getActivity(), output, Toast.LENGTH_SHORT).show();
        }

        return valid;
    }


    private void addUserToDB(){

        //create Tags string
        //todo get category names to database
        String tags = "";
        tags += internship.isChecked()? "internship,":null;
        tags += oneYear.isChecked()? "oneYear,":null;
        tags += longTerm.isChecked()? "longTerm,":null;
        tags += springBreak.isChecked()? "springBreak,":null;

        ContentValues values = new ContentValues();
        //todo let id be assigned by server
        values.put(LocalDBTables.ConnectionTable.CONNECTION_ID, getConnectionId());
        values.put(LocalDBTables.ConnectionTable.FORM_ID, formId);
        values.put(LocalDBTables.ConnectionTable.CONNECTION_NAME, lastName.getText() + ", " + firstName.getText());
        values.put(LocalDBTables.ConnectionTable.CONNECTION_EMAIL, email.getText().toString());
        values.put(LocalDBTables.ConnectionTable.CONNECTION_GRAD_YEAR, gradYear.getText().toString());
        values.put(LocalDBTables.ConnectionTable.CONNECTION_TAGS, tags);
        getActivity().getContentResolver().insert(LocalDBTables.ConnectionTable.CONTENT_URI, values);

    }

//todo remove once id assigned by server
    private int getConnectionId(){
        int connId=-1;

        Cursor cursor = getActivity().getContentResolver().query(
                LocalDBTables.ConnectionTable.CONTENT_URI,
                new String[] {LocalDBTables.ConnectionTable.CONNECTION_ID,},
                null, null, LocalDBTables.ConnectionTable.CONNECTION_ID
        );
        try {
            while (cursor.moveToNext()) {
                connId = cursor.getInt(0);
            }
        }catch(NullPointerException ne) {
            ne.printStackTrace();
        }
        cursor.close();
        connId+=1;

        Log.d("SignUpSheetInputFrag", "getConnectionId: " + connId);
        return connId;
    }

    public void openForm(int formId){
        FormFragment newFrag = new FormFragment();

        Bundle args = new Bundle();
        args.putInt(FormListFragment.FORM_ID, formId);

        newFrag.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, newFrag, "Form").addToBackStack("Form")
                .commit();
    }

    private void submitOnClick()
    {
        try {
            setCurrentDate();

            AsyncTask<String, Void, String> uploadJson1;
            AsyncTask<String, Void, String> uploadJson2;
            AsyncTask<String, Void, String> uploadJson3;
            AsyncTask<String, Void, String> uploadJson4;
            AsyncTask<String, Void, String> uploadJson5;
            AsyncTask<String, Void, String> uploadJson6;

            //Urls for Posting/Patching
            String partnerUrl;
            String emailUrl;
            String typeUrl;
            String photoUrl;

            mAccountManager = AccountManager.get(getContext());
            final Account[] accounts = mAccountManager.getAccounts();
            if (accounts.length > 0) {
                mAccount = accounts[0];
            }

            //todo check for duplicate signups
            mNewProfile = true;
            if (mNewProfile) {
                nextPartnerKey = new GetPartnerKey().execute().get();
                System.out.println("Retrieved New Partner Key: " + nextPartnerKey);

                //Set urls for Posting to kardia
                partnerUrl = createPostUrl("partner/Partners");
                emailUrl = createPostUrl("partner/Partners/" + nextPartnerKey + "/ContactInfo");
                typeUrl = createPostUrl("crm/Partners/" + mAccountManager.getUserData(mAccount, "partnerId") + "/Collaboratees");

                //set up POST json objects for patching
                uploadJson1 = new PostJson(getContext(), partnerUrl, createPartnerJson(), mAccount, false);
                uploadJson5 = new PostJson(getContext(), emailUrl, createEmailJson(), mAccount, false);
                uploadJson6 = new PostJson(getContext(), typeUrl, createTypeJson(), mAccount, true);
            /*} else {
                //Set urls and json objects for Patching to kardia
                //If no existing url, use a Post call instead
                if (mPartnerJsonId != null)
                {
                    partnerUrl = createPatchUrl(mPartnerJsonId);
                    uploadJson1 = new PatchJson(getContext(), partnerUrl, createPartnerJson(), mAccount, false);
                } else
                {
                    partnerUrl = createPostUrl("partner/Partners");
                    uploadJson1 = new PostJson(getContext(), partnerUrl, createPartnerJson(), mAccount, false);
                }

                if (mAddressJsonId != null)
                {
                    addressUrl = createPatchUrl(mAddressJsonId);
                    uploadJson2 = new PatchJson(getContext(), addressUrl, createAddressJson(), mAccount, false);
                } else
                {
                    addressUrl = createPostUrl("partner/Partners/" + mPartnerId + "/Addresses");
                    uploadJson2 = new PostJson(getContext(), addressUrl, createAddressJson(), mAccount, false);
                }

                if (mPhoneJsonId != null)
                {
                    phoneUrl = createPatchUrl(mPhoneJsonId);
                    uploadJson3 = new PatchJson(getContext(), phoneUrl, createPhoneJson(), mAccount, false);
                } else
                {
                    phoneUrl = selectedPhone.equals("home")
                            ? createPostUrl("partner/Partners/" + mPartnerId + "/ContactInfo")
                            : null;
                    uploadJson3 = new PostJson(getContext(), phoneUrl, createPhoneJson(), mAccount, false);
                }

                if (mCellJsonId != null)
                {
                    cellUrl = createPatchUrl(mCellJsonId);
                    uploadJson4 = new PatchJson(getContext(), cellUrl, createCellJson(), mAccount, false);
                } else
                {
                    cellUrl = selectedPhone.equals("mobile")
                            ? createPostUrl("partner/Partners/" + mPartnerId + "/ContactInfo")
                            : null;
                    uploadJson4 = new PostJson(getContext(), cellUrl, createCellJson(), mAccount, false);
                }

                if (mEmailJsonId != null)
                {
                    emailUrl = createPatchUrl(mEmailJsonId);
                    uploadJson5 = new PatchJson(getContext(), emailUrl, createEmailJson(), mAccount, false);
                } else
                {
                    emailUrl = createPostUrl("partner/Partners/" + mPartnerId + "/ContactInfo");
                    uploadJson5 = new PostJson(getContext(), emailUrl, createEmailJson(), mAccount, false);
                }

                if (mTypeJsonId != null)
                {
                    typeUrl = createPatchUrl(mTypeJsonId);
                    uploadJson6 = new PatchJson(getContext(), typeUrl, createTypeJson(), mAccount, true);
                } else
                {
                    typeUrl = createPostUrl("crm/Partners/" + mAccountManager.getUserData(mAccount, "partnerId") + "/Collaboratees");
                    uploadJson6 = new PostJson(getContext(), typeUrl, createTypeJson(), mAccount, true);
                }

                postProfilePicture = null;
            }*/

            uploadJson1.execute();

            System.out.println("POST Email info");
            uploadJson5.execute();
            uploadJson6.execute();
        }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
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
            jsonDate.put("hour", cal.get(Calendar.HOUR));
            jsonDate.put("minute", cal.get(Calendar.MINUTE));
            jsonDate.put("second", cal.get(Calendar.SECOND));
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
    }

    private JSONObject createPartnerJson()
    {
        JSONObject partnerJson = new JSONObject();
        //todo if add edit option: " " should change to mPartnerId;
        String partner = mNewProfile ? nextPartnerKey : " ";

        try
        {
            if (mNewProfile || mPartnerJsonId == null)
            {
                partnerJson.put("p_partner_key", partner);
                partnerJson.put("s_created_by", mAccount.name);
                partnerJson.put("s_modified_by", mAccount.name);
                partnerJson.put("p_creating_office", mAccountManager.getUserData(mAccount, "partnerId"));
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

    private JSONObject createEmailJson()
    {
        JSONObject emailJson = new JSONObject();
        //todo if add edit option: " " should change to mPartnerId;
        String partner = mNewProfile ? nextPartnerKey : " ";

        try {
            if (mNewProfile || mEmailJsonId == null)
            {
                emailJson.put("p_partner_key", partner);
                emailJson.put("s_created_by", mAccount.name);
                emailJson.put("s_modified_by", mAccount.name);
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

    private JSONObject createTypeJson()
    {

        //Convert collaborator type to a number
        collabTypeNumber = 1; //code for mobilizer

        JSONObject typeJson = new JSONObject();
        //todo if add edit option: " " should change to mPartnerId;
        String partner = mNewProfile ? nextPartnerKey : " ";

        try
        {
            if (mNewProfile || mTypeJsonId == null)
            {
                typeJson.put("e_collaborator", mAccountManager.getUserData(mAccount, "partnerId"));
                typeJson.put("p_partner_key", partner);
                typeJson.put("e_collab_type_id", collabTypeNumber);
                typeJson.put("e_is_automatic", 0);
                typeJson.put("e_collaborator_status", "A");
                //todo set as account username
                typeJson.put("s_created_by", mAccountManager.getUserData(mAccount, "partnerId"));
                typeJson.put("s_modified_by", "dgarcia");
                typeJson.put("s_date_created", jsonDate);
                typeJson.put("s_date_modified", jsonDate);
                typeJson.put( "collaborator_id", mAccountManager.getUserData(mAccount, "partnerId"));
                typeJson.put( "collaborator_type_id", collabTypeNumber);
                typeJson.put( "collaborator_status", "A");
                typeJson.put( "partner_id", partner);
                typeJson.put( "role_id", collabTypeNumber);
                typeJson.put( "role_name", "Mobilizer");



            } else
            {
                typeJson.put("collab_type_id", collabTypeNumber);
            }
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        return typeJson;
    }

    /**
     * Saves info.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putStringArray(DATA_ARRAY_KEY, mEditTextData);
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

    private String createPostUrl(String url){
        String postUrl = mAccountManager.getUserData(mAccount, "server") + "/apps/kardia/api/"
                + url
                + "?cx__mode=rest&cx__res_format=attrs&cx__res_attrs=basic&cx__res_type=collection";
        return postUrl;
    }

    private String createPatchUrl(String JsonId){
        return mAccountManager.getUserData(mAccount, "server") + JsonId + "&cx__res_type=element";
    }
}