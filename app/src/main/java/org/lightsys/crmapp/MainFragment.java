package org.lightsys.crmapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by cubemaster on 3/7/16.
 */
public class MainFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ProfileAdapter mAdapter;

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        updateUI();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void updateUI() {
        mAdapter = new ProfileAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private class ProfileHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLinearLayout;

        public ProfileHolder(View view) {
            super(view);

            mLinearLayout = (LinearLayout) view;
        }
    }

    private class ProfileAdapter extends RecyclerView.Adapter<ProfileHolder> {
        String[] mProfiles = new String[]{"Josh","Nathan","David","Benjamin","Anna","Josh","Nathan","David","Benjamin","Anna"};

        @Override
        public ProfileHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.profile_listitem, container, false);

            return new ProfileHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ProfileHolder holder, int position) {
            ((TextView)holder.mLinearLayout.findViewById(R.id.profile_name)).setText(mProfiles[position]);
        }

        @Override
        public int getItemCount() {
            return mProfiles.length;
        }
    }
}