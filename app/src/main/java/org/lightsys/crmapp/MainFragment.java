package org.lightsys.crmapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.content.Intent;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import org.lightsys.crmapp.data.KardiaProvider;
import org.lightsys.crmapp.data.Partner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cubemaster on 3/7/16.
 */
public class MainFragment extends Fragment {
    private static final String LOG_TAG = MainFragment.class.getName();

    private RecyclerView mRecyclerView;
    private List<Partner> mProfiles = new ArrayList<>();
    private Account mAccount;

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Account[] accounts = AccountManager.get(getActivity()).getAccountsByType(KardiaProvider.accountType);
        if(accounts.length > 0) {
            mAccount = accounts[0];
            new GetCollaborateesTask().execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (android.support.v7.widget.RecyclerView) rootView.findViewById(R.id.recyclerview_profiles);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();

        return rootView;
    }

    private void setupAdapter() {
        if(isAdded()) {
            mRecyclerView.setAdapter(new ProfileAdapter(mProfiles));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class ProfileHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout mLinearLayout;
        private Partner mPartner;

        public ProfileHolder(View view) {
            super(view);

            mLinearLayout = (LinearLayout) view;

            view.setOnClickListener(this);
        }

        public void bindProfile(Partner partner) {
            Picasso.with(getActivity())
                    .load(partner.getProfilePictureFilename())
                    .placeholder(R.drawable.persona)
                    .into(((ImageView) mLinearLayout.findViewById(R.id.profile_photo)));
            ((TextView)mLinearLayout.findViewById(R.id.profile_name)).setText(partner.getPartnerName());
            mPartner = partner;
        }

        @Override
        public void onClick(View v) {
            String name = ((TextView)((LinearLayout)v).findViewById(R.id.profile_name)).getText().toString();
            Log.d(LOG_TAG, name);
            Intent i = new Intent(getActivity(), ProfileActivity.class);
            i.putExtra(ProfileActivity.NAME_KEY, name);
            i.putExtra(ProfileActivity.PARTNER_ID_KEY, mPartner.getPartnerId());
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
            //getContentResolver().setSyncAutomatically(accounts[0], KardiaProvider.providerAuthority, true);
            //ContentResolver.setIsSyncable(accounts[0], KardiaProvider.providerAuthority, 1);
            //ContentResolver.setSyncAutomatically(accounts[0], KardiaProvider.providerAuthority, true);
            //ContentResolver.addPeriodicSync(
            //        accounts[0], KardiaProvider.providerAuthority, new Bundle(), 60 * 60);
            Bundle settingsBundle = new Bundle();
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            //Log.d("serverLogin", AccountManager.get(getActivity()).getUserData(accounts[0], "server"));
            ContentResolver.requestSync(mAccount, KardiaProvider.providerAuthority, settingsBundle);
            Log.d("Login", "yes");
            Log.d("URI", CRMContract.StaffTable.CONTENT_URI.toString());
            try {
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            Cursor cursor = getActivity().getContentResolver().query(
                    CRMContract.CollaborateeTable.CONTENT_URI,
                    new String[] {CRMContract.CollaborateeTable.PARTNER_ID, CRMContract.CollaborateeTable.PARTNER_NAME},
                    CRMContract.CollaborateeTable.COLLABORATER_ID + " = ?",
                    new String[] {AccountManager.get(getActivity()).getUserData(mAccount, "partnerId")},
                    null
            );
            String partnerId = null;
            List<Partner> collaboratees = new ArrayList<>();
            while(cursor.moveToNext()) {
                Log.d("StaffResults", "yes");
                Partner collaboratee = new Partner(cursor.getString(0));
                collaboratee.setPartnerName(cursor.getString(1));
                collaboratees.add(collaboratee);
            }
            Log.d("Login", "yes2");
            return collaboratees;
        }

        @Override
        protected void onPostExecute(List<Partner> collaboratees) {
            mProfiles = collaboratees;
            setupAdapter();
        }
    }
}