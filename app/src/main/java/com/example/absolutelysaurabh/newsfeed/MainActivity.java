package com.example.absolutelysaurabh.newsfeed;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {

    //Constant value for the earthquake loader ID. We can choode any constant integer
    //This really comes into play if we're using multiple loaders
    private static final int NEWS_LOADER_ID = 1;

    //This textView is displyed when the list is empty
    private TextView emptyTextView;

    private static final String LOG_TAG = MainActivity.class.getName();

    //Here we need the URL to extract JSON news
    //Use AllCaps for a constant.
    private static final String USGS_REQUEST_URL = "https://newsapi.org/v1/articles?source=the-hindu&sortBy=latest&apiKey=ead2ef7ff6fd4231a07398f003e24436";
    //Adapter for the list of the earthquakes
    private NewsAdapter newsAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //To support the toolbar;
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);

        //Find the reference to the link LIstView in the layout
        ListView newsListView = (ListView)findViewById(R.id.list);

        //Create a new adapter that tekaes empty list of earthquakes as input
        newsAdapter = new NewsAdapter(this,new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface

        newsListView.setAdapter(newsAdapter);


        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                News currentNews = newsAdapter.getItem(position);

                Intent i = new Intent(getApplicationContext(),WebviewActivity.class);
                i.putExtra("articleUrl",currentNews.getUrl());
                startActivity(i);
            }
        });


        //Get a reference to the LoaderManager , in order to interact with Loaders
        android.app.LoaderManager loaderManager = getLoaderManager();

        //Initialize the loader. Pass in the int ID constant defined above and pass in null for
        //the bundle . Pass this activity for the LoaderCallBacks parameter (which is valid because
        //this activity implements the LoaderCallbacks interface;
        loaderManager.initLoader(NEWS_LOADER_ID,null,this);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        // Create a new loader for the given URL
        return new NewsLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.main, menu);

        //sharing intent
//        MenuItem shareItem =  menu.findItem(R.id.share);
//
//        ActionProvider mShare =  MenuItemCompat.getActionProvider(shareItem);
//
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.setType("text/plain");
//        shareIntent.putExtra(Intent.EXTRA_TEXT,"text to share");

        //mShare.setShareIntent(shareIntent);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.shareit:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Take a look at my android app";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "NewsFeed app");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share App Via"));

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<News>> loader, List<News> earthquakes) {
        // Hide loading indicator because the data has been loaded
//        View loadingIndicator = findViewById(R.id.loading_indicator);
//        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText("No Internet Connection");

        // Clear the adapter of previous earthquake data
        newsAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            newsAdapter.addAll(earthquakes);
        }
    }
    @Override
    public void onLoaderReset(android.content.Loader<List<News>> loader) {

        // Loader reset, so we can clear out our existing data.
        newsAdapter.clear();
    }
}