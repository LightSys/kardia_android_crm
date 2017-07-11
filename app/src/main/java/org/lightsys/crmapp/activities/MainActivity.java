package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.models.Partner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.lightsys.crmapp.activities.ProfileActivity.PARTNER_ID_KEY;
import static org.lightsys.crmapp.data.CRMContract.CollaborateeTable.PARTNER_NAME;
import static org.lightsys.crmapp.data.CRMContract.CollaborateeTable.PROFILE_PICTURE;


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
    private NavigationView navigationView;

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
            new GetCollaborateesTask().execute(mAccountManager.getUserData(mAccount, "partnerId"));
        }

        setContentView(R.layout.activity_main);

        setupNavigationView();
        setupToolbar();
        setupFAB();

        mRecyclerView = (android.support.v7.widget.RecyclerView) findViewById(R.id.recyclerview);
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

    /**
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
        navigationView = (NavigationView) findViewById(R.id.mainNavigation);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.action_collaborators);
        navigationView.getMenu().findItem(R.id.action_collaborators).setCheckable(true);
        navigationView.getMenu().findItem(R.id.action_collaborators).setChecked(true);
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
                break;
            case R.id.action_engagement:
                Intent intent = new Intent(this, EngagementActivity.class);
                startActivity(intent);
                break;
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
            if (partner.ProfilePictureFilename == null || partner.ProfilePictureFilename.equals(""))
            {
                Picasso.with(getApplication())
                        .load(R.drawable.persona)
                        .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            }
            else
            {
                File directory = getDir("imageDir", Context.MODE_PRIVATE);
                int indexoffileName = partner.ProfilePictureFilename.lastIndexOf("/");
                String finalPath = directory + "/" + partner.ProfilePictureFilename.substring(indexoffileName + 1);

                Picasso.with(getApplication())
                        .load(new File(finalPath))
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            }
            ((TextView) mLinearLayout.findViewById(R.id.profile_name)).setText(partner.PartnerName);

            mPartner = partner;
        }

        /**
         * Goes to get further information regarding a collaboratee after a collaboratee is selected.
         */
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplication(), ProfileActivity.class);
            i.putExtra(PARTNER_ID_KEY, mPartner.PartnerId);
            i.putExtra(PARTNER_NAME, mPartner.PartnerName);
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
     * Fetches a list of collaboratee IDs and names.
     */
    private class GetCollaborateesTask extends AsyncTask<String, Void, List<Partner>> {

        @Override
        protected List<Partner> doInBackground(String... params) {
            String partnerId = params[0];

            //get collaboratee stuff from the database
            Cursor cursor = getContentResolver().query(
                    CRMContract.CollaborateeTable.CONTENT_URI,
                    new String[] {
                            CRMContract.CollaborateeTable.PARTNER_ID,
                            CRMContract.CollaborateeTable.PARTNER_NAME,
                            CRMContract.CollaborateeTable.PROFILE_PICTURE },
                    CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                    new String[] { partnerId },
                    null
            );

            //put query stuff into a list
            List<Partner> collaboratees = new ArrayList<>();
            if (cursor != null)
            {
                int partnerIdIndex = cursor.getColumnIndex(CRMContract.CollaborateeTable.PARTNER_ID);
                int partnerNameIndex = cursor.getColumnIndex(PARTNER_NAME);
                int profilePictureIndex = cursor.getColumnIndex(PROFILE_PICTURE);

                while (cursor.moveToNext())
                {
                    Partner collaboratee = new Partner();
                    collaboratee.PartnerId = cursor.getString(partnerIdIndex);
                    collaboratee.PartnerName = cursor.getString(partnerNameIndex);
                    collaboratee.ProfilePictureFilename = cursor.getString(profilePictureIndex);
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
            if(profile.PartnerName.toLowerCase().contains(searchText.toLowerCase())) {
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
