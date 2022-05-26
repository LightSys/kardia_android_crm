package org.lightsys.crmapp.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.activities.FormActivity;
import org.lightsys.crmapp.data.LocalDBTables;

import java.util.ArrayList;


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
            R.id.input_grad_year};

    private String mPartnerId;

    private TextView firstName;
    private TextView lastName;
    private TextView email;
    private TextView gradYear;
    private TextView phone, countryCode, areaCode;
    private Spinner phoneType;
    private LinearLayout phoneLayout;
    private LinearLayout checkBoxes;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mPhone;
    private String mGradYear;
    private int formId;
    private String[] tagOptionsInterests, tagOptionsSkills;
    private String tags="";
    private ArrayList<CheckBox> tagChks;
    private boolean interestForm;


    View rootView;
    LayoutInflater inflater;

    private String[] mEditTextData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        formId = getArguments().getInt(FormFragment.FORM_ID);
        tagOptionsInterests = getArguments().getStringArray(FormFragment.SIGNUP_TAGS_INTERESTS);
        tagOptionsSkills = getArguments().getStringArray(FormFragment.SIGNUP_TAGS_SKILLS);
        interestForm = getArguments().getBoolean(FormFragment.INTEREST_FORM);

        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_signup_sheet_input, container, false);
        getActivity().setTitle("Sign up");

        tagChks = new ArrayList<>();

        // Sets up views.
        firstName = (TextView)rootView.findViewById(R.id.profile_input_name_first);
        lastName = (TextView)rootView.findViewById(R.id.profile_input_name_last);
        email = (TextView)rootView.findViewById(R.id.profile_input_email);
        gradYear = (TextView)rootView.findViewById(R.id.input_grad_year);
        checkBoxes = (LinearLayout)rootView.findViewById(R.id.check_boxes);

        phoneLayout = (LinearLayout) rootView.findViewById(R.id.phoneLayout);
        phone = (TextView)rootView.findViewById(R.id.form_input_phone_text);
        countryCode = (TextView) rootView.findViewById(R.id.form_input_country_code_text);
        areaCode = (TextView)rootView.findViewById(R.id.form_input_area_code_text);
        phoneType = (Spinner)rootView.findViewById(R.id.form_input_phone_spinner);

        //add interest check boxes
        if (tagOptionsInterests != null && tagOptionsInterests.length !=0) {
            addCheckbox((LinearLayout) inflater.inflate(R.layout.sign_up_interest_check_boxes,
                    (ViewGroup) rootView.findViewById(R.id.parentCheckBoxes), false), tagOptionsInterests);
        }
        if (tagOptionsSkills != null && tagOptionsSkills.length !=0){
            //add skill check boxes
            addCheckbox((LinearLayout) inflater.inflate(R.layout.sign_up_skill_check_boxes,
                    (ViewGroup) rootView.findViewById(R.id.parentCheckBoxes), false), tagOptionsSkills);
        }
        if (!interestForm){
            phoneLayout.setVisibility(View.GONE);
        }

        //todo remove
        email.setText("test@test.com");
        firstName.setText("Elle");
        lastName.setText("Rogers");
        gradYear.setText("2020");
        //remove to here


        Button submit = (Button) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mEmail = email.getText().toString();
                mFirstName = firstName.getText().toString();
                mLastName = lastName.getText().toString();
                mGradYear = gradYear.getText().toString();
                mPhone = gatherPhoneData();


                if (checkValidInput()) {
                    addUserToDB();
                    openForm(formId);

                }
            }
        });

        //((MainActivity) getActivity()).showNavButton(false);
        //((MainActivity) getActivity()).changeOptionsMenu(false, true);

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

        ((FormActivity) getActivity()).setCloseButton(true);

        return rootView;
    }

    @Override
    public void onDestroyView(){
        ((FormActivity) getActivity()).setCloseButton(false);
        super.onDestroyView();
    }

    private String gatherPhoneData(){
        String phoneNumber=(phoneType.getSelectedItem().toString().equals("Mobile")?"C":"P") + ","+countryCode.getText().toString()
                + ","+ areaCode.getText().toString() + ","+phone.getText().toString();

        return phoneNumber;
    }

    private void addCheckbox(LinearLayout childLayout, String[] tags){
        LinearLayout tagsLeft = (LinearLayout) childLayout.findViewById(R.id.leftColumn);
        LinearLayout tagsRight = (LinearLayout) childLayout.findViewById(R.id.rightColumn);

        for (int t = 0; t< tags.length; t++){
            CheckBox cb = (CheckBox) inflater.inflate(R.layout.checkbox,
                    (ViewGroup) rootView.findViewById(R.id.parentCheckBoxes), false);
            cb.setText(tags[t]);

            if (t%2==0) {
                tagsLeft.addView(cb);
            }else{
                tagsRight.addView(cb);
            }
            tagChks.add(cb);
        }
        checkBoxes.addView(childLayout);
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
        for (CheckBox cb:tagChks) {
            tags += cb.isChecked()?cb.getText():null;
        }

        ContentValues values = new ContentValues();
        //todo let id be assigned by server
        values.put(LocalDBTables.ConnectionTable.CONNECTION_ID, getConnectionId());
        values.put(LocalDBTables.ConnectionTable.FORM_ID, formId);
        values.put(LocalDBTables.ConnectionTable.CONNECTION_NAME, lastName.getText() + ", " + firstName.getText());
        values.put(LocalDBTables.ConnectionTable.CONNECTION_EMAIL, email.getText().toString());
        values.put(LocalDBTables.ConnectionTable.CONNECTION_GRAD_YEAR, gradYear.getText().toString());
        values.put(LocalDBTables.ConnectionTable.CONNECTION_TAGS, tags);
        values.put(LocalDBTables.ConnectionTable.CONNECTION_PHONE, mPhone);
        values.put(LocalDBTables.ConnectionTable.CONNECTION_UPLOADED, 0);
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
        if (cursor != null){
            while (cursor.moveToNext()) {
                connId = cursor.getInt(0);
            }
            cursor.close();
        }
        connId+=1;

        return connId;
    }

    private void openForm(int formId){
        FormFragment newFrag = new FormFragment();

        Bundle args = new Bundle();
        args.putInt(FormListFragment.FORM_ID, formId);

        newFrag.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_profile_input_container, newFrag, "Form").addToBackStack("Form")
                .commit();
    }

    /**
     * Saves info.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putStringArray(DATA_ARRAY_KEY, mEditTextData);
    }
}