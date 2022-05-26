package org.lightsys.crmapp.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.data.PostJson;
import org.lightsys.crmapp.models.Connection;
import org.lightsys.crmapp.models.Form;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author otter57
 * created on 9/14/2017.
 *
 * Displays sign up sheet, allows users to add themselves to the list
 *
 */
public class FormFragment extends Fragment{

    private ArrayList<Connection> formConnections;
    private String[] tags;
    private String[] signUpTagInterests, signUpTagSkills;

    final static public String FORM_ID = "form_id";
    final static public String SIGNUP_TAGS_INTERESTS = "signup_tags_interests";
    final static public String SIGNUP_TAGS_SKILLS = "signup_tags_skills";
    final static public String INTEREST_FORM = "interest_form";
    private int formId = -1;
    private final String TAG = "SignUpListFrag";
    private LayoutInflater inflater;
    private TableLayout table;
    private Form form;
    private boolean interestForm;

    //For adding person to server
    private String nextPartnerKey;
    private JSONObject jsonDate;
    private AccountManager mAccountManager;
    private Account mAccount;
    private boolean mNewProfile;
    private String[] mEditTextData;

    //These store the url id for phone, email, address, and partner.
    //They are used for posting specific json objects.
    private String mEmailJsonId = null;
    private String mAddressJsonId = null;
    private String mPartnerJsonId = null;
    private String mTypeJsonId = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sign_up_sheet_table_layout, container, false);
        getActivity().setTitle("Sign up form");
        this.inflater = inflater;
        table = (TableLayout) v;

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TableRow row = (TableRow) v.findViewById(R.id.form_row);
        TextView[] checkBox = {(TextView) v.findViewById(R.id.tag1),(TextView) v.findViewById(R.id.tag2),
                (TextView) v.findViewById(R.id.tag3), (TextView) v.findViewById(R.id.tag4)};
        FrameLayout[] layouts = {(FrameLayout) v.findViewById(R.id.tag1Layout), (FrameLayout) v.findViewById(R.id.tag2Layout),
                (FrameLayout) v.findViewById(R.id.tag3Layout), (FrameLayout) v.findViewById(R.id.tag4Layout)};

        formId = getArguments().getInt(FORM_ID);
        getTags();

        setCheckBoxes(row, null, layouts, null, checkBox);


        getActivity().getSupportFragmentManager().popBackStack("SignUp",0);
        getActivity().getSupportFragmentManager().popBackStack("Form",0);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getForm(formId);

        TextView description = (TextView) v.findViewById(R.id.form_description);
        description.setText(form.getFormDescription());

        return v;
    }


    public void onStart(){
        super.onStart();

        getFormPeople(formId);

        displayPeople();

        setUpAddButton();

        setUpCompleteButton();
    }

    public void onDestroyView(){
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE);

        super.onDestroyView();
    }

    private void setUpAddButton() {
        TableRow addPersonButton = (TableRow) inflater.inflate(R.layout.sign_up_element_table_row,
                (ViewGroup) getView().findViewById(R.id.add_sign_up), false);

        //set up layout and checkboxes based on tags
        TableRow row = (TableRow) addPersonButton.findViewById(R.id.add_sign_up);
        CheckBox[] checkBox = {(CheckBox) addPersonButton.findViewById(R.id.tag1),(CheckBox) addPersonButton.findViewById(R.id.tag2),
                (CheckBox) addPersonButton.findViewById(R.id.tag3), (CheckBox) addPersonButton.findViewById(R.id.tag4)};
        FrameLayout[] layouts = {(FrameLayout) addPersonButton.findViewById(R.id.tag1Layout), (FrameLayout) addPersonButton.findViewById(R.id.tag2Layout),
                (FrameLayout) addPersonButton.findViewById(R.id.tag3Layout), (FrameLayout) addPersonButton.findViewById(R.id.tag4Layout)};

        setCheckBoxes(row, checkBox, layouts, null, null);

        TextView prompt = (TextView) addPersonButton.findViewById(R.id.name);
        prompt.setText(R.string.sign_up_prompt);
        prompt.setTextColor(ContextCompat.getColor(getContext(), R.color.green));

        table.addView(addPersonButton);

        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment newFrag = new SignUpFragment();

                Bundle args = new Bundle();
                args.putInt(FormListFragment.FORM_ID, formId);
                args.putStringArray(SIGNUP_TAGS_INTERESTS, signUpTagInterests);
                args.putStringArray(SIGNUP_TAGS_SKILLS, signUpTagSkills);
                args.putBoolean(INTEREST_FORM, interestForm);

                newFrag.setArguments(args);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_profile_input_container, newFrag, "SignUp")
                        .addToBackStack("SignUp").commit();
            }
        });
    }

    private void setCheckBoxes(TableRow container, CheckBox[] boxes, FrameLayout[] boxHolders, String tags, TextView[] header){
        float weightSum = 5;

        for (int n = 0; n < (signUpTagInterests.length <= 4 ? signUpTagInterests.length : 4); n++) {
            if (tags != null) {
                boxes[n].setChecked(tags.contains(signUpTagInterests[n]));
            }
            if (header != null) {
                header[n].setText(signUpTagInterests[n]);
            }
            weightSum += .5;
            boxHolders[n].setVisibility(View.VISIBLE);
        }

        container.setWeightSum(weightSum);
    }

    //Complete button for when sign up is over.

    private void setUpCompleteButton(){

        FrameLayout frame = new FrameLayout(this.getActivity());
        TableLayout.LayoutParams FrameLP = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams ButtonLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        Button completeButton = new Button(this.getActivity());
        ButtonLP.gravity = Gravity.BOTTOM;
        completeButton.setPadding(15,15,15,15);
        completeButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
        completeButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        completeButton.setText(R.string.complete_btn);
        completeButton.setTextSize(30);

        frame.setLayoutParams(FrameLP);
        completeButton.setLayoutParams(ButtonLP);

        frame.addView(completeButton);
        table.addView(frame);

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().stopLockTask();
                for (Connection f:formConnections) {
                    uploadContacts(f);
                }
                getActivity().finish();
            }
        });
    }

    //uploads contacts to server
    private void uploadContacts(Connection connection){
        //todo check form and server for duplicates. server duplicates give option: add as new, update info, delete from f

        {
            try {
                setCurrentDate();

                AsyncTask<String, Void, String> uploadJson1;
                AsyncTask<String, Void, String> uploadJson2;
                AsyncTask<String, Void, String> uploadJson3;
                AsyncTask<String, Void, String> uploadJson4 = null;
                AsyncTask<String, Void, String> uploadJson5;
                AsyncTask<String, Void, String> uploadJson6;

                //Urls for Posting/Patching
                String partnerUrl;
                String emailUrl;
                String typeUrl;
                String tagUrl;
                String phoneUrl;

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
                    tagUrl = createPostUrl("crm/Partners/" + nextPartnerKey + "/Tags");
                    phoneUrl = createPostUrl("partner/Partners/" + nextPartnerKey + "/ContactInfo");

                    //set up POST json objects for patching
                    uploadJson1 = new PostJson(getContext(), partnerUrl, createPartnerJson(nextPartnerKey, connection.getName().split(", ")), mAccount, false);
                    uploadJson2 = new PostJson(getContext(), emailUrl, createEmailJson(connection.getEmail()), mAccount, false);
                    uploadJson3 = new PostJson(getContext(), tagUrl, createTagJson(nextPartnerKey), mAccount, false);//todo add form tags
                    if (interestForm && phoneUrl.split(",").length==4) {
                        //todo phone does not upload properly
                        uploadJson4 = new PostJson(getContext(), phoneUrl, createPhoneJson(nextPartnerKey, connection.getPhone()), mAccount, false);
                    }
                    uploadJson5 = new PostJson(getContext(), typeUrl, createTypeJson(nextPartnerKey), mAccount, true);

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

                String tagUrl = createPostUrl("crm/Partners/" + /*newuserId "/Tags");
            }*/

                    uploadJson1.execute();

                    System.out.println("POST Email info");
                    uploadJson2.execute();
                    //todo uploadJson3.execute();
                    if (uploadJson4 != null) {
                        uploadJson4.execute();
                    }
                    uploadJson5.execute();

                    //todo set Uploaded to 1 if successful
                    //todo if unsuccessful create way to try again
                } //remove this if un-comment else
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //loads a list of people on sign up list
    private void displayPeople() {

        if (formConnections != null) {
            Log.d(TAG, "displayPeople: " + formConnections.size());
            for (Connection person : formConnections) {
                View childLayout = LayoutInflater.from(this.getContext()).inflate(R.layout.sign_up_element_table_row, table, false);

                TextView name = (TextView) childLayout.findViewById(R.id.name);
                TextView email = (TextView) childLayout.findViewById(R.id.email);
                TextView gradYear = (TextView) childLayout.findViewById(R.id.grad_year);
                TableRow row = (TableRow) childLayout.findViewById(R.id.add_sign_up);
                CheckBox[] checkBox = {(CheckBox) childLayout.findViewById(R.id.tag1),(CheckBox) childLayout.findViewById(R.id.tag2),
                        (CheckBox) childLayout.findViewById(R.id.tag3), (CheckBox) childLayout.findViewById(R.id.tag4)};
                FrameLayout[] layouts = {(FrameLayout) childLayout.findViewById(R.id.tag1Layout), (FrameLayout) childLayout.findViewById(R.id.tag2Layout),
                        (FrameLayout) childLayout.findViewById(R.id.tag3Layout), (FrameLayout) childLayout.findViewById(R.id.tag4Layout)};

                setCheckBoxes(row, checkBox, layouts, person.getTags(), null);

                String[] nameArray = person.getName().split(", ");
                String nameText="";
                if(nameArray.length==2) {
                    nameText = nameArray[1] + " " + nameArray[0];
                }else{
                    nameText=person.getName().replace(","," ");
                }

                name.setText(nameText);
                email.setText(R.string.check_mark);
                gradYear.setText(person.getGradYear());

                table.addView(childLayout);
            }
        }
    }

    //gets tags for form
    private void getTags(){
        //get info for form
        Cursor cursor = getActivity().getContentResolver().query(
                LocalDBTables.FormTable.CONTENT_URI,
                new String[] {LocalDBTables.FormTable.FORM_TAGS,
                        LocalDBTables.FormTable.FORM_SIGN_UP_TAGS},
                LocalDBTables.FormTable.FORM_ID + " = " + formId,
                null,
                null
        );

        if (cursor != null){
            while (cursor.moveToNext()) {
                tags = cursor.getString(0).split(",");
                String[] allTags = cursor.getString(1).split("infoRequestSkillsTag:");
                signUpTagInterests = allTags[0].split(",");
                signUpTagSkills = allTags.length>1?allTags[1].split(","):null;
                interestForm = allTags.length>1;
            }
            cursor.close();
        }
    }

    //gets all connections for this form
    private void getFormPeople(int formId){
        formConnections =  new ArrayList<>();

        Log.d(TAG, "getFormData: " + formId);

        //get people on sign up list
        Cursor cursor = getActivity().getContentResolver().query(
                LocalDBTables.ConnectionTable.CONTENT_URI,
                new String[] {LocalDBTables.ConnectionTable.CONNECTION_NAME,
                        LocalDBTables.ConnectionTable.CONNECTION_EMAIL,
                        LocalDBTables.ConnectionTable.CONNECTION_GRAD_YEAR,
                        LocalDBTables.ConnectionTable.CONNECTION_TAGS,
                        LocalDBTables.ConnectionTable.CONNECTION_PHONE},
                LocalDBTables.ConnectionTable.FORM_ID + " = " + formId,
                null,
                null
        );

        if (cursor != null){
            while (cursor.moveToNext()) {
                Connection temp = new Connection();
                temp.setName(cursor.getString(0));
                temp.setEmail(cursor.getString(1));
                temp.setGradYear(cursor.getString(2));
                temp.setTags(cursor.getString(3));
                temp.setPhone(cursor.getString(4));
                formConnections.add(temp);
            }
            cursor.close();
        }
    }

    //gets form info
    private void getForm(int formId){

        //get form Info
        Cursor cursor = getActivity().getContentResolver().query(
                LocalDBTables.FormTable.CONTENT_URI,
                new String[] {LocalDBTables.FormTable.FORM_DESCRIPTION,
                        LocalDBTables.FormTable.FORM_TAGS,
                        LocalDBTables.FormTable.FORM_SIGN_UP_TAGS},
                LocalDBTables.FormTable.FORM_ID + " = " + formId,
                null,
                null
        );

        if (cursor != null){
            while (cursor.moveToNext()) {
                Form temp = new Form();
                temp.setFormDescription(cursor.getString(0));
                temp.setFormTags(cursor.getString(1));
                temp.setSignUpTags(cursor.getString(2));
                form = temp;
            }
            Log.d(TAG, "getForm: " + form.getFormTags() + form.getSignUpTags());
            cursor.close();
        }
    }

    private String createPostUrl(String url){
        return mAccountManager.getUserData(mAccount, "server") + "/apps/kardia/api/"
                + url
                + "?cx__mode=rest&cx__res_format=attrs&cx__res_attrs=basic&cx__res_type=collection";
    }

    private String createPatchUrl(String JsonId){
        return mAccountManager.getUserData(mAccount, "server") + JsonId + "&cx__res_type=element";
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

    private JSONObject createTypeJson(String id)
    {

        JSONObject typeJson = new JSONObject();
        //todo if add edit option: " " should change to mPartnerId;
        String partner = mNewProfile ? nextPartnerKey : " ";

        try
        {
            if (mNewProfile)
            {
                typeJson.put("e_collaborator", mAccountManager.getUserData(mAccount, "partnerId"));
                typeJson.put("p_partner_key", id);
                typeJson.put("e_collab_type_id", 1); //always set to mobilizer
                typeJson.put("e_is_automatic", 0);
                typeJson.put("e_collaborator_status", "A");
                //todo set as account username
                typeJson.put("s_created_by", mAccount.name);
                typeJson.put("s_modified_by", mAccount.name);
                typeJson.put("s_date_created", jsonDate);
                typeJson.put("s_date_modified", jsonDate);
                typeJson.put( "collaborator_id", mAccountManager.getUserData(mAccount, "partnerId"));
                typeJson.put( "collaborator_type_id", 1); //always set to mobilizer
                typeJson.put( "collaborator_status", "A");
                typeJson.put( "partner_id", partner);
                typeJson.put( "role_id", 1); //always set to mobilizer
                typeJson.put( "role_name", "Mobilizer");



            } else
            {
                typeJson.put("collab_type_id", 1);  //always set to mobilizer
            }
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        return typeJson;
    }

    private JSONObject createTagJson(String id)
    {
        //todo make server upload work
        int tag_id = 2;

        JSONObject tagJson = new JSONObject();
        //todo if add edit option: " " should change to mPartnerId;
        String partner = mNewProfile ? nextPartnerKey : " ";

        try
        {
            if (mNewProfile)
            {
                tagJson.put("e_tag_id", 0);
                tagJson.put("p_partner_key", id);
                tagJson.put("e_tag_strength", 1.0);
                tagJson.put("e_tag_certainty", 1.0);
                tagJson.put("e_tag_volatility", "P");


                tagJson.put("s_created_by", mAccount.name);
                tagJson.put("s_modified_by", mAccount.name);
                tagJson.put("s_date_created", jsonDate);
                tagJson.put("s_date_modified", jsonDate);
            }else{
                tagJson.put("s_date_modified", jsonDate);
            }
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        return tagJson;
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

    private JSONObject createPartnerJson(String id, String [] name)
    {
        JSONObject partnerJson = new JSONObject();
        //todo if add edit option: " " should change to mPartnerId;
        String partner = mNewProfile ? nextPartnerKey : " ";

        try
        {
            if (mNewProfile)
            {
                partnerJson.put("p_partner_key", id);
                partnerJson.put("s_created_by", mAccount.name);
                partnerJson.put("s_modified_by", mAccount.name);
                partnerJson.put("p_creating_office", mAccountManager.getUserData(mAccount, "partnerId"));
                partnerJson.put("p_status_code", "A");
                partnerJson.put("p_partner_class", "123");
                partnerJson.put("p_record_status_code", "A");
                partnerJson.put("p_surname", name[0]);
                partnerJson.put("p_given_name", name[1]);
                partnerJson.put("s_date_created", jsonDate);
                partnerJson.put("s_date_modified", jsonDate);

            } else
            {
                partnerJson.put("surname", "last");//lastName.getText().toString());
                partnerJson.put("given_names", "first");//firstName.getText().toString());
            }
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        return partnerJson;
    }

    private JSONObject createEmailJson(String email)
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
                emailJson.put("p_contact_data", email);
                emailJson.put("p_contact_type", "E");
                emailJson.put("p_record_status_code", "A");
            }
            else
            {
                emailJson.put("contact_data", "email");//email.getText().toString());
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }

        return emailJson;
    }

    private JSONObject createPhoneJson(String id, String phone)
    {
        JSONObject phoneJson = new JSONObject();
        //String partner = mNewProfile ? nextPartnerKey : mPartnerId;

        //phone = 0type,1country,2area,3number
        String[] mPhone= phone.split(",");

        try {

            String countryCodeTemp = mPhone[1];
            String actualCountryCode = countryCodeTemp.equals("") ? "1" : countryCodeTemp.replace("+", "");

            if(mNewProfile)// || mPhoneJsonId == null)
            {
                phoneJson.put("p_partner_key", id);
                phoneJson.put("s_created_by", mAccount.name);
                phoneJson.put("s_modified_by", mAccount.name);
                phoneJson.put("s_date_created", jsonDate);
                phoneJson.put("s_date_modified", jsonDate);
                phoneJson.put("p_contact_data", mPhone[3]);
                phoneJson.put("p_phone_country", actualCountryCode);
                phoneJson.put("p_phone_area_city", mPhone[2]);
                phoneJson.put("p_contact_type", mPhone[0]); //C or P
                phoneJson.put("p_record_status_code", "A");
            }
            else
            {
                phoneJson.put("contact_data", mPhone[3]);
                phoneJson.put("phone_country", actualCountryCode);
                phoneJson.put("phone_area_city", mPhone[2]);
            }

        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }

        return phoneJson;
    }

    /**
     * Used to hold onto the id, in case the user comes back to this page
     * (like if their phone goes into sleep mode or they temporarily leave the app)
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(FORM_ID, formId);
    }
}
