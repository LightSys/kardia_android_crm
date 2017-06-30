package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.models.Partner;

import java.util.ArrayList;
import java.util.List;

import static org.lightsys.crmapp.activities.ProfileActivity.PARTNER_ID_KEY;
import static org.lightsys.crmapp.data.CRMContract.CollaborateeTable.PARTNER_NAME;


/**
 * Edited by Daniel Garcia on 30/June/2017
 * to merge unnecessary Fragment into MainActivity
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    private AccountManager mAccountManager;

    private RecyclerView mRecyclerView;
    private List<Partner> mProfiles = new ArrayList<>();
    private Account mAccount;


    Partner mPartner2 = new Partner();
    /**
     * Retrieves account information.
     * Sets up main activity view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Main Activity", "Created");

        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(CRMContract.accountType);
        if(accounts.length == 0) {
            mAccountManager.addAccount(CRMContract.accountType, null, null, null, this, null, null);
            finish();
        } else if (accounts.length > 0){
            mAccount = accounts[0];
            new GetCollaborateesTask().execute();
        }

        setContentView(R.layout.activity_main);

        setupNavigationView();
        setupToolbar();
        setupFAB();

        mRecyclerView = (android.support.v7.widget.RecyclerView) findViewById(R.id.recyclerview_profiles);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));

        setupAdapter(mProfiles);
    }

    /**
     * Builds options menu
     * Sets up "search" function
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Gets text to search for.
        final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Runs when a search is submitted.
            @Override
            public boolean onQueryTextSubmit(String s) {

                Snackbar.make(findViewById(R.id.coordinatorlayout_main),
                        "TODO execute query and repopulate",
                        Snackbar.LENGTH_LONG).show();
                return false;
            }

            /**
             * Runs a search every time the search input is changed.
             */
            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return true;
            }
        });
        return true;
    }

    /*
     * Provides option to select something within the Menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a toolbar.
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.mainNavigation);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        switch (itemId)
        {
            case R.id.action_logout:
                Account[] accounts = mAccountManager.getAccountsByType(CRMContract.accountType);
                Log.d("Main Activity", "# of Accounts: " + accounts.length);
                Account account = accounts[0];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                {
                    mAccountManager.removeAccountExplicitly(account);
                }
                else
                {
                    mAccountManager.removeAccount(account, null, null);
                }
                mAccountManager.addAccount(CRMContract.accountType, null, null, null, this, null, null);
                finish();
                return true;
        }

        return true;
    }

    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewProfileActivity.class);
                startActivity(i);
            }
        });
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
            Picasso.with(getApplication())
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
            Intent i = new Intent(getApplication(), ProfileActivity.class);
            i.putExtra(PARTNER_ID_KEY, mPartner2.getPartnerId());
            i.putExtra(PARTNER_NAME, mPartner2.getPartnerName());
            startActivity(i);
            //new getCollaborateeInfoTask().execute();
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
            LayoutInflater inflater = LayoutInflater.from(getApplication());
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
            Cursor cursor = getContentResolver().query(
                    CRMContract.CollaborateeTable.CONTENT_URI,
                    new String[] {CRMContract.CollaborateeTable.PARTNER_ID, PARTNER_NAME, CRMContract.CollaborateeTable.EMAIL,
                            CRMContract.CollaborateeTable.PHONE, CRMContract.CollaborateeTable.ADDRESS_1, CRMContract.CollaborateeTable.CITY,
                            CRMContract.CollaborateeTable.STATE_PROVINCE, CRMContract.CollaborateeTable.POSTAL_CODE, CRMContract.CollaborateeTable.CELL,
                            CRMContract.CollaborateeTable.SURNAME, CRMContract.CollaborateeTable.GIVEN_NAMES, CRMContract.CollaborateeTable.PHONE_ID,
                            CRMContract.CollaborateeTable.CELL_ID, CRMContract.CollaborateeTable.EMAIL_ID, CRMContract.CollaborateeTable.PHONE_JSON_ID,
                            CRMContract.CollaborateeTable.CELL_JSON_ID, CRMContract.CollaborateeTable.EMAIL_JSON_ID, CRMContract.CollaborateeTable.ADDRESS_JSON_ID,
                            CRMContract.CollaborateeTable.PARTNER_JSON_ID},
                    CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                    new String[] {mAccountManager.getUserData(mAccount, "partnerId")},
                    null);

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
                KardiaFetcher fetcher = new KardiaFetcher(getApplicationContext());
                collaboratee = fetcher.getCollaborateeInfo(mAccount, collaboratee);

                mPartner2 = collaboratee;

                //get new stuff ready to go into the database, but don't add blank things
                //blank things break things
                ContentValues values = new ContentValues();
                if (collaboratee.getPartnerId() != null) {
                    values.put(CRMContract.CollaborateeTable.COLLABORATER_ID, collaboratee.getPartnerId());
                }if (collaboratee.getPartnerName() != null) {
                    values.put(PARTNER_NAME, collaboratee.getPartnerName());
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
                getApplicationContext().getContentResolver().update(CRMContract.CollaborateeTable.CONTENT_URI, values,
                        CRMContract.CollaborateeTable.PARTNER_ID + " = ?", new String[] {collaboratee.getPartnerId()});

                mPartner2 = collaboratee;

                //pull stuff back out of the database
                //this gets the original data back in case kardia returned nothing
                Cursor cursor2 = getContentResolver().query(
                        CRMContract.CollaborateeTable.CONTENT_URI,
                        new String[]{CRMContract.CollaborateeTable.PARTNER_ID, PARTNER_NAME,
                                CRMContract.CollaborateeTable.EMAIL, CRMContract.CollaborateeTable.PHONE, CRMContract.CollaborateeTable.ADDRESS_1,
                                CRMContract.CollaborateeTable.CITY, CRMContract.CollaborateeTable.STATE_PROVINCE,
                                CRMContract.CollaborateeTable.POSTAL_CODE, CRMContract.CollaborateeTable.CELL, CRMContract.CollaborateeTable.SURNAME,
                                CRMContract.CollaborateeTable.GIVEN_NAMES, CRMContract.CollaborateeTable.PHONE_ID, CRMContract.CollaborateeTable.CELL_ID,
                                CRMContract.CollaborateeTable.EMAIL_ID, CRMContract.CollaborateeTable.PHONE_JSON_ID,
                                CRMContract.CollaborateeTable.CELL_JSON_ID, CRMContract.CollaborateeTable.EMAIL_JSON_ID,
                                CRMContract.CollaborateeTable.ADDRESS_JSON_ID, CRMContract.CollaborateeTable.PARTNER_JSON_ID},
                        CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                        new String[]{mAccountManager.getUserData(mAccount, "partnerId")},
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

            Intent i = new Intent(getApplication(), ProfileActivity.class);
            i.putExtra(ProfileActivity.NAME_KEY, mPartner2.getPartnerName());
            i.putExtra(PARTNER_ID_KEY, mPartner2.getPartnerId());
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
     * Fetches a list of collaboratee IDs and names.
     * ToDo make this asyncTask get stuffs from kardia
     * if this doesn't get things from kardia, new partners will never be registered.
     */
    private class GetCollaborateesTask extends AsyncTask<Void, Void, List<Partner>> {
        @Override
        protected List<Partner> doInBackground(Void... params) {
            String partnerId = mAccountManager.getUserData(mAccount, "partnerId");

            //get collaboratee stuff from the database
            Cursor cursor = getContentResolver().query(
                    CRMContract.CollaborateeTable.CONTENT_URI,
                    new String[] { CRMContract.CollaborateeTable.PARTNER_ID, PARTNER_NAME },
                    CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                    new String[] { partnerId },
                    null
            );

            //put query junk into a list
            List<Partner> collaboratees = new ArrayList<>();

            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    Partner collaboratee = new Partner(cursor.getString(cursor.getColumnIndex(CRMContract.CollaborateeTable.PARTNER_ID)), cursor.getString(cursor.getColumnIndex(PARTNER_NAME)));
                    collaboratees.add(collaboratee);
                }
                cursor.close();
            }
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

    /**
     * Sets up adapter after Async task is complete.
     */
    private void setupAdapter(List<Partner> profiles) {
        mRecyclerView.setAdapter(new ProfileAdapter(profiles));
    }

}
