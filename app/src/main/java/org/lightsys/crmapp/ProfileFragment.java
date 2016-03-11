package org.lightsys.crmapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by cubemaster on 3/10/16.
 */
public class ProfileFragment extends Fragment {
    private static final String LOG_TAG = ProfileFragment.class.getName();

    private String mName;
    private String mPartnerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(arguments != null) {
            mName = arguments.getString(ProfileActivity.NAME_KEY);
            mPartnerId = arguments.getString(ProfileActivity.PARTNER_ID_KEY);
        }

        Log.d(LOG_TAG, mName);
        Log.d(LOG_TAG, mPartnerId);

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        ((TextView)rootView.findViewById(R.id.textview_profile)).setText(mPartnerId);

        return rootView;
    }
}
