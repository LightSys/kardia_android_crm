package org.lightsys.crmapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.Partner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cubemaster on 3/7/16.
 * Edited by ca2br and Judah on 7/18/16
 *
 * Lists all collaborators of the user.
 */
public class MainFragment extends android.support.v4.app.Fragment {
    private static final String LOG_TAG = MainFragment.class.getName();

    private RecyclerView mRecyclerView;
    private List<Partner> mProfiles = new ArrayList<>();
    private Account mAccount;

    Partner mPartner2 = new Partner();

    public MainFragment() {}

    /**
     * Gets account information.
     * Gets list of collaboratee names and ID numbers.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountManager accountManager = AccountManager.get(getActivity());
        Account[] accounts = accountManager.getAccountsByType(CRMContract.accountType);
        if(accounts.length > 0) {
            mAccount = accounts[0];
            new GetCollaborateesTask().execute();
        }
    }

    /**
     * Selects collaboratee.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates view to list collaboratees.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (android.support.v7.widget.RecyclerView) rootView.findViewById(R.id.recyclerview_profiles);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter(mProfiles);

        return rootView;
    }

    /**
     * Sets up adapter after Async task is complete.
     */
    private void setupAdapter(List<Partner> profiles) {
        if(isAdded()) {
            mRecyclerView.setAdapter(new ProfileAdapter(profiles));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * View that holds collaboratee information
     */
    private class ProfileHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout mLinearLayout;
        private Partner mPartner;

        public ProfileHolder(View view) {
            super(view);

            mLinearLayout = (LinearLayout) view;

            view.setOnClickListener(this);
        }

        /**
         * Binds profile information to the view.
         */
        public void bindProfile(Partner partner) {
            Picasso.with(getActivity())
                    .load(partner.getProfilePictureFilename())
                    .placeholder(R.drawable.john_smith)
                    .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            ((TextView) mLinearLayout.findViewById(R.id.profile_name)).setText(partner.getPartnerName());

            mPartner = partner;
        }

        /**
         * Goes to get further information regarding a collaboratee after a collaboratee is selected.
         */
        @Override
        public void onClick(View v) {
            mPartner2 = mPartner;
            new getCollaborateeInfoTask().execute();
        }
    }

    /**
     * Gets detailed collaboratee information.
     *
     */
    private class getCollaborateeInfoTask extends AsyncTask<Void, Void, Partner> {

        /**
         * Background thread that fetches info from the server.
         * This where the magic happens.
         */
        @Override
        protected Partner doInBackground(Void... params) {
            Partner collaboratee = new Partner(mPartner2.getPartnerId(), mPartner2.getPartnerName());

            Cursor cursor = getActivity().getContentResolver().query(
                    CRMContract.CollaborateeTable.CONTENT_URI,
                    new String[] {CRMContract.CollaborateeTable.PARTNER_ID, CRMContract.CollaborateeTable.PARTNER_NAME, CRMContract.CollaborateeTable.EMAIL,
                            CRMContract.CollaborateeTable.PHONE, CRMContract.CollaborateeTable.ADDRESS_1, CRMContract.CollaborateeTable.CITY,
                            CRMContract.CollaborateeTable.STATE_PROVINCE, CRMContract.CollaborateeTable.POSTAL_CODE, CRMContract.CollaborateeTable.CELL,
                            CRMContract.CollaborateeTable.SURNAME, CRMContract.CollaborateeTable.GIVEN_NAMES},
                    CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                    new String[] {AccountManager.get(getActivity()).getUserData(mAccount, "partnerId")},
                    null
            );


            while(cursor.moveToNext()) {
                if (cursor.getString(0).equals(mPartner2.getPartnerId())) {
                    collaboratee.setPartnerName(cursor.getString(1));
                    collaboratee.setEmail(cursor.getString(2));
                    collaboratee.setPhone(cursor.getString(3));
                    collaboratee.setAddress1(cursor.getString(4));
                    collaboratee.setCity(cursor.getString(5));
                    collaboratee.setStateProvince(cursor.getString(6));
                    collaboratee.setPostalCode(cursor.getString(7));
                    collaboratee.setFullAddress(cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
                    collaboratee.setCell(cursor.getString(8));
                    collaboratee.setSurname(cursor.getString(9));
                    collaboratee.setGivenNames(cursor.getString(10));
                }
            }
            cursor.close();

            if(collaboratee.getEmail() == null || collaboratee.getPhone() == null || collaboratee.getAddress1() == null
                    || collaboratee.getCity() == null || collaboratee.getStateProvince() == null || collaboratee.getPostalCode() == null ||
                    collaboratee.getCell() == null) {

                KardiaFetcher fetcher = new KardiaFetcher(getContext());
                collaboratee = fetcher.getCollaborateeInfo(mAccount, collaboratee);

                mPartner2 = collaboratee;

                ContentValues values = new ContentValues();
                values.put(CRMContract.CollaborateeTable.COLLABORATER_ID, collaboratee.getPartnerId());
                values.put(CRMContract.CollaborateeTable.PARTNER_NAME, collaboratee.getPartnerName());
                values.put(CRMContract.CollaborateeTable.SURNAME, collaboratee.getSurname());
                values.put(CRMContract.CollaborateeTable.GIVEN_NAMES, collaboratee.getGivenNames());
                values.put(CRMContract.CollaborateeTable.PHONE, collaboratee.getPhone());
                values.put(CRMContract.CollaborateeTable.CELL, collaboratee.getCell());
                values.put(CRMContract.CollaborateeTable.EMAIL, collaboratee.getEmail());
                values.put(CRMContract.CollaborateeTable.ADDRESS_1, collaboratee.getAddress1());
                values.put(CRMContract.CollaborateeTable.CITY, collaboratee.getCity());
                values.put(CRMContract.CollaborateeTable.STATE_PROVINCE, collaboratee.getStateProvince());
                values.put(CRMContract.CollaborateeTable.POSTAL_CODE, collaboratee.getPostalCode());
                values.put(CRMContract.CollaborateeTable.SURNAME, collaboratee.getSurname());
                values.put(CRMContract.CollaborateeTable.GIVEN_NAMES, collaboratee.getGivenNames());
                getContext().getContentResolver().insert(CRMContract.CollaborateeTable.CONTENT_URI, values);


                Cursor cursor2 = getActivity().getContentResolver().query(
                        CRMContract.CollaborateeTable.CONTENT_URI,
                        new String[]{CRMContract.CollaborateeTable.PARTNER_ID, CRMContract.CollaborateeTable.PARTNER_NAME, CRMContract.CollaborateeTable.EMAIL,
                                CRMContract.CollaborateeTable.PHONE, CRMContract.CollaborateeTable.ADDRESS_1, CRMContract.CollaborateeTable.CITY,
                                CRMContract.CollaborateeTable.STATE_PROVINCE, CRMContract.CollaborateeTable.POSTAL_CODE, CRMContract.CollaborateeTable.CELL,
                                CRMContract.CollaborateeTable.SURNAME, CRMContract.CollaborateeTable.GIVEN_NAMES},
                        CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                        new String[]{AccountManager.get(getActivity()).getUserData(mAccount, "partnerId")},
                        null
                );

                cursor2.close();
            }
            else {
                mPartner2 = collaboratee;
            }


            return collaboratee;
        }


        /**
         *
         *
         */
        @Override
        protected void onPostExecute(Partner collaboratee) {

            Intent i = new Intent(getActivity(), ProfileActivity.class);
            i.putExtra(ProfileActivity.NAME_KEY, mPartner2.getPartnerName());
            i.putExtra(ProfileActivity.PARTNER_ID_KEY, mPartner2.getPartnerId());
            i.putExtra(ProfileActivity.EMAIL_KEY, mPartner2.getEmail());
            i.putExtra(ProfileActivity.PHONE_KEY, mPartner2.getPhone());
            i.putExtra(ProfileActivity.ADDRESS_KEY, mPartner2.getAddress1());
            i.putExtra(ProfileActivity.CITY_KEY, mPartner2.getCity());
            i.putExtra(ProfileActivity.STATE_KEY,mPartner2.getStateProvince());
            i.putExtra(ProfileActivity.POSTALCODE_KEY, mPartner2.getPostalCode());
            i.putExtra(ProfileActivity.FULLADDRESS_KEY, mPartner2.getFullAddress());
            i.putExtra(ProfileActivity.CELL_KEY, mPartner2.getCell());
            i.putExtra(ProfileActivity.SURNAME_KEY, mPartner2.getSurname());
            i.putExtra(ProfileActivity.GIVEN_NAMES_KEY, mPartner2.getGivenNames());

            i.putExtra(ProfileActivity.BLOG_KEY, mPartner2.getBlog());
            i.putExtra(ProfileActivity.FAX_KEY, mPartner2.getFax());
            i.putExtra(ProfileActivity.FACEBOOK_KEY, mPartner2.getFacebook());
            i.putExtra(ProfileActivity.SKYPE_KEY, mPartner2.getSkype());
            i.putExtra(ProfileActivity.TWITTER_KEY, mPartner2.getTwitter());
            i.putExtra(ProfileActivity.WEBSITE_KEY, mPartner2.getWebsite());


            startActivity(i);
        }

    }




    private class ProfileAdapter extends RecyclerView.Adapter<ProfileHolder> {
        private List<Partner> mCollaboratees;

        public ProfileAdapter(List<Partner> collaboratees) {
            mCollaboratees = collaboratees;
        }

        @Override
        public ProfileHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.profile_listitem, container, false);

            return new ProfileHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ProfileHolder profileHolder, int position) {
            Partner collaboratee = mCollaboratees.get(position);
            profileHolder.bindProfile(collaboratee);
        }

        @Override
        public int getItemCount() {
            return mCollaboratees.size();
        }
    }



    private class GetCollaborateesTask extends AsyncTask<Void, Void, List<Partner>> {
        @Override
        protected List<Partner> doInBackground(Void... params) {
            Cursor cursor = getActivity().getContentResolver().query(
                    CRMContract.CollaborateeTable.CONTENT_URI,
                    new String[] {CRMContract.CollaborateeTable.PARTNER_ID, CRMContract.CollaborateeTable.PARTNER_NAME},
                    CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                    new String[] {AccountManager.get(getActivity()).getUserData(mAccount, "partnerId")},
                    null
            );

            List<Partner> collaboratees = new ArrayList<>();
            while(cursor.moveToNext()) {
                Partner collaboratee = new Partner(cursor.getString(0));
                collaboratee.setPartnerName(cursor.getString(1));
                collaboratees.add(collaboratee);
            }
            cursor.close();

            return collaboratees;
        }


        @Override
        protected void onPostExecute(List<Partner> collaboratees) {
            mProfiles = collaboratees;
            setupAdapter(mProfiles);
        }
    }

    public void search(String searchText){

        ArrayList<Partner> profiles = new ArrayList<>();
        for(Partner profile : mProfiles) {
            if(profile.getPartnerName().toLowerCase().contains(searchText.toLowerCase())) {
                profiles.add(profile);
            }

        }

        setupAdapter(profiles);

    }

}