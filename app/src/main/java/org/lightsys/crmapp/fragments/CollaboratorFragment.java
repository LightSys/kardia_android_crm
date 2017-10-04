package org.lightsys.crmapp.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.activities.FormActivity;
import org.lightsys.crmapp.activities.MainActivity;
import org.lightsys.crmapp.activities.NewProfileActivity;
import org.lightsys.crmapp.activities.ProfileActivity;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.data.PostJson;
import org.lightsys.crmapp.data.infoTypes.Form;
import org.lightsys.crmapp.models.Partner;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.lightsys.crmapp.activities.FormActivity.LOG_TAG;
import static org.lightsys.crmapp.activities.ProfileActivity.PARTNER_ID_KEY;
import static org.lightsys.crmapp.activities.ProfileActivity.saveImageFromUrl;
import static org.lightsys.crmapp.data.LocalDBTables.CollaborateeTable.PARTNER_NAME;
import static org.lightsys.crmapp.data.LocalDBTables.CollaborateeTable.PROFILE_PICTURE;

/**
 * @author otter57
 * created on 9/14/2017.
 *
 * Displays sign up sheet, allows users to add themselves to the list
 *
 */
public class CollaboratorFragment extends Fragment {

    private AccountManager mAccountManager;
    private Account mAccount;
    private String partnerId;
    private List<Partner> mProfiles = new ArrayList<>();
    private ProfileAdapter profileAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MaterialDialog materialDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.collaborator_layout, container, false);
        getActivity().setTitle("Kardia CRM");

        Bundle args = getArguments();
        if (args != null){
            search(args.getString(MainActivity.SEARCH_QUERY));
        }

        mAccountManager = AccountManager.get(this.getActivity());
        Account[] accounts = mAccountManager.getAccountsByType(LocalDBTables.accountType);
        if(accounts.length == 0) {
            mAccountManager.addAccount(LocalDBTables.accountType, null, null, null, this.getActivity(), null, null);
            this.getActivity().finish();
        } else if (accounts.length > 0){
            mAccount = accounts[0];
            new GetCollaborateesTask().execute(mAccountManager.getUserData(mAccount, "partnerId"));
        }

        mRecyclerView = (android.support.v7.widget.RecyclerView) v.findViewById(R.id.recycler_view_profiles);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplication());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        partnerId = mAccountManager.getUserData(mAccount, "partnerId");
        setupFAB(v);
        setupAdapter(mProfiles);




        return v;
    }

    private void setupFAB(View v) {
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), NewProfileActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * Fetches a list of collaboratee IDs and names.
     * if this doesn't get things from kardia, new partners will never be registered.
     */
    private class GetCollaborateesTask extends AsyncTask<String, Void, List<Partner>> {

        @Override
        protected List<Partner> doInBackground(String... params) {
            String partnerId = params[0];
            Log.d(LOG_TAG, "doInBackground: " + partnerId);

            //get collaboratee stuff from the database
            Cursor cursor = getActivity().getContentResolver().query(
                    LocalDBTables.CollaborateeTable.CONTENT_URI,
                    new String[] {
                            LocalDBTables.CollaborateeTable.PARTNER_ID,
                            LocalDBTables.CollaborateeTable.PARTNER_NAME,
                            LocalDBTables.CollaborateeTable.PROFILE_PICTURE },
                    LocalDBTables.CollaborateeTable.COLLABORATER_ID + " = ?",
                    new String[] { partnerId },
                    null
            );

            //put query stuff into a list
            List<Partner> collaboratees = new ArrayList<>();
            if (cursor != null)
            {
                int partnerIdIndex = cursor.getColumnIndex(LocalDBTables.CollaborateeTable.PARTNER_ID);
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
     * Sets up adapter after Async task is complete.
     */
    private void setupAdapter(List<Partner> profiles) {
        profileAdapter = new ProfileAdapter(profiles);
        mRecyclerView.setAdapter(profileAdapter);
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
            LayoutInflater inflater = LayoutInflater.from(getActivity().getApplication());
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
            if (partner.ProfilePictureFilename == null || partner.ProfilePictureFilename.equals(""))
            {
                Picasso.with(getActivity().getApplication())
                        .load(R.drawable.persona)
                        .resize(64,64)
                        .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            }
            else
            {
                saveImageFromUrl(mAccountManager.getUserData(mAccount, "server"), getActivity().getApplicationContext(), partner.ProfilePictureFilename);
                File directory = getActivity().getDir("imageDir", Context.MODE_PRIVATE);
                int indexoffileName = partner.ProfilePictureFilename.lastIndexOf("/");
                String finalPath = directory + "/" + partner.ProfilePictureFilename.substring(indexoffileName + 1);

                Picasso.with(getActivity().getApplication())
                        .load(new File(finalPath))
                        .resize(64, 64)
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
            Intent i = new Intent(getActivity().getApplication(), ProfileActivity.class);
            i.putExtra(PARTNER_ID_KEY, mPartner.PartnerId);
            i.putExtra(PARTNER_NAME, mPartner.PartnerName);
            startActivity(i);
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

            if (partner.ProfilePictureFilename == null || partner.ProfilePictureFilename.equals(""))
            {
                Picasso.with(getActivity().getApplication())
                        .load(R.drawable.persona)
                        .resize(64,64)
                        .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            }
            else {
                File directory = getActivity().getDir("imageDir", Context.MODE_PRIVATE);
                int indexoffileName = partner.ProfilePictureFilename.lastIndexOf("/");
                String finalPath = directory + "/" + partner.ProfilePictureFilename.substring(indexoffileName + 1);

                File pictureFile = new File(finalPath);

                if (pictureFile.exists()) {
                    Log.d(LOG_TAG, "Loading image from: " + pictureFile.getPath());
                    Picasso.with(getActivity().getApplication())
                            .load(pictureFile)
                            .resize(64, 64)
                            .placeholder(R.drawable.persona)
                            .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
                } else {
                    Log.d(LOG_TAG,"Loading image from: " + mAccountManager.getUserData(mAccount, "server") + partner.ProfilePictureFilename);
                    Picasso.with(getActivity().getApplication())
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
            Toast.makeText(getActivity(), mPartner.PartnerName + " Selected", Toast.LENGTH_SHORT).show();
            new addCollaboratee().execute(mPartner);
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
            LayoutInflater inflater = LayoutInflater.from(getActivity().getApplication());
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

    private class addCollaboratee extends AsyncTask<Partner, Void, Partner> {

        String collaboratorId;

        @Override
        protected Partner doInBackground(Partner... params) {

            final Partner collaboratee = params[0];
            collaboratorId = mAccountManager.getUserData(mAccount, "partnerId");

            String url = Uri.parse(mAccountManager.getUserData(mAccount, "server"))
                    .buildUpon()
                    .appendEncodedPath("apps/kardia/api/crm/Partners/" + collaboratorId + "/Collaboratees")
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_format", "attrs")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .appendQueryParameter("cx__res_type", "collection")
                    .build().toString();

            final PostJson createCollaboratee = new PostJson(getActivity(), url, createCollaborateeJson(collaboratee), mAccount, true);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    createCollaboratee.execute();
                }
            });

            ContentValues values = new ContentValues();
            values.put(LocalDBTables.CollaborateeTable.PARTNER_ID, collaboratorId);
            values.put(LocalDBTables.CollaborateeTable.COLLABORATER_ID, mAccountManager.getUserData(mAccount, "partnerId"));
            values.put(LocalDBTables.CollaborateeTable.PARTNER_NAME, collaboratee.PartnerName);
            values.put(LocalDBTables.CollaborateeTable.PROFILE_PICTURE, collaboratee.ProfilePictureFilename);

            getActivity().getContentResolver().insert(LocalDBTables.CollaborateeTable.CONTENT_URI, values);

            return collaboratee;
        }

        private JSONObject createCollaborateeJson(Partner collaboratee) {
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

                collaborator.put("p_partner_key", collaboratee.PartnerId);
                collaborator.put("e_collaborator", collaboratorId);
                collaborator.put("e_collab_type_id", 1);
                collaborator.put("e_collaborator_status", "A");
                collaborator.put("e_is_automatic", 0);
                collaborator.put("s_date_created", jsonDate);
                collaborator.put("s_date_modified", jsonDate);
                collaborator.put("s_created_by", collaboratorId);
                collaborator.put("s_modified_by", collaboratorId);

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
            Log.d(LOG_TAG, "New RecyclerView Size: " + newPosition);
        }
    }

    /*
     * get all collaborators from localDB
     */

    private ArrayList<Partner> getCollaborators(String searchQuery){
            ArrayList<Partner> partners =  new ArrayList<>();

            Cursor cursor = getActivity().getContentResolver().query(
                    LocalDBTables.CollaborateeTable.CONTENT_URI,
                    new String[] {LocalDBTables.CollaborateeTable.PARTNER_ID,
                            LocalDBTables.CollaborateeTable.PARTNER_NAME,
                            LocalDBTables.CollaborateeTable.PROFILE_PICTURE
                    },
                    null,
                    null,
                    LocalDBTables.CollaborateeTable.PARTNER_NAME + " DESC"
            );

            try {
                while (cursor.moveToNext()) {
                    Partner temp = new Partner();
                    temp.PartnerId = cursor.getString(0);
                    temp.PartnerName = (cursor.getString(1));
                    temp.ProfilePictureFilename = (cursor.getString(2));

                    partners.add(temp);
                }
            }catch(NullPointerException ne) {
                ne.printStackTrace();
            }
        cursor.close();

        materialDialog = new MaterialDialog.Builder(getActivity())
                .title("Partners to Collaborate with")
                .adapter(new PartnerSearchAdapter(partners), null)
                .show();

        return partners;
    }

    /**
     * Searches through a list of profile names for a particular substring.
     */
    public void search(String searchText) {
        //getCollaborators(searchText);

        new PartnerSearchTask().execute(searchText);
    }

    private class PartnerSearchTask extends AsyncTask<String, Void, List<Partner>> {
        @Override
        protected List<Partner> doInBackground(String... params) {
            KardiaFetcher fetcher = new KardiaFetcher(getActivity());
            List<Partner> partners = fetcher.partnerSearch(mAccount, params[0]);
            for (Partner partner : partners) {
                try {
                    partner.ProfilePictureFilename = fetcher.getProfilePictureUrl(mAccount, partner.PartnerId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return partners;
        }

        @Override
        protected void onPostExecute(List<Partner> partners) {
            if (partners == null || partners.size() < 1) {
                Toast.makeText(getActivity(), "No Partners found", Toast.LENGTH_SHORT).show();
                return;
            }


            materialDialog = new MaterialDialog.Builder(getActivity())
                    .title("Partners to Collaborate with")
                    .adapter(new PartnerSearchAdapter(partners), null)
                    .show();
        }
    }
}
