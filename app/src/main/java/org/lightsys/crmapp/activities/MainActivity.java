package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.lightsys.crmapp.fragments.MainFragment;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.CRMContract;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    private AccountManager mAccountManager;

    /**
     * Retrieves account information.
     * Sets up main activity view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Main Activity", "Created");

        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(CRMContract.accountType);
        if(accounts.length == 0) {
            mAccountManager.addAccount(CRMContract.accountType, null, null, null, this, null, null);
            finish();
        }

        setContentView(R.layout.activity_main);

        setupNavigationView();
        setupToolbar();
        setupFAB();
    }

    /**
     * Builds options menu
     * Sets up "search" function
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Gets text to search for.
        final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Runs when a search is submitted.
            @Override
            public boolean onQueryTextSubmit(String s) {

                Snackbar.make(findViewById(R.id.coordinatorlayout_main),
                        "TODO execute query and repopulate",
                        Snackbar.LENGTH_LONG).show();
                return false;
            }

            /**
             * Runs a search every time the search input is changed.
             */
            @Override
            public boolean onQueryTextChange(String s) {
                MainFragment mainFrag = (MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_profiles);

                if (mainFrag != null && mainFrag.isVisible()) {
                    mainFrag.search(s);
                }

                return true;
            }
        });
        return true;
    }

    /*
     * Provides option to select something within the Menu.
     */
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

    /**
     * Creates a toolbar.
     */
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
    public boolean onNavigationItemSelected(MenuItem item)
    {
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
        }

        return true;
    }

    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewProfileActivity.class);
                startActivity(i);
            }
        });
    }
}
