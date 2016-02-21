package samurai.geeft.android.geeft.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import samurai.geeft.android.geeft.fragments.TabGeeftoryFragment;
import samurai.geeft.android.geeft.fragments.TabGeeftFragment;

/**
 * Created by ugookeadu on 31/01/16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence mTitles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int mNumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence titles[], int numbOfTabsumb) {
        super(fm);
        this.mTitles = titles;
        this.mNumbOfTabs = numbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            return TabGeeftoryFragment.newInstance(new Bundle());
        }
        else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            return TabGeeftFragment.newInstance(new Bundle());
        }


    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return mNumbOfTabs;
    }
}