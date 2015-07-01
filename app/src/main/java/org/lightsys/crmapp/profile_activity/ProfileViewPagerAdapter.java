package org.lightsys.crmapp.profile_activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.lightsys.crmapp.PeopleTab;
import org.lightsys.crmapp.Tab1;

/**
 * Created by Jake on 6/17/2015.
 */
public class ProfileViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int numOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ProfileViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumOfTabs) {
        super(fm);

        this.Titles = mTitles;
        this.numOfTabs = mNumOfTabs;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            PTab1 tab1 = new PTab1();
            return tab1;
        }
        else if (position == 1)
        {
            PTab2 tab2 = new PTab2();
            return tab2;
        }
        else {
            PTab3 tab3 = new PTab3();
            return tab3;
        }


    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
