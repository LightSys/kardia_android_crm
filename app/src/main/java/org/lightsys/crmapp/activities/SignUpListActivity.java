package org.lightsys.crmapp.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.LocalDBProvider;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.fragments.SignUpListFragment;
import org.lightsys.crmapp.models.TimelineItem;

/**
 * Created by otter57 on 9/14/17.
 *
 * Creates a signUp list for passing around class rooms to collect student info
 *
 */

public class SignUpListActivity extends AppCompatActivity{

    int formId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        formId = getIntent().getIntExtra(SignUpListFragment.FORM_ID,-1);
        formId = (formId == -1)? getFormId():formId;


        if(savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt(SignUpListFragment.FORM_ID, getFormId());

            SignUpListFragment fragment = new SignUpListFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_profile_input_container, fragment)
                    .commit();
        }

        setContentView(R.layout.activity_profile_input);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_input);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign up sheet");

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private int getFormId(){
        int formId=-1;

        Cursor cursor = getContentResolver().query(
                LocalDBTables.FormTable.CONTENT_URI,
                new String[] {LocalDBTables.FormTable.FORM_ID,},
                null, null, null
        );
        try {
            while (cursor.moveToLast()) {
                formId = cursor.getInt(0);
            }
        }catch (NullPointerException ne) {
            ne.printStackTrace();
        }

        formId+=1;
        return formId;
    }



}
