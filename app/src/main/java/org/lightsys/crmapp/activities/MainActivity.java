package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.fragments.CollaboratorFragment;
import org.lightsys.crmapp.fragments.EngagementFragment;
import org.lightsys.crmapp.fragments.FormListFragment;

/**
 * Edited by Daniel Garcia on 30/June/2017
 * to merge unnecessary Fragment into MainActivity
 *
 * Edited by Alex Fehr on 6/1/2022
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private AccountManager mAccountManager; // Global for potential future use
    private final static String ACCOUNT_ID = "account_id";
    final static public String SEARCH_QUERY = "search_query";

    private SearchView searchView;
    private MenuItem closeButton, searchButton;
    private String partnerId; // Global for potential future use
    private static final String TAG = "Main Activity";

    /**
     * Retrieves account information.
     * Sets up main activity view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Created");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);
        setupNavigationView();
        setupToolbar();

        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(LocalDBTables.accountType);
        if(accounts.length == 0) {
            mAccountManager.addAccount(LocalDBTables.accountType, null,
                    null, null, this, null, null);
            finish();
        }else {
            //open Collaborator fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            CollaboratorFragment fragment = new CollaboratorFragment();
            fragmentManager.beginTransaction().replace(R.id.content_main, fragment,
                            "Collaborator").addToBackStack("Collaborator").commit();
        }


        //todo remove follow 2 lines
        getContentResolver().delete(LocalDBTables.FormTable.CONTENT_URI,null,null);
        getContentResolver().delete(LocalDBTables.ConnectionTable.CONTENT_URI,null,null);
    }

    /**
     * Builds options menu
     * Sets up "search" function
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchButton = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchButton.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals(""))
                    return true;

                searchView.clearFocus();
                getSupportFragmentManager().findFragmentByTag("Collaborator");
                Fragment newFrag = new CollaboratorFragment();
                Bundle args = new Bundle();
                args.putString(SEARCH_QUERY, query);
                newFrag.setArguments(args);

                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, newFrag, "Collaborator")
                        .addToBackStack("Collaborator").commit();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        closeButton = menu.findItem(R.id.action_close);
        closeButton.setOnMenuItemClickListener(item -> {
            getSupportFragmentManager().popBackStackImmediate();
            return false;
        });

        closeButton.setVisible(false);

        return true;
    }

    /*
     * change options menu from search icon to close icon
     */
    public void changeOptionsMenu(boolean search, boolean close){
        searchButton.setVisible(search);
        closeButton.setVisible(close);
    }

    public void showNavButton(boolean bool) {
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(bool);

    }

    /**
     * Provides option to select something within the Menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        int id = item.getItemId();

        // Left as switch statement to allow more options to be added later
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
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationView(){
        NavigationView navigationView = findViewById(R.id.mainNavigation);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.action_collaborators);
        navigationView.getMenu().findItem(R.id.action_collaborators).setCheckable(true);
        navigationView.getMenu().findItem(R.id.action_collaborators).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment;
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        if (itemId == R.id.action_collaborators) {
            fragment = new CollaboratorFragment();
            fragmentManager.beginTransaction().replace(R.id.content_main, fragment,
                    "Collaborator").addToBackStack("Collaborator").commit();
        }

        else if (itemId == R.id.action_logout) {
            loginActivity(); // Move back to LoginActivity
        }

        else if (itemId == R.id.action_engagement) {
            fragment = new EngagementFragment();

            fragmentManager.beginTransaction().replace(R.id.content_main, fragment,
                    "Engagements").addToBackStack("Engagements").commit();
        }

        else if (itemId == R.id.action_sign_up) {
            fragment = new FormListFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment,
                    "FormList").addToBackStack("FormList").commit();
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(ACCOUNT_ID, partnerId);
    }

    /**
     * Moves application back to the LoginActivity
     */
    private void loginActivity() {
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
        finish();
    }
}
