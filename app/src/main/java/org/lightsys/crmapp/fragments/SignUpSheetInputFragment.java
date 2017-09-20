package org.lightsys.crmapp.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.activities.EditProfileActivity;
import org.lightsys.crmapp.activities.SignUpListActivity;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.models.TimelineItem;


/**
 * Created by otter57 on 9/14/17.
 *
 * Allows a user to edit/create a signUpList.
 *
 */
public class SignUpSheetInputFragment extends Fragment{
    private static final String LOG_TAG = SignUpSheetInputFragment.class.getName();

    TextView firstName;
    TextView lastName;
    TextView email;
    TextView gradYear;
    CheckBox longTerm, internship, oneYear, springBreak;
    int formId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        formId = getArguments().getInt(SignUpListFragment.FORM_ID);
        View rootView = inflater.inflate(R.layout.fragment_signup_sheet_input, container, false);

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
                boolean validEmail = false;
                /*if (email.getText() != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
                    validEmail = true;
                }*/
                String TAG = "SignUpSheetInputFrag";
                //submitOnClick();
                addUserToDB();
                Log.d(TAG, "onClick: " + validEmail);
                Intent intent = new Intent(getActivity(), SignUpListActivity.class);
                intent.putExtra(SignUpListFragment.FORM_ID, formId);
                startActivity(intent);
            }
        });

        return rootView;
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
}