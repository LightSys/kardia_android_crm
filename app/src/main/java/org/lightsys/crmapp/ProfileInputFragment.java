package org.lightsys.crmapp;

import android.accounts.AccountManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.squareup.picasso.Picasso;

import org.lightsys.crmapp.data.CRMContract;

import java.util.Arrays;

/**
 * Created by cubemaster on 3/11/16.
 */
public class ProfileInputFragment extends Fragment {
    private static final String LOG_TAG = ProfileInputFragment.class.getName();
    private static final String DATA_ARRAY_KEY = "DATA_ARRAY_KEY";
    private static final int[] VIEW_IDS = new int[]{
            R.id.profile_input_name_first,
            R.id.profile_input_name_last,
            R.id.profile_input_phone_text,
            R.id.profile_input_email,
            R.id.profile_input_street_address,
            R.id.profile_input_city,
            R.id.profile_input_state,
            R.id.profile_input_zip};

    private String mName;
    private String mPartnerId;
    private boolean mNewProfile;

    //private String mFirstName, mLastName, mPhone, mEmail, mStreetAddress, mCity, mState, mZIP;
    private String[] mEditTextData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mName = arguments.getString(ProfileActivity.NAME_KEY);
            mPartnerId = arguments.getString(ProfileActivity.PARTNER_ID_KEY);
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

        View rootView = inflater.inflate(R.layout.fragment_profile_input, container, false);

        Picasso.with(getActivity())
                .load(R.drawable.persona)
                .into(((ImageView) rootView.findViewById(R.id.profile_input_photo)));

        Spinner spinner = (Spinner) rootView.findViewById(R.id.profile_input_phone_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.name_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        Cursor cursor = getActivity().getContentResolver().query(
                CRMContract.CollaborateeTable.CONTENT_URI,
                new String[] {CRMContract.CollaborateeTable.GIVEN_NAMES,
                        CRMContract.CollaborateeTable.SURNAME,
                        CRMContract.CollaborateeTable.PHONE,
                        CRMContract.CollaborateeTable.EMAIL,
                        CRMContract.CollaborateeTable.ADDRESS_1,
                        CRMContract.CollaborateeTable.CITY,
                        CRMContract.CollaborateeTable.STATE_PROVINCE,
                        CRMContract.CollaborateeTable.POSTAL_CODE},
                CRMContract.CollaborateeTable.PARTNER_ID + " = ?",
                new String[] {mPartnerId},
                null
        );

        if(cursor.moveToFirst()) {
            //populate fields here
            Log.d("profileName", mPartnerId);
        }

        spinner.setAdapter(adapter);

        for(int i = 0; i < mEditTextData.length; i++) {
            final EditText editText = (EditText) rootView.findViewById(VIEW_IDS[i]);
            editText.setText(cursor.getString(i));
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    mEditTextData[getIndexById(editText.getId())] = s.toString();
                }
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
        }

        cursor.close();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putStringArray(DATA_ARRAY_KEY, mEditTextData);
    }

    private int getIndexById(int id) {
        for(int i = 0; i < VIEW_IDS.length; i++) {
            if(id == VIEW_IDS[i]) {
                return i;
            }
        }
        return -1;
    }
}
