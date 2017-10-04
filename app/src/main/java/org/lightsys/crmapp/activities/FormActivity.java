package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.fragments.FormFragment;
import org.lightsys.crmapp.fragments.FormListFragment;

import static org.lightsys.crmapp.fragments.FormFragment.FORM_ID;


/**
 * Created by otter57 on 9/20/17.
 */

public class FormActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    int formId = -1;
    Fragment currentFrag;
    private AccountManager mAccountManager;
    ActionBar ab;
    private Boolean locked;
    public static final String LOG_TAG = FormActivity.class.getName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locked = false;

        mAccountManager = AccountManager.get(this);


        if (savedInstanceState == null) {

            FormListFragment newFrag = new FormListFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_profile_input_container, newFrag, "FormList");
            transaction.addToBackStack("FormList");
            transaction.commit();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_profile_input);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_input);
        setSupportActionBar(toolbar);
        setupNavigationView();

        currentFrag = getFragmentManager().findFragmentByTag("FORM");

        ab = getSupportActionBar();
    }

    public void setLocked(boolean locked){
        this.locked = locked;
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
                Account[] accounts = mAccountManager.getAccountsByType(LocalDBTables.accountType);
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
                mAccountManager.addAccount(LocalDBTables.accountType, null, null, null, this, null, null);
                finish();
                return true;
            case R.id.action_collaborators:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
        }

        return true;
    }
    /*@Override
    public void onBackPressed(){
        if(locked) {

            this.setLocked(false);

            FormFragment newFrag = new FormFragment();

            Bundle args = new Bundle();
            args.putInt(FORM_ID, formId);

            newFrag.setArguments(args);

            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_profile_input_container, newFrag, "Form");
            transaction.addToBackStack("Form");
            transaction.commit();
            Toast.makeText(this, "\"Complete Form\" must be used to exit", Toast.LENGTH_SHORT).show();
        }

        super.onBackPressed();
    }*/
}
