package com.example.absolutelysaurabh.newshour.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.absolutelysaurabh.newshour.ClassFragments.ChannelFragment;
import com.example.absolutelysaurabh.newshour.ClassFragments.HeadlineFragment;
import com.example.absolutelysaurabh.newshour.ClassFragments.TechFragment;
import com.example.absolutelysaurabh.newshour.R;
import com.example.absolutelysaurabh.newshour.Fragment.SettingsFragment;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_BOOKMARKS = "bookmarks";
    private static final String TAG_CONTACTUS = "contactUs";
    private static final String TAG_SETTINGS = "settings";

    public static String CURRENT_TAG = TAG_HOME;

    private Handler mHandler;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // toolbar titles respected to selected nav menu item
//    private String[] activityTitles = {"Now Playing","Popular Movies" ,"Top Rated","Upcoming Movies","Favourite Movies","About Us", "Settings"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Create Navigation drawer and inlfate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null){

            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(),R.color.white,getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);

                        // TODO: handle navigation

                        //Check to see which item was being clicked and perform appropriate action
                        switch (menuItem.getItemId()) {
                            //Replacing the main content with ContentFragment Which is our Inbox View;
                            case R.id.nav_home:
                                navItemIndex = 0;
                                CURRENT_TAG = TAG_HOME;
                                break;
                            case R.id.nav_bookmarks:
                                navItemIndex = 1;
                                CURRENT_TAG = TAG_BOOKMARKS;
                                break;
                            case R.id.nav_contact:
                                navItemIndex = 2;
                                CURRENT_TAG = TAG_CONTACTUS;
                                break;
                            case R.id.nav_settings:
                                navItemIndex = 3;
                                CURRENT_TAG = TAG_SETTINGS;
                                break;

                            default:
                                navItemIndex = 0;
                                // Closing drawer on item click
                                CURRENT_TAG = TAG_HOME;
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });


        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }


        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent intent = new Intent(getApplicationContext(), BookmarksActivity.class);
                    startActivity(intent);


            }
        });
    }

    private void loadHomeFragment() {

            // if user select the current navigation menu again, don't do anything
            // just close the navigation drawer
            if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {

//                drawer.closeDrawers();
                return;
            }
            // Sometimes, when fragment has huge data, screen seems hanging
            // when switching between navigation menus
            // So using runnable, the fragment is loaded with cross fade effect
            // This effect can be seen in GMail app
            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {

                    // update the main content by replacing fragments
                    for(int i=0;i<getFragmentManager().getBackStackEntryCount();i++){
                        //delete the fragments in the backstack
                        getSupportFragmentManager().popBackStack();
                    }
                    Fragment fragment = getHomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
//                    fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };
            // If mPendingRunnable is not null, then add to the message queue
            if (mPendingRunnable != null) {
                mHandler.post(mPendingRunnable);
            }
            //Closing drawer on item click
           // drawer.closeDrawers();
            // refresh toolbar menu
            invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {

        switch (navItemIndex) {
            case 0:
                // HomeFragment displays the "Now Playing" movies.
                HeadlineFragment homeFragment = new HeadlineFragment();
                return homeFragment;
            case 1:
                // popular movies fragment
                HeadlineFragment popularMoviesFragment = new HeadlineFragment();
                return popularMoviesFragment;
            case 2:
                // Top Rated movies fragment
                SettingsFragment topRatedMovies = new SettingsFragment();
                return topRatedMovies;

            default:
                return new HeadlineFragment();
        }
    }




    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new HeadlineFragment(), "HeadLines");
        adapter.addFragment(new ChannelFragment(), "Channels");
        adapter.addFragment(new TechFragment(), "TechNews");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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

        switch(item.getItemId()){

            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
