package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.PostJson;
import org.lightsys.crmapp.fragments.CollaboratorFragment;
import org.lightsys.crmapp.fragments.EngagementFragment;
import org.lightsys.crmapp.fragments.FormListFragment;
import org.lightsys.crmapp.models.Partner;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.lightsys.crmapp.activities.ProfileActivity.PARTNER_ID_KEY;
import static org.lightsys.crmapp.activities.ProfileActivity.saveImageFromUrl;
import static org.lightsys.crmapp.data.LocalDBTables.CollaborateeTable.PARTNER_NAME;
import static org.lightsys.crmapp.data.LocalDBTables.CollaborateeTable.PROFILE_PICTURE;

/**
 * Edited by Daniel Garcia on 30/June/2017
 * to merge unnecessary Fragment into MainActivity
 */

//todo check that engagment activity functionality was not lost changing to fragment
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private AccountManager mAccountManager;
    final static public String ACCOUNT_ID = "account_id";
    final static public String SEARCH_QUERY = "search_query";



    private RecyclerView mRecyclerView;
    private List<Partner> mProfiles = new ArrayList<>();
    private Account mAccount;

    Partner mPartner2 = new Partner();
    private NavigationView navigationView;
    private SearchView searchView;
    private MaterialDialog materialDialog;
    private String partnerId;
    private static String TAG = "Main Activity";
    private LinearLayoutManager linearLayoutManager;

    /**
     * Retrieves account information.
     * Sets up main activity view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Created");
        setContentView(R.layout.activity_main);
        setupNavigationView();
        setupToolbar();

        FragmentManager fragmentManager = getSupportFragmentManager();
        CollaboratorFragment fragment = new CollaboratorFragment();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment, "Collaborator")
                .addToBackStack("Collaborator").commit();

    }

    /**
     * Builds options menu
     * Sets up "search" function
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals(""))
                    return true;

                searchView.clearFocus();
                Fragment frag = getSupportFragmentManager().findFragmentByTag("Collaborator");
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
        return true;
    }

    public void showNavButton(boolean bool) {
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(bool);

    }

    /**
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
        navigationView = (NavigationView) findViewById(R.id.mainNavigation);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.action_collaborators);
        navigationView.getMenu().findItem(R.id.action_collaborators).setCheckable(true);
        navigationView.getMenu().findItem(R.id.action_collaborators).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Intent intent;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment;
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(Gravity.START);

        switch (itemId)
        {
            case R.id.action_collaborators:
                fragment = new CollaboratorFragment();
                fragmentManager.beginTransaction().replace(R.id.content_main, fragment, "Collaborator")
                        .addToBackStack("Collaborator").commit();
                break;
            case R.id.action_logout:
                Account[] accounts = mAccountManager.getAccountsByType(LocalDBTables.accountType);
                Log.d(TAG, "# of Accounts: " + accounts.length);
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
                break;
            case R.id.action_engagement:
                fragment = new EngagementFragment();

                fragmentManager.beginTransaction().replace(R.id.content_main, fragment, "Engagements")
                        .addToBackStack("Engagements").commit();
                break;
            case R.id.action_sign_up:
                fragment = new FormListFragment();

                fragmentManager.beginTransaction().replace(R.id.content_main, fragment, "FormList")
                        .addToBackStack("FormList").commit();
                break;
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(ACCOUNT_ID, partnerId);
    }
}
