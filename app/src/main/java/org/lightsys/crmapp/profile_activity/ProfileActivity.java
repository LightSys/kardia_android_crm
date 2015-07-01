package org.lightsys.crmapp.profile_activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.ViewPagerAdapter;


public class ProfileActivity extends ActionBarActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    FloatingActionButton fab;
    TabLayout tabLayout;
    ViewPager viewPager;
    ProfileViewPagerAdapter viewPagerAdapter;
    CharSequence Titles[]={"Profile", "Contact Information", "Other"};
    int NumOfTabs = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);





        setupToolbar();
        setupTablayout();

    }



    private void setupTablayout(){

        viewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), Titles,NumOfTabs);
        viewPager = (ViewPager) findViewById(R.id.profilePager);
        viewPager.setAdapter(viewPagerAdapter);


        tabLayout = (TabLayout) findViewById(R.id.profileTabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.setTabTextColors(getResources().getColorStateList(R.color.selector));
    }

    private void setupToolbar() {
        Intent intent = this.getIntent();
        toolbar = (Toolbar) findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        setTitle(intent.getStringExtra("person"));

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
