package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.models.Engagement;
import org.lightsys.crmapp.models.Partner;

import java.util.ArrayList;
import java.util.List;

public class EngagementActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private AccountManager mAccountManager;

    private RecyclerView mRecyclerView;
    private List<Engagement> mEngagements;
    private Account mAccount;
    private List<Partner> collaboratees;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Engagement Activity", "Created");

        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(CRMContract.accountType);
        if(accounts.length == 0) {
            mAccountManager.addAccount(CRMContract.accountType, null, null, null, this, null, null);
            finish();
        } else if (accounts.length > 0){
            mAccount = accounts[0];
            new GetCollaborateeIdsTask().execute();
        }

        setupNavigationView();
        setupToolbar();
        setupFAB();

        mRecyclerView = (android.support.v7.widget.RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
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
            case R.id.action_collaborators:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
        }

        return true;
    }

    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "FAB Clicked", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(getApplicationContext(), NewProfileActivity.class);
//                startActivity(i);
            }
        });
    }

    /**
     * Searches through a list of profile names for a particular substring.
     */
    public void search(String searchText) {
//        ArrayList<Engagement> profiles = new ArrayList<>();
//        for(Engagement engagement : mEngagements) {
//            if(engagement.PartnerId.toLowerCase().contains(searchText.toLowerCase())) {
//                profiles.add(engagement);
//            }
//        }
//        setupAdapter(profiles);
    }

    /**
     * Sets up adapter after Async task is complete.
     */
    private void setupAdapter(List<Engagement> engagements) {
        mRecyclerView.setAdapter(new EngagementAdapter(engagements));
    }

    private class EngagementHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout mLinearLayout;

        public EngagementHolder(View view) {
            super(view);

            mLinearLayout = (LinearLayout) view;

            view.setOnClickListener(this);
        }

        /**
         * Binds profile information to the view.
         */
        public void bindProfile(Engagement engagement) {

            ((TextView) mLinearLayout.findViewById(R.id.engagementName)).setText(engagement.PartnerName);
            ((TextView) mLinearLayout.findViewById(R.id.engagementTrack)).setText(engagement.TrackName);
        }

        /**
         * Goes to get further information regarding a collaboratee after a collaboratee is selected.
         */
        @Override
        public void onClick(View v) {
//            mPartner2 = mPartner;
//            Intent i = new Intent(getApplication(), ProfileActivity.class);
//            i.putExtra(PARTNER_ID_KEY, mPartner2.PartnerId);
//            i.putExtra(PARTNER_NAME, mPartner2.PartnerName);
//            startActivity(i);
        }
    }

    private class EngagementAdapter extends RecyclerView.Adapter<EngagementHolder>
    {
        List<Engagement> engagements;
        public EngagementAdapter(List<Engagement> engagements)
        {
            this.engagements = engagements;
        }

        @Override
        public EngagementHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getApplication());
            View rootView = inflater.inflate(R.layout.engagement_listitem, parent, false);

            return new EngagementHolder(rootView);
        }

        @Override
        public void onBindViewHolder(EngagementHolder holder, int position)
        {
            Engagement engagement = mEngagements.get(position);
            holder.bindProfile(engagement);
        }

        @Override
        public int getItemCount()
        {
            return this.engagements.size();
        }
    }

    private class GetEngagementsTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            KardiaFetcher fetcher = new KardiaFetcher(getApplicationContext());
            mEngagements = fetcher.getEngagements(mAccount, collaboratees);

            ContentResolver contentResolver = getContentResolver();
            for (Engagement engagement : mEngagements)
            {
                ContentValues values = new ContentValues();
                values.put(CRMContract.EngagementTable.PARTNER_ID, engagement.PartnerId);
                values.put(CRMContract.EngagementTable.ENGAGEMENT_ID, engagement.EngagementId);
                values.put(CRMContract.EngagementTable.DESCRIPTION, engagement.Description);
                values.put(CRMContract.EngagementTable.ENGAGEMENT_TRACK, engagement.TrackName);
                values.put(CRMContract.EngagementTable.ENGAGEMENT_STEP, engagement.StepName);
                values.put(CRMContract.EngagementTable.ENGAGEMENT_COMMENTS, engagement.Comments);
                values.put(CRMContract.EngagementTable.COMPLETION_STATUS, engagement.CompletionStatus);

                contentResolver.insert(CRMContract.EngagementTable.CONTENT_URI, values);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            setupAdapter(mEngagements);
        }
    }

    private class GetCollaborateeIdsTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            collaboratees = new ArrayList<>();
            //get collaborateeIds from the database
            Cursor cursor = getContentResolver().query(
                    CRMContract.CollaborateeTable.CONTENT_URI,
                    new String[] { CRMContract.CollaborateeTable.PARTNER_ID, CRMContract.CollaborateeTable.PARTNER_NAME },
                    CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                    new String[] { mAccountManager.getUserData(mAccount, "partnerId") },
                    null
            );

            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    Partner partner = new Partner();
                    partner.PartnerId = cursor.getString(0);
                    partner.PartnerName = cursor.getString(1);
                    collaboratees.add(partner);
                }
                cursor.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            new GetEngagementsTask().execute();
        }
    }
}
