package org.lightsys.crmapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


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
    private boolean mNewProfile;

    Spinner phoneType;


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

        TextView firstName = (TextView)rootView.findViewById(R.id.profile_input_name_first);
        TextView lastName = (TextView)rootView.findViewById(R.id.profile_input_name_last);
        TextView cell = (TextView)rootView.findViewById(R.id.profile_input_phone_text);
        final TextView phone = (TextView)rootView.findViewById(R.id.profile_input_phone_text);
        TextView email = (TextView)rootView.findViewById(R.id.profile_input_email);
        TextView address = (TextView)rootView.findViewById(R.id.profile_input_street_address);
        TextView city = (TextView)rootView.findViewById(R.id.profile_input_city);
        TextView state = (TextView)rootView.findViewById(R.id.profile_input_state);
        TextView postalCode = (TextView)rootView.findViewById(R.id.profile_input_zip);
        phoneType = (Spinner)rootView.findViewById(R.id.profile_input_phone_spinner);

        phoneType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String type = (String)phoneType.getSelectedItem();
                if (type.equals("Home")){
                    phone.setText(mPhone);
                }
                else if (type.equals("Mobile")){
                    phone.setText(mCell);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        firstName.setText(mGivenName);
        lastName.setText(mSurname);
        //phone.setText(mPhone);
        cell.setText(mCell);
        email.setText(mEmail);
        address.setText(mAddress);
        city.setText(mCity);
        state.setText(mState);
        postalCode.setText(mPostalCode);

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


    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            Toast.makeText(parent.getContext(), "The planet is " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }

}
