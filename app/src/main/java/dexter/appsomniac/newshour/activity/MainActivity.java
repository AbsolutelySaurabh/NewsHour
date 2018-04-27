package dexter.appsomniac.newshour.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import dexter.appsomniac.newshour.classfragments.ChannelFragment;
import dexter.appsomniac.newshour.classfragments.HeadlineFragment;
import dexter.appsomniac.newshour.classfragments.TechFragment;
import dexter.appsomniac.newshour.config.Config;
import dexter.appsomniac.newshour.R;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    public static String CURRENT_TAG = TAG_HOME;
    private ViewPager viewPager;
    private DrawerLayout drawer;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        initViews();
        setToolbar();
        setViewPager();

    }

    private void initViews(){

        setToolbar();
        setViewPager();
        setFAB();

        //SharedPrefs used to track the item_index in Bookmarks table in news.db
        SharedPreferences.Editor editor = getSharedPreferences("bookmarksPrefs", MODE_PRIVATE).edit();
        editor.putInt("bookmarks_item_index", 0);
        editor.apply();
    }

    private void setToolbar(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        setDrawer(toolbar);
    }

    private void setDrawer(Toolbar toolbar){
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setViewPager(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
    }

    private void setFAB(){

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), BookmarksActivity.class);
                startActivity(intent);

            }
        });
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new HeadlineFragment(), "HeadLines");
        adapter.addFragment(new ChannelFragment(), "Channels");
        adapter.addFragment(new TechFragment(), "TechNews");
        viewPager.setAdapter(adapter);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_headlines) {
            viewPager.setCurrentItem(0);

        } else
        if (id == R.id.nav_channels) {
            viewPager.setCurrentItem(1);
        }else
        if(id == R.id.nav_techNews){

            viewPager.setCurrentItem(2);

        } else
        if(id ==  R.id.nav_bookmarks){

            Intent intent = new Intent(this, BookmarksActivity.class);
            startActivity(intent);
        }else
        if(id == R.id.nav_legalInfo){

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.legal_info_dialog);
            dialog.show();
            Button close_button = (Button) dialog.findViewById(R.id.info_close);
            close_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });


        }else
        if(id == R.id.nav_share){

            Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "NewsHour: The all in one news app");
            sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, Config.PLAYSTORE_LINK);
            startActivity(Intent.createChooser(sendIntent, "Share App via: "));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        switch(item.getItemId()){

            case R.id.action_share:

                Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "NewsHour: The all in one news app");
                sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, Config.PLAYSTORE_LINK);
                startActivity(Intent.createChooser(sendIntent, "Share App via: "));

        }
        return super.onOptionsItemSelected(item);
    }
}
