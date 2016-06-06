package org.lightsys.crmapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.Partner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cubemaster on 3/10/16.
 */
public class ProfileFragment extends Fragment {
    private static final String LOG_TAG = ProfileFragment.class.getName();

    private String mName;
    private String mPartnerId;
    private Account mAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AccountManager accountManager = AccountManager.get(getActivity());
        Account[] accounts = accountManager.getAccountsByType(CRMContract.ACCOUNT_TYPE);
        if(accounts.length > 0) {
            mAccount = accounts[0];
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(arguments != null) {
            mName = arguments.getString(ProfileActivity.NAME_KEY);
            mPartnerId = arguments.getString(ProfileActivity.PARTNER_ID_KEY);
        }

        // TODO API call with mPartnerId

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);



        //query - partnerId, so have email, phone number, address


        Partner current = new Partner(mPartnerId, mName);

        //how use cursor to get phone, email, address, city, state, zip for a particular collaborator
        //***************
        //Need to figure out what to do the query on...
  /*      Cursor cursor = query(
                CRMContract.CollaborateeTable.CONTENT_URI,
                new String[] {CRMContract.CollaborateeTable.PARTNER_ID,
                              CRMContract.CollaborateeTable.PHONE, CRMContract.CollaborateeTable.EMAIL,
                              CRMContract.CollaborateeTable.ADDRESS_1, CRMContract.CollaborateeTable.CITY,
                              CRMContract.CollaborateeTable.STATE_PROVINCE, CRMContract.CollaborateeTable.POSTAL_CODE},
                CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                new String[] {AccountManager.get(getActivity()).getUserData(mAccount, "partnerId"),
                              AccountManager.get(getActivity()).getUserData(mAccount, "phone"),
                              AccountManager.get(getActivity()).getUserData(mAccount, "email"),
                              AccountManager.get(getActivity()).getUserData(mAccount, "address1"),
                              AccountManager.get(getActivity()).getUserData(mAccount, "city"),
                              AccountManager.get(getActivity()).getUserData(mAccount, "stateProvince"),
                              AccountManager.get(getActivity()).getUserData(mAccount, "postalCode")},
                null
        );

        while((cursor.isLast() == false) && (cursor.getString(0) != mPartnerId)) {
            cursor.moveToNext();
        }
        if(cursor.getString(0) == mPartnerId){
            current.setPhone(cursor.getString(1));
            current.setEmail(cursor.getString(2));
            current.setFullAddress(cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
        }
        cursor.close();
*/





        if(current.getEmail() != null){
            TextView emailAddress = (TextView) rootView.findViewById(R.id.e_address);
            emailAddress.setText(current.getEmail());
            //emailAddress.setText("email");//location in CRMContract);
        }

        if(current.getPhone() != null) {
            TextView phoneNumber = (TextView) rootView.findViewById(R.id.phone_number);
            phoneNumber.setText(current.getPhone());
            //phoneNumber.setText("phone number");//location in CRMContract
        }

        if(current.getFullAddress() != null) {
            TextView streetAddress = (TextView) rootView.findViewById(R.id.s_address);
            streetAddress.setText(current.getFullAddress());
            // streetAddress.setText("address");//location in CRMContract
        }

        return rootView;
    }
}
