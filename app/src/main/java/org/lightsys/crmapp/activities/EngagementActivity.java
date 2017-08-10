package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.models.Engagement;
import org.lightsys.crmapp.models.EngagementStep;
import org.lightsys.crmapp.models.EngagementTrack;
import org.lightsys.crmapp.models.Partner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.lightsys.crmapp.data.CRMContract.CollaborateeTable.PARTNER_NAME;

public class EngagementActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = "Engagement Activity";
    private AccountManager mAccountManager;

    private RecyclerView mRecyclerView;
    private List<Engagement> mEngagements;
    private List<Engagement> nonArchivedEngagements = new ArrayList<>();
    private Account mAccount;
    private List<Partner> collaboratees;
    public static String PARTNER_ID = "partnerId";
    public static String ENGAGEMENT_ID = "engagementId";
    public static String DESCRIPTION = "description";
    public static String TRACK_NAME = "trackName";
    public static String STEP_NAME = "stepName";
    public static String COMMENTS = "comments";
    public static String COMPLETON_STATUS = "completionStatus";
    private List<EngagementTrack> tracks;
    private List<EngagementStep> steps;
    private MaterialDialog partnerResultsDialog;
    private MaterialDialog partnerDetailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            new GetEngagementTracksTask().execute();
        }

        setupNavigationView();
        setupToolbar();
        setupFAB();

        mRecyclerView = (android.support.v7.widget.RecyclerView) findViewById(R.id.recyclerview_profiles);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
    }

    /**
     * Builds options menu
     * Sets up "search" function
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    private void setupAdapter(List<Engagement> engagements) {
        mRecyclerView.setAdapter(new EngagementAdapter(engagements));
    }

    private class PartnerSearchTask extends AsyncTask<String, Void, List<Partner>> {
        @Override
        protected List<Partner> doInBackground(String... params) {
            KardiaFetcher fetcher = new KardiaFetcher(EngagementActivity.this);
            List<Partner> partners = fetcher.partnerSearch(mAccount, params[0]);
            for (Partner partner : partners) {
                try {
                    partner.ProfilePictureFilename = fetcher.getProfilePictureUrl(mAccount, partner.PartnerId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //TODO: keep only current collaboratees.
            return partners;
        }

        @Override
        protected void onPostExecute(List<Partner> partners) {
            if (partners == null || partners.size() < 1) {
                Toast.makeText(EngagementActivity.this, "No Partners found", Toast.LENGTH_SHORT).show();
                return;
            }

            partnerResultsDialog = new MaterialDialog.Builder(EngagementActivity.this)
                    .title("Start Partner on new Engagement")
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

            if (partner.ProfilePictureFilename == null || partner.ProfilePictureFilename.equals("")) {
                Picasso.with(getApplication())
                        .load(R.drawable.persona)
                        .resize(64, 64)
                        .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            } else {
                File directory = getDir("imageDir", Context.MODE_PRIVATE);
                int indexoffileName = partner.ProfilePictureFilename.lastIndexOf("/");
                String finalPath = directory + "/" + partner.ProfilePictureFilename.substring(indexoffileName + 1);

                File pictureFile = new File(finalPath);

                if (pictureFile.exists()) {
                    Log.d(TAG, "Loading image from: " + pictureFile.getPath());
                    Picasso.with(getApplication())
                            .load(pictureFile)
                            .resize(64, 64)
                            .placeholder(R.drawable.persona)
                            .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
                } else {
                    Log.d(TAG, "Loading image from: " + mAccountManager.getUserData(mAccount, "server") + partner.ProfilePictureFilename);
                    Picasso.with(getApplication())
                            .load(mAccountManager.getUserData(mAccount, "server") + partner.ProfilePictureFilename)
                            .resize(64, 64)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .placeholder(R.drawable.persona)
                            .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
                }
            }

            ((TextView) mLinearLayout.findViewById(R.id.profile_name)).setText(partner.PartnerName);
            mLinearLayout.findViewById(R.id.add_button).setOnClickListener(this);
        }

        /**
         * Goes to get further information regarding a collaboratee after a collaboratee is selected.
         */
        @Override
        public void onClick(View v) {
            Toast.makeText(EngagementActivity.this, mPartner.PartnerName + " Selected", Toast.LENGTH_SHORT).show();

            View customView = getLayoutInflater().inflate(R.layout.engagement_dialog_detail, null);
            ImageView image = (ImageView) customView.findViewById(R.id.profile_photo);

            if (mPartner.ProfilePictureFilename == null || mPartner.ProfilePictureFilename.equals("")) {
                Picasso.with(EngagementActivity.this)
                        .load(R.drawable.persona)
                        .resize(64, 64)
                        .into(image);
            } else {
                Picasso.with(EngagementActivity.this)
                        .load(mPartner.ProfilePictureFilename)
                        .resize(64, 64)
                        .placeholder(R.drawable.persona)
                        .into(image);
            }

            TextView profileName = (TextView) customView.findViewById(R.id.profile_name);
            profileName.setText(mPartner.PartnerName);

            Spinner trackSpinner = (Spinner) customView.findViewById(R.id.trackSpinner);
            ArrayAdapter<String> adapter;
            List<String> list = new ArrayList<>();

            for (EngagementTrack track : tracks) {
                list.add(track.TrackName);
            }

            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            trackSpinner.setAdapter(adapter);

            Button submitButton = (Button) customView.findViewById(R.id.submit);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: AsyncTask to Add new engagement Track to REST API/Database
                }
            });

            partnerResultsDialog = new MaterialDialog.Builder(EngagementActivity.this)
                    .title("Start Partner on new Engagement")
                    .customView(customView, false)
                    .show();
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

    private class EngagementHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout mLinearLayout;
        private Engagement engagement;

        public EngagementHolder(View view) {
            super(view);

            mLinearLayout = (LinearLayout) view;

            view.setOnClickListener(this);
        }

        /**
         * Binds profile information to the view.
         */
        public void bindProfile(Engagement engagement) {
            this.engagement = engagement;
            ((TextView) mLinearLayout.findViewById(R.id.engagementName)).setText(engagement.PartnerName);
            ((TextView) mLinearLayout.findViewById(R.id.engagementTrack)).setText(engagement.TrackName);
            ((TextView) mLinearLayout.findViewById(R.id.engagementStep)).setText(engagement.StepName);
            ((TextView) mLinearLayout.findViewById(R.id.engagementComment)).setText(engagement.Comments);
            
            if (engagement.ProfilePicture == null || engagement.ProfilePicture.equals(""))
            {
                Picasso.with(getApplication())
                        .load(R.drawable.persona)
                        .resize(64,64)
                        .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            }
            else
            {
                File directory = getDir("imageDir", Context.MODE_PRIVATE);
                int indexoffileName = engagement.ProfilePicture.lastIndexOf("/");
                String finalPath = directory + "/" + engagement.ProfilePicture.substring(indexoffileName + 1);

                Picasso.with(getApplication())
                        .load(new File(finalPath))
                        .resize(64,64)
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            }
        }

        /**
         * Goes to get further information regarding a collaboratee after a collaboratee is selected.
         */
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplication(), EngagementDetailActivity.class);
            i.putExtra(PARTNER_ID, engagement.PartnerId);
            i.putExtra(ENGAGEMENT_ID, engagement.EngagementId);
            i.putExtra(DESCRIPTION, engagement.Description);
            i.putExtra(TRACK_NAME, engagement.TrackName);
            i.putExtra(STEP_NAME, engagement.StepName);
            i.putExtra(COMMENTS, engagement.Comments);
            i.putExtra(COMPLETON_STATUS, engagement.CompletionStatus);
            i.putExtra(PARTNER_NAME, engagement.PartnerName);
            startActivity(i);
        }
    }

    private class EngagementAdapter extends RecyclerView.Adapter<EngagementHolder> {
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
            Engagement engagement = engagements.get(position);
            holder.bindProfile(engagement);
        }

        @Override
        public int getItemCount()
        {
            return this.engagements.size();
        }
    }

    private class GetCollaborateeIdsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids)
        {
            collaboratees = new ArrayList<>();
            //get collaborateeIds from the database
            Cursor cursor = getContentResolver().query(
                    CRMContract.CollaborateeTable.CONTENT_URI,
                    new String[] { CRMContract.CollaborateeTable.PARTNER_ID, PARTNER_NAME, CRMContract.CollaborateeTable.PROFILE_PICTURE },
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
                    partner.ProfilePictureFilename = cursor.getString(2);
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
            new GetEngagementsforCollaborateesTask().execute();
        }
    }

    private class GetEngagementsforCollaborateesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids)
        {
            KardiaFetcher fetcher = new KardiaFetcher(getApplicationContext());
            mEngagements = fetcher.getEngagements(mAccount, collaboratees);

            if (mEngagements != null) {

                ContentResolver contentResolver = getContentResolver();
                for (Engagement engagement : mEngagements) {
                    if (!engagement.Archived) {

                        nonArchivedEngagements.add(engagement);

                        ContentValues values = new ContentValues();
                        values.put(CRMContract.EngagementTable.PARTNER_ID, engagement.PartnerId);
                        values.put(CRMContract.EngagementTable.ENGAGEMENT_ID, engagement.EngagementId);
                        values.put(CRMContract.EngagementTable.DESCRIPTION, engagement.Description);
                        values.put(CRMContract.EngagementTable.ENGAGEMENT_TRACK, engagement.TrackName);
                        values.put(CRMContract.EngagementTable.ENGAGEMENT_STEP, engagement.StepName);
                        values.put(CRMContract.EngagementTable.ENGAGEMENT_COMMENTS, engagement.Comments);
                        values.put(CRMContract.EngagementTable.COMPLETION_STATUS, engagement.CompletionStatus);
                        values.put(CRMContract.EngagementTable.IS_ARCHIVED, engagement.Archived);

                        contentResolver.insert(CRMContract.EngagementTable.CONTENT_URI, values);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            setupAdapter(nonArchivedEngagements);
        }
    }

    private class GetEngagementTracksTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            KardiaFetcher fetcher = new KardiaFetcher(getApplicationContext());
            try
            {
                tracks = fetcher.getEngagementTracks(mAccount);
                Log.d("Engagement Activity", "Tracks Count: " + tracks.size());

                for (EngagementTrack track : tracks)
                {
                    ContentValues values = new ContentValues();

                    values.put(CRMContract.EngagementTrackTable.TRACK_ID, track.TrackId);
                    values.put(CRMContract.EngagementTrackTable.TRACK_NAME, track.TrackName);
                    values.put(CRMContract.EngagementTrackTable.TRACK_DESCRIPTION, track.TrackDescription);
                    values.put(CRMContract.EngagementTrackTable.TRACK_STATUS, track.TrackStatus);

                    getContentResolver().insert(CRMContract.EngagementTrackTable.CONTENT_URI, values);
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            new getEngagementStepsTask().execute();
        }
    }

    private class getEngagementStepsTask  extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params)
        {
            KardiaFetcher fetcher = new KardiaFetcher(getApplicationContext());
            try
            {
                steps = fetcher.getEngagementSteps(mAccount, tracks);
                Log.d("Engagement Activity", "Steps Count: " + steps.size());

                for (EngagementStep step : steps)
                {
                    ContentValues values = new ContentValues();

                    values.put(CRMContract.EngagementStepTable.TRACK_ID, step.TrackId);
                    values.put(CRMContract.EngagementStepTable.TRACK_NAME, step.TrackName);
                    values.put(CRMContract.EngagementStepTable.STEP_DESCRIPTION, step.StepDescription);
                    values.put(CRMContract.EngagementStepTable.STEP_ID, step.StepId);
                    values.put(CRMContract.EngagementStepTable.STEP_NAME, step.StepName);
                    values.put(CRMContract.EngagementStepTable.STEP_SEQUENCE, step.StepSequence);

                    getContentResolver().insert(CRMContract.EngagementStepTable.CONTENT_URI, values);
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }
}
