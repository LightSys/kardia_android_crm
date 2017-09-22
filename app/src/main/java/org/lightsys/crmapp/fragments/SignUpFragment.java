package org.lightsys.crmapp.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.data.PatchJson;
import org.lightsys.crmapp.data.PostJson;
import org.lightsys.crmapp.data.PostProfilePicture;

import java.io.File;
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

    TextView firstName, lastName, email, gradYear;
    CheckBox longTerm, internship, oneYear, springBreak;
    String mFirstName, mLastName, mEmail, mGradYear;
    int formId;

    //todo for adding account
    private String nextPartnerKey;
    private JSONObject jsonDate;
    private AccountManager mAccountManger;

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


                //submitOnClick();
                if (checkValidInput()) {
                    addUserToDB();

                    openForm(formId);

                }
            }
        });

        return rootView;
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
        //todo match category names to database
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

        connId+=1;

        Log.d("SignUpSheetInputFrag", "getConnectionId: " + connId);
        return connId;
    }


    public void openForm(int formId){
        FormFragment newFrag = new FormFragment();

        Bundle args = new Bundle();
        args.putInt(FormListFragment.FORM_ID, formId);

        newFrag.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_profile_input_container, newFrag, "Form");
        transaction.addToBackStack("Form");
        transaction.commit();
    }
    //todo add complete button - require password that matches user password. block back button or app close unless user password is entered

    /*/todo everything follow is to add users to account
    private Account getAccount(){
        // Gets user account.
        AccountManager mAccountManager = AccountManager.get(getContext());
        final Account[] accounts = mAccountManager.getAccounts();
        if(accounts.length > 0) {
            return accounts[0];
        }
        return null;
    }

    private class GetPartnerKey extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids)
        {
            KardiaFetcher fetcher = new KardiaFetcher(getContext());

            return fetcher.getNextPartnerKey(getAccount());
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            nextPartnerKey = s;
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

    private void submitOnClick() {
        try {
            setCurrentDate();

            AsyncTask<String, Void, String> uploadJson1;
            AsyncTask<String, Void, String> uploadJson2;
            AsyncTask<String, Void, String> uploadJson3;
            AsyncTask<String, Void, String> uploadJson4;
            AsyncTask<String, Void, String> uploadJson5;
            AsyncTask<String, Void, String> uploadJson6;
            PostProfilePicture postProfilePicture;

            //Urls for Posting/Patching
            String partnerUrl;
            String addressUrl;
            String phoneUrl;
            String cellUrl;
            String emailUrl;
            String typeUrl;
            String photoUrl;

            //todo set check to ensure user is new
            if (true) {
                nextPartnerKey = new GetPartnerKey().execute().get();
                System.out.println("Retrieved New Partner Key: " + nextPartnerKey);

                //Set urls for Posting to kardia
                partnerUrl = createPostUrl("partner/Partners");
                addressUrl = createPostUrl("partner/Partners/" + nextPartnerKey + "/Addresses");
                phoneUrl = selectedPhone.equals("home")
                        ? createPostUrl("partner/Partners/" + nextPartnerKey + "/ContactInfo")
                        : null;
                cellUrl = selectedPhone.equals("mobile")
                        ? createPostUrl("partner/Partners/" + nextPartnerKey + "/ContactInfo")
                        : null;
                emailUrl = createPostUrl("partner/Partners/" + nextPartnerKey + "/ContactInfo");
                typeUrl = createPostUrl("crm/Partners/" + mAccountManager.getUserData(mAccount, "partnerId") + "/Collaboratees");
                photoUrl = mAccountManager.getUserData(mAccount, "server") + "/apps/kardia/files?";

                //set up POST json objects for patching
                uploadJson1 = new PostJson(getContext(), partnerUrl, createPartnerJson(), mAccount, false);
                uploadJson2 = new PostJson(getContext(), addressUrl, createAddressJson(), mAccount, false);
                uploadJson3 = new PostJson(getContext(), phoneUrl, createPhoneJson(), mAccount, false);
                uploadJson4 = new PostJson(getContext(), cellUrl, createCellJson(), mAccount, false);
                uploadJson5 = new PostJson(getContext(), emailUrl, createEmailJson(), mAccount, false);
                uploadJson6 = new PostJson(getContext(), typeUrl, createTypeJson(), mAccount, true);
                String realPathFromURI = getRealPathFromURI(selectedImageUri, getContext());
                postProfilePicture = new PostProfilePicture(getContext(), photoUrl, new File(realPathFromURI), mAccount, nextPartnerKey);
            } else {
                //Set urls and json objects for Patching to kardia
                //If no existing url, use a Post call instead
                if (mPartnerJsonId != null) {
                    partnerUrl = createPatchUrl(mPartnerJsonId);
                    uploadJson1 = new PatchJson(getContext(), partnerUrl, createPartnerJson(), mAccount, false);
                } else {
                    partnerUrl = createPostUrl("partner/Partners");
                    uploadJson1 = new PostJson(getContext(), partnerUrl, createPartnerJson(), mAccount, false);
                }

                if (mAddressJsonId != null) {
                    addressUrl = createPatchUrl(mAddressJsonId);
                    uploadJson2 = new PatchJson(getContext(), addressUrl, createAddressJson(), mAccount, false);
                } else {
                    addressUrl = createPostUrl("partner/Partners/" + mPartnerId + "/Addresses");
                    uploadJson2 = new PostJson(getContext(), addressUrl, createAddressJson(), mAccount, false);
                }

                if (mPhoneJsonId != null) {
                    phoneUrl = createPatchUrl(mPhoneJsonId);
                    uploadJson3 = new PatchJson(getContext(), phoneUrl, createPhoneJson(), mAccount, false);
                } else {
                    phoneUrl = selectedPhone.equals("home")
                            ? createPostUrl("partner/Partners/" + mPartnerId + "/ContactInfo")
                            : null;
                    uploadJson3 = new PostJson(getContext(), phoneUrl, createPhoneJson(), mAccount, false);
                }

                if (mCellJsonId != null) {
                    cellUrl = createPatchUrl(mCellJsonId);
                    uploadJson4 = new PatchJson(getContext(), cellUrl, createCellJson(), mAccount, false);
                } else {
                    cellUrl = selectedPhone.equals("mobile")
                            ? createPostUrl("partner/Partners/" + mPartnerId + "/ContactInfo")
                            : null;
                    uploadJson4 = new PostJson(getContext(), cellUrl, createCellJson(), mAccount, false);
                }

                if (mEmailJsonId != null) {
                    emailUrl = createPatchUrl(mEmailJsonId);
                    uploadJson5 = new PatchJson(getContext(), emailUrl, createEmailJson(), mAccount, false);
                } else {
                    emailUrl = createPostUrl("partner/Partners/" + mPartnerId + "/ContactInfo");
                    uploadJson5 = new PostJson(getContext(), emailUrl, createEmailJson(), mAccount, false);
                }

                if (mTypeJsonId != null) {
                    typeUrl = createPatchUrl(mTypeJsonId);
                    uploadJson6 = new PatchJson(getContext(), typeUrl, createTypeJson(), mAccount, true);
                } else {
                    typeUrl = createPostUrl("crm/Partners/" + mAccountManager.getUserData(mAccount, "partnerId") + "/Collaboratees");
                    uploadJson6 = new PostJson(getContext(), typeUrl, createTypeJson(), mAccount, true);
                }

                postProfilePicture = null;
            }

            uploadJson1.execute();
            uploadJson2.execute();
            if (selectedPhone.equals("home")) {//if home phone is selected, patch home
                System.out.println("POST Phone info");
                uploadJson3.execute();
            } else if (selectedPhone.equals("mobile")) {//if mobile phone is selected, patch mobile
                System.out.println("POST Mobile info");
                uploadJson4.execute();
            }
            System.out.println("POST Email info");
            uploadJson5.execute();
            uploadJson6.execute();
            if (postProfilePicture != null)
                System.out.println("Posting Profile Picture");
            postProfilePicture.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createPostUrl(String url){
        String postUrl = mAccountManager.getUserData(mAccount, "server") + "/apps/kardia/api/"
                + url
                + "?cx__mode=rest&cx__res_format=attrs&cx__res_attrs=basic&cx__res_type=collection";
        return postUrl;
    }*/

}