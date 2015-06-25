package org.lightsys.crmapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lightsys.crmapp.data.DataConnection;
import org.lightsys.crmapp.data.PullType;

/**
 * Created by Jake on 6/17/2015.
 */
public class PeopleTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.people_tab,container,false);

        return v;
    }
}