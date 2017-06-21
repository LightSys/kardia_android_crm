package org.lightsys.crmapp.fragments;

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

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.activities.ProfileActivity;
import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.models.Partner;

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

            //get collaboratee from the database
            Cursor cursor = getActivity().getContentResolver().query(
                    CRMContract.CollaborateeTable.CONTENT_URI,
                    new String[] {CRMContract.CollaborateeTable.PARTNER_ID, CRMContract.CollaborateeTable.PARTNER_NAME, CRMContract.CollaborateeTable.EMAIL,
                            CRMContract.CollaborateeTable.PHONE, CRMContract.CollaborateeTable.ADDRESS_1, CRMContract.CollaborateeTable.CITY,
                            CRMContract.CollaborateeTable.STATE_PROVINCE, CRMContract.CollaborateeTable.POSTAL_CODE, CRMContract.CollaborateeTable.CELL,
                            CRMContract.CollaborateeTable.SURNAME, CRMContract.CollaborateeTable.GIVEN_NAMES, CRMContract.CollaborateeTable.PHONE_ID,
                            CRMContract.CollaborateeTable.CELL_ID, CRMContract.CollaborateeTable.EMAIL_ID, CRMContract.CollaborateeTable.PHONE_JSON_ID,
                            CRMContract.CollaborateeTable.CELL_JSON_ID, CRMContract.CollaborateeTable.EMAIL_JSON_ID, CRMContract.CollaborateeTable.ADDRESS_JSON_ID,
                            CRMContract.CollaborateeTable.PARTNER_JSON_ID},
                    CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                    new String[] {AccountManager.get(getActivity()).getUserData(mAccount, "partnerId")},
                    null
            );


            //turn raw query stuffs into a partner
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
                    collaboratee.setPhoneId(cursor.getString(11));
                    collaboratee.setCellId(cursor.getString(12));
                    collaboratee.setEmailId(cursor.getString(13));
                    collaboratee.setPhoneJsonId(cursor.getString(14));
                    collaboratee.setCellJsonId(cursor.getString(15));
                    collaboratee.setEmailJsonId(cursor.getString(16));
                    collaboratee.setAddressJsonId(cursor.getString(17));
                    collaboratee.setPartnerJsonId(cursor.getString(18));
                }
            }
            cursor.close();

            //if the collaboratee is missing any information, pull it down from the server
            if(collaboratee.getEmail() == null || collaboratee.getPhone() == null || collaboratee.getAddress1() == null
                    || collaboratee.getCity() == null || collaboratee.getStateProvince() == null || collaboratee.getPostalCode() == null ||
                    collaboratee.getCell() == null) {

                //get all the collaboratee junk from the server
                KardiaFetcher fetcher = new KardiaFetcher(getContext());
                collaboratee = fetcher.getCollaborateeInfo(mAccount, collaboratee);

                mPartner2 = collaboratee;

                //get new stuff ready to go into the database, but don't add blank things
                //blank things break things
                ContentValues values = new ContentValues();
                if (collaboratee.getPartnerId() != null) {
                    values.put(CRMContract.CollaborateeTable.COLLABORATER_ID, collaboratee.getPartnerId());
                }if (collaboratee.getPartnerName() != null) {
                    values.put(CRMContract.CollaborateeTable.PARTNER_NAME, collaboratee.getPartnerName());
                }if (collaboratee.getSurname() != null) {
                    values.put(CRMContract.CollaborateeTable.SURNAME, collaboratee.getSurname());
                }if (collaboratee.getGivenNames() != null) {
                    values.put(CRMContract.CollaborateeTable.GIVEN_NAMES, collaboratee.getGivenNames());
                }if (collaboratee.getPhone() != null) {
                    values.put(CRMContract.CollaborateeTable.PHONE, collaboratee.getPhone());
                }if (collaboratee.getCell() != null) {
                    values.put(CRMContract.CollaborateeTable.CELL, collaboratee.getCell());
                }if (collaboratee.getEmail() != null) {
                    values.put(CRMContract.CollaborateeTable.EMAIL, collaboratee.getEmail());
                }if (collaboratee.getAddress1() != null) {
                    values.put(CRMContract.CollaborateeTable.ADDRESS_1, collaboratee.getAddress1());
                }if (collaboratee.getCity() != null) {
                    values.put(CRMContract.CollaborateeTable.CITY, collaboratee.getCity());
                }if (collaboratee.getStateProvince() != null) {
                    values.put(CRMContract.CollaborateeTable.STATE_PROVINCE, collaboratee.getStateProvince());
                }if (collaboratee.getPostalCode() != null) {
                    values.put(CRMContract.CollaborateeTable.POSTAL_CODE, collaboratee.getPostalCode());
                }if (collaboratee.getPhoneId() != null) {
                    values.put(CRMContract.CollaborateeTable.PHONE_ID, collaboratee.getPhoneId());
                }if (collaboratee.getEmailId() != null) {
                    values.put(CRMContract.CollaborateeTable.EMAIL_ID, collaboratee.getEmailId());
                }if (collaboratee.getCellId() != null) {
                    values.put(CRMContract.CollaborateeTable.CELL_ID, collaboratee.getCellId());
                }if (collaboratee.getPhoneJsonId() != null) {
                    values.put(CRMContract.CollaborateeTable.PHONE_JSON_ID, collaboratee.getPhoneJsonId());
                }if (collaboratee.getCellJsonId() != null) {
                    values.put(CRMContract.CollaborateeTable.CELL_JSON_ID, collaboratee.getCellJsonId());
                }if (collaboratee.getEmailJsonId() != null) {
                    values.put(CRMContract.CollaborateeTable.EMAIL_JSON_ID, collaboratee.getEmailJsonId());
                }if (collaboratee.getAddressJsonId() != null) {
                    values.put(CRMContract.CollaborateeTable.ADDRESS_JSON_ID, collaboratee.getAddressJsonId());
                }if (collaboratee.getPartnerJsonId() != null) {
                    values.put(CRMContract.CollaborateeTable.PARTNER_JSON_ID, collaboratee.getPartnerJsonId());
                }

                //put new stuff into database
                getContext().getContentResolver().update(CRMContract.CollaborateeTable.CONTENT_URI, values,
                        CRMContract.CollaborateeTable.PARTNER_ID + " = ?", new String[] {collaboratee.getPartnerId()});

                mPartner2 = collaboratee;

                //pull stuff back out of the database
                //this gets the original data back in case kardia returned nothing
                Cursor cursor2 = getActivity().getContentResolver().query(
                        CRMContract.CollaborateeTable.CONTENT_URI,
                        new String[]{CRMContract.CollaborateeTable.PARTNER_ID, CRMContract.CollaborateeTable.PARTNER_NAME, CRMContract.CollaborateeTable.EMAIL,
                                CRMContract.CollaborateeTable.PHONE, CRMContract.CollaborateeTable.ADDRESS_1, CRMContract.CollaborateeTable.CITY,
                                CRMContract.CollaborateeTable.STATE_PROVINCE, CRMContract.CollaborateeTable.POSTAL_CODE, CRMContract.CollaborateeTable.CELL,
                                CRMContract.CollaborateeTable.SURNAME, CRMContract.CollaborateeTable.GIVEN_NAMES, CRMContract.CollaborateeTable.PHONE_ID,
                                CRMContract.CollaborateeTable.CELL_ID, CRMContract.CollaborateeTable.EMAIL_ID, CRMContract.CollaborateeTable.PHONE_JSON_ID,
                                CRMContract.CollaborateeTable.CELL_JSON_ID, CRMContract.CollaborateeTable.EMAIL_JSON_ID, CRMContract.CollaborateeTable.ADDRESS_JSON_ID,
                                CRMContract.CollaborateeTable.PARTNER_JSON_ID},
                        CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                        new String[]{AccountManager.get(getActivity()).getUserData(mAccount, "partnerId")},
                        null
                );

                //smash query data into the general shape of a partner
                while(cursor2.moveToNext()) {
                    if (cursor2.getString(0).equals(mPartner2.getPartnerId())) {
                        collaboratee.setPartnerName(cursor2.getString(1));
                        collaboratee.setEmail(cursor2.getString(2));
                        collaboratee.setPhone(cursor2.getString(3));
                        collaboratee.setAddress1(cursor2.getString(4));
                        collaboratee.setCity(cursor2.getString(5));
                        collaboratee.setStateProvince(cursor2.getString(6));
                        collaboratee.setPostalCode(cursor2.getString(7));
                        collaboratee.setFullAddress(cursor2.getString(4), cursor2.getString(5), cursor2.getString(6), cursor2.getString(7));
                        collaboratee.setCell(cursor2.getString(8));
                        collaboratee.setSurname(cursor2.getString(9));
                        collaboratee.setGivenNames(cursor2.getString(10));
                        collaboratee.setPhoneId(cursor2.getString(11));
                        collaboratee.setCellId(cursor2.getString(12));
                        collaboratee.setEmailId(cursor2.getString(13));
                        collaboratee.setPhoneJsonId(cursor2.getString(14));
                        collaboratee.setCellJsonId(cursor2.getString(15));
                        collaboratee.setEmailJsonId(cursor2.getString(16));
                        collaboratee.setAddressJsonId(cursor2.getString(17));
                        collaboratee.setPartnerJsonId(cursor2.getString(18));
                    }
                }


                cursor2.close();

                mPartner2 = collaboratee;

            }
            else {
                mPartner2 = collaboratee;
            }


            return collaboratee;
        }


        /**
         * Places info into an intent after Async task has finished
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

            i.putExtra(ProfileActivity.PHONE_ID_KEY, mPartner2.getPhoneId());
            i.putExtra(ProfileActivity.CELL_ID_KEY, mPartner2.getCellId());
            i.putExtra(ProfileActivity.EMAIL_ID_KEY, mPartner2.getEmailId());

            i.putExtra(ProfileActivity.PHONE_JSON_ID_KEY, mPartner2.getPhoneJsonId());
            i.putExtra(ProfileActivity.CELL_JSON_ID_KEY, mPartner2.getCellJsonId());
            i.putExtra(ProfileActivity.EMAIL_JSON_ID_KEY, mPartner2.getEmailJsonId());
            i.putExtra(ProfileActivity.ADDRESS_JSON_ID_KEY, mPartner2.getAddressJsonId());
            i.putExtra(ProfileActivity.PARTNER_JSON_ID_KEY, mPartner2.getPartnerJsonId());

            i.putExtra(ProfileActivity.BLOG_KEY, mPartner2.getBlog());
            i.putExtra(ProfileActivity.FAX_KEY, mPartner2.getFax());
            i.putExtra(ProfileActivity.FACEBOOK_KEY, mPartner2.getFacebook());
            i.putExtra(ProfileActivity.SKYPE_KEY, mPartner2.getSkype());
            i.putExtra(ProfileActivity.TWITTER_KEY, mPartner2.getTwitter());
            i.putExtra(ProfileActivity.WEBSITE_KEY, mPartner2.getWebsite());


            startActivity(i);
        }

    }



    /**
     * Lists profiles.
     */
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


    /**
     * Fetches a list of collaboratee IDs and names.
     * ToDo make this asyncTask get stuffs from kardia
     * if this doesn't get things from kardia, new partners will never be registered.
     */
    private class GetCollaborateesTask extends AsyncTask<Void, Void, List<Partner>> {
        @Override
        protected List<Partner> doInBackground(Void... params) {
            String partnerId = AccountManager.get(getActivity()).getUserData(mAccount, "partnerId");

            //get collaboratee stuff from the database
            Cursor cursor = getActivity().getContentResolver().query(
                    CRMContract.CollaborateeTable.CONTENT_URI,
                    new String[] { CRMContract.CollaborateeTable.PARTNER_ID, CRMContract.CollaborateeTable.PARTNER_NAME },
                    CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                    new String[] { partnerId },
                    null
            );

            //put query junk into a list
            List<Partner> collaboratees = new ArrayList<>();
            while(cursor.moveToNext()) {
                Partner collaboratee = new Partner(cursor.getString(0));
                collaboratee.setPartnerName(cursor.getString(1));
                collaboratees.add(collaboratee);
            }
            cursor.close();

            return collaboratees;
        }

        /**
         * Sets up an adapter to list profiles after Async task is complete.
         */
        @Override
        protected void onPostExecute(List<Partner> collaboratees) {
            mProfiles = collaboratees;
            setupAdapter(mProfiles);
        }
    }

    /**
     * Searches through a list of profile names for a particular substring.
     */
    public void search(String searchText) {
        ArrayList<Partner> profiles = new ArrayList<>();
        for(Partner profile : mProfiles) {
            if(profile.getPartnerName().toLowerCase().contains(searchText.toLowerCase())) {
                profiles.add(profile);
            }

        }

        setupAdapter(profiles);

    }

}