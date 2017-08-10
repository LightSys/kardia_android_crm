package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.PostJson;
import org.lightsys.crmapp.models.Partner;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    private SearchView searchView;
    private MaterialDialog materialDialog;
    private static String TAG = "Main Activity";
    private LinearLayoutManager linearLayoutManager;
    private ProfileAdapter profileAdapter;

    /**
     * Retrieves account information.
     * Sets up main activity view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Created");

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
        linearLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        setupAdapter(mProfiles);
    }

    /**
     * Builds options menu
     * Sets up "search" function
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals(""))
                    return true;

                searchView.clearFocus();
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId)
        {
            case R.id.action_logout:
                Account[] accounts = mAccountManager.getAccountsByType(CRMContract.accountType);
                Log.d(TAG, "# of Accounts: " + accounts.length);
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
    private class PersonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout mLinearLayout;
        private Partner mPartner;

        public PersonHolder(View view) {
            super(view);

            mLinearLayout = (LinearLayout) view;

            view.setOnClickListener(this);
        }

        /**
         * Binds profile information to the view.
         */
        public void bindProfile(Partner partner) {
            if (partner.getProfilePictureFilename() == null || partner.getProfilePictureFilename().equals(""))
            {
                Picasso.with(getApplication())
                        .load(R.drawable.persona)
                        .resize(64,64)
                        .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            }
            else {
                File directory = getDir("imageDir", Context.MODE_PRIVATE);
                int indexoffileName = partner.getProfilePictureFilename().lastIndexOf("/");
                String finalPath = directory + "/" + partner.getProfilePictureFilename().substring(indexoffileName + 1);

                Picasso.with(getApplication())
                        .load(new File(finalPath))
                        .resize(64, 64)
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            }

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
        }
    }

    /**
     * Lists profiles.
     */
    private class ProfileAdapter extends RecyclerView.Adapter<PersonHolder> {
        private List<Partner> mCollaboratees;

        public ProfileAdapter(List<Partner> collaboratees) {
            mCollaboratees = collaboratees;
        }

        @Override
        public PersonHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getApplication());
            View rootView = inflater.inflate(R.layout.profile_listitem, container, false);

            return new PersonHolder(rootView);
        }

        @Override
        public void onBindViewHolder(PersonHolder partnerHolder, int position) {
            partnerHolder.bindProfile(mCollaboratees.get(position));
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
            String partnerId = mAccountManager.getUserData(mAccount, "partnerId");

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
                    collaboratee.setPartnerId(cursor.getString(partnerIdIndex));
                    collaboratee.setPartnerName(cursor.getString(partnerNameIndex));
                    collaboratee.setProfilePictureFilename(cursor.getString(profilePictureIndex));
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
        new PartnerSearchTask().execute(searchText);
    }

    /**
     * Sets up adapter after Async task is complete.
     */
    private void setupAdapter(List<Partner> profiles) {
        profileAdapter = new ProfileAdapter(profiles);
        mRecyclerView.setAdapter(profileAdapter);
    }

    private class PartnerSearchTask extends AsyncTask<String, Void, List<Partner>> {
        @Override
        protected List<Partner> doInBackground(String... params) {
            KardiaFetcher fetcher = new KardiaFetcher(MainActivity.this);
            List<Partner> partners = fetcher.partnerSearch(mAccount, params[0]);
            for (Partner partner : partners) {
                try {
                    partner.setProfilePictureFilename(fetcher.getProfilePictureUrl(mAccount, partner.getPartnerId()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return partners;
        }

        @Override
        protected void onPostExecute(List<Partner> partners) {
            if (partners == null || partners.size() < 1) {
                Toast.makeText(MainActivity.this, "No Partners found", Toast.LENGTH_SHORT).show();
                return;
            }


            materialDialog = new MaterialDialog.Builder(MainActivity.this)
                    .title("Partners to Collaborate with")
                    .adapter(new PartnerSearchAdapter(partners), null)
                    .show();
        }
    }

    /**
     * View that holds collaboratee information
     */
    private class PartnerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout mLinearLayout;
        private Partner mPartner;

        public PartnerHolder(View view) {
            super(view);

            mLinearLayout = (LinearLayout) view;
        }

        /**
         * Binds profile information to the view.
         */
        public void bindProfile(Partner partner) {
            mPartner = partner;

            if (partner.getProfilePictureFilename() == null || partner.getProfilePictureFilename().equals(""))
            {
                Picasso.with(getApplication())
                        .load(R.drawable.persona)
                        .resize(64,64)
                        .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            }
            else {
                File directory = getDir("imageDir", Context.MODE_PRIVATE);
                int indexoffileName = partner.getProfilePictureFilename().lastIndexOf("/");
                String finalPath = directory + "/" + partner.getProfilePictureFilename().substring(indexoffileName + 1);

                File pictureFile = new File(finalPath);

                if (pictureFile.exists()) {
                    Log.d(TAG, "Loading image from: " + pictureFile.getPath());
                    Picasso.with(getApplication())
                            .load(pictureFile)
                            .resize(64, 64)
                            .placeholder(R.drawable.persona)
                            .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
                } else {
                    Log.d(TAG,"Loading image from: " + mAccountManager.getUserData(mAccount, "server") + partner.getProfilePictureFilename());
                    Picasso.with(getApplication())
                            .load(mAccountManager.getUserData(mAccount, "server") + partner.getProfilePictureFilename())
                            .resize(64, 64)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .placeholder(R.drawable.persona)
                            .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
                }
            }

            ((TextView) mLinearLayout.findViewById(R.id.profile_name)).setText(partner.getPartnerName());
            mLinearLayout.findViewById(R.id.add_button).setOnClickListener(this);
        }

        /**
         * Goes to get further information regarding a collaboratee after a collaboratee is selected.
         */
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, mPartner.getPartnerName() + " Selected", Toast.LENGTH_SHORT).show();
            new addPartner().execute(mPartner);
        }
    }

    /**
     * Lists profiles.
     */
    private class PartnerSearchAdapter extends RecyclerView.Adapter<PartnerHolder> {
        private List<Partner> mCollaboratees;

        PartnerSearchAdapter(List<Partner> collaboratees) {
            mCollaboratees = collaboratees;
        }

        @Override
        public PartnerHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getApplication());
            View rootView = inflater.inflate(R.layout.partner_listitem, container, false);

            return new PartnerHolder(rootView);
        }

        @Override
        public void onBindViewHolder(PartnerHolder partnerHolder, int position) {
            Partner collaboratee = mCollaboratees.get(position);
            partnerHolder.bindProfile(collaboratee);
        }

        @Override
        public int getItemCount() {
            return mCollaboratees.size();
        }
    }

    private class addPartner extends AsyncTask<Partner, Void, Partner> {

        String collaboratorId;

        @Override
        protected Partner doInBackground(Partner... params) {

            final Partner partner = params[0];
            collaboratorId = partner.getPartnerId();

            String url = Uri.parse(mAccountManager.getUserData(mAccount, "server"))
                    .buildUpon()
                    .appendEncodedPath("apps/kardia/api/crm/Partners/" + mAccountManager.getUserData(mAccount, "partnerId") + "/Collaborators")
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_format", "attrs")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .appendQueryParameter("cx__res_type", "collection")
                    .build().toString();

            final PostJson createCollaborator = new PostJson(MainActivity.this, url, createCollaboratorJson(), mAccount, true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //createCollaborator.execute();
                }
            });

            return partner;
        }

        private JSONObject createCollaboratorJson() {
            JSONObject collaborator = new JSONObject();

            //Get current date
            java.util.Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            try
            {
                //Create Date Json object of current datetime
                JSONObject jsonDate = new JSONObject();
                jsonDate.put("month", cal.get(Calendar.MONTH));
                jsonDate.put("year", cal.get(Calendar.YEAR));
                jsonDate.put("day", cal.get(Calendar.DAY_OF_MONTH));
                jsonDate.put("minute", cal.get(Calendar.MINUTE));
                jsonDate.put("second", cal.get(Calendar.SECOND));
                jsonDate.put("hour", cal.get(Calendar.HOUR));

                collaborator.put("p_partner_key", mAccountManager.getUserData(mAccount, "partnerId"));
                collaborator.put("e_collaborator", collaboratorId);
                collaborator.put("e_collab_type_id", 1);
                collaborator.put("e_collaborator_status", "A");
                collaborator.put("e_is_automatic", 0);
                collaborator.put("s_date_created", jsonDate);
                collaborator.put("s_date_modified", jsonDate);
                collaborator.put("s_created_by", mAccountManager.getUserData(mAccount, "partnerId"));
                collaborator.put("s_modified_by", mAccountManager.getUserData(mAccount, "partnerId"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return collaborator;
        }

        @Override
        protected void onPostExecute(Partner partner) {
            super.onPostExecute(partner);

            int newPosition = mProfiles.size() + 1;

            mProfiles.add(partner);
            materialDialog.dismiss();
            mRecyclerView.getAdapter().notifyItemInserted(newPosition);
            linearLayoutManager.scrollToPosition(newPosition);
            Log.d(TAG, "New RecyclerView Size: " + newPosition);
        }
    }
}
