package org.lightsys.crmapp;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.lightsys.crmapp.data.Account;
import org.lightsys.crmapp.data.DataConnection;
import org.lightsys.crmapp.data.LocalDatabaseHelper;
import org.lightsys.crmapp.data.PullType;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    DrawerLayout drawerLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    FloatingActionButton fab;
    TabLayout tabLayout;

    private static Account loggedInAccount;

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    CharSequence Titles[]={"Search", "My People"};
    int NumOfTabs = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigationView();
        setupToolbar();

        ArrayList<Account> accounts = new ArrayList<>();
        LocalDatabaseHelper db = new LocalDatabaseHelper(this);
        accounts = db.getAccounts();
        db.close();

        if (accounts.size() == 0) {
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
        } else {
            for (Account account : accounts){
                this.setLoggedInAccount(account);
                (new DataConnection(this, getLoggedInAccount(), PullType.GetPartners)).execute("");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            //case R.id.action_search:
                //return true;
        }

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationView(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    public static void setLoggedInAccount(Account a) {
        loggedInAccount = a;
    }
    public static Account getLoggedInAccount() {
        return loggedInAccount;
    }
}
