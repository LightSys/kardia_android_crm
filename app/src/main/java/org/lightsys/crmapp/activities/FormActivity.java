package org.lightsys.crmapp.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.fragments.FormFragment;
import org.lightsys.crmapp.fragments.FormListFragment;

/**
 * Created by otter57 on 9/20/17.
 */

public class FormActivity extends AppCompatActivity {

    int formId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {

            FormListFragment newFrag = new FormListFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_profile_input_container, newFrag, "FormList");
            transaction.addToBackStack("FormList");
            transaction.commit();
        }

        setContentView(R.layout.activity_profile_input);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_input);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Forms");

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

}/*{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = new Bundle();
        bundle.putInt(FormFragment.FORM_ID, getIntent().getIntExtra(FormFragment.FORM_ID,-1));

        if(savedInstanceState == null) {
            FormListFragment newFrag = new FormListFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_profile_input_container, newFrag, "FormList");
            transaction.addToBackStack("FormList");
            transaction.commit();
        }

        setContentView(R.layout.activity_profile_input);

        final ActionBar ab = getSupportActionBar();
    }
}*/
