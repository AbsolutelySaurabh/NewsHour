package com.example.absolutelysaurabh.citytourapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container,new HomeFragment());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Home");

        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){

                    case R.id.home:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new HomeFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Home");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.about_us:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new AboutFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("About");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.visit:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new VisitFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Lets Visit");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new SettingsFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Settings ");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.contact:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new ContactFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Contact Us");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.share:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new ShareFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Share");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.rate:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new RateFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Rate Us");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;

                }
                return false;
            }
        });
        }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();


    }
}
