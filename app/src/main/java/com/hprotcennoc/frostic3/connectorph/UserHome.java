package com.hprotcennoc.frostic3.connectorph;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hprotcennoc.frostic3.connectorph.fragments.UserDonateFeedFragment;
import com.hprotcennoc.frostic3.connectorph.fragments.UserFindOrphanageFragment;
import com.hprotcennoc.frostic3.connectorph.library.model.NavDrawerItem;
import com.hprotcennoc.frostic3.connectorph.library.model.NavDrawerListAdapter;

import java.util.ArrayList;

public class UserHome extends ActionBarActivity{

    //NAVIGATION DRAWER STARTS HERE
    private static String demail;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    //NAVIGATION DRAWER CONTINUES LATER

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_user);


        //NAVIGATION DRAWER CONTINUES HERE
        demail = getIntent().getStringExtra("email");
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_user);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<>();

        // adding nav drawer items to array
        // Donation Feed
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
        // My Donations
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
        // Orphanages Near You
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
        // Find Orphanages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));


        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //ON DRAWER TOGGLE, DO WHATEVER YOU WISH TO DO, HERE!
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                //getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //END DRAWER TOGGLE

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
            Log.e("UserHOME", "saved instance is NULL");
        }
        else {
            setTitle(navMenuTitles[previousPosition]);
            Log.e("UserHOME", "saved instance is NOT NULL");
        }
        //NAVIGATION DRAWER CONTINUES LATER
    }

    //NAVIGATION DRAWER CONTINUES HERE
    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_logout:
                Intent logout = new Intent(this, MainActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logout);
                break;
            case R.id.action_refresh:
                this.recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /* *
     * Called when  is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_logout).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    static int previousPosition = 0;
    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                previousPosition = position;
                fragment = new UserDonateFeedFragment();
                break;
            case 1:
                Intent UserMyDonationsIntent = new Intent(this, UserMyDonations.class);
                UserMyDonationsIntent.putExtra("email", demail);
                startActivity(UserMyDonationsIntent);
                mDrawerList.setItemChecked(previousPosition, true);
                mDrawerList.setSelection(previousPosition);
                setTitle(navMenuTitles[previousPosition]);
                mDrawerLayout.closeDrawers();
                break;
            case 2:
                Intent OrphanagesNearYouFragmentActivity = new Intent(this, FindNearbyOrphanages.class);
                OrphanagesNearYouFragmentActivity.putExtra("position", position);
                OrphanagesNearYouFragmentActivity.putExtra("email", demail);
                startActivity(OrphanagesNearYouFragmentActivity);
                mDrawerList.setItemChecked(previousPosition, true);
                mDrawerList.setSelection(previousPosition);
                setTitle(navMenuTitles[previousPosition]);
                mDrawerLayout.closeDrawers();
                break;
            case 3:
                previousPosition = position;
                fragment = new UserFindOrphanageFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("UserHome", "Error in creating fragment or Intent Called!");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    //NAVIGATION DRAWER ENDS HERE
    //BACK BUTTON EXITS ON TWO CLICKS STARTS
    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to logout", Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(mRunnable, 2000);
    }
    //BACK BUTTON EXITS ON TWO CLICKS ENDS
}
