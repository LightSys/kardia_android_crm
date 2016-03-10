package org.lightsys.crmapp;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.Partner;
import org.lightsys.crmapp.data.User;
import org.lightsys.crmapp.data.UserLab;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cubemaster on 3/7/16.
 */
public class MainFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<Partner> mProfiles = new ArrayList<>();
    private User mUser;

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = UserLab.get(getActivity()).getUser();
        if(mUser.getUsername() != null) {
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
        // TODO create adapter and set iton mListView
        // TODO call setOnItemClickListener on mListView

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
        }

        @Override
        public void onClick(View v) {
            String name = ((TextView)((LinearLayout)v).findViewById(R.id.profile_name)).getText().toString();
            Snackbar.make(getActivity().findViewById(R.id.coordinatorlayout),
                    "TODO launch ProfileActivity for " + name,
                    Snackbar.LENGTH_LONG).show();
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
            return new KardiaFetcher().getCollaboratees(mUser);
        }

        @Override
        protected void onPostExecute(List<Partner> collaboratees) {
            mProfiles = collaboratees;
            setupAdapter();
        }
    }
}