package com.example.absolutelysaurabh.newsfeed;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
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
    private static final String USGS_REQUEST_URL = "PASTE YOUR API KEY HERE";
    //Adapter for the list of the earthquakes
    private NewsAdapter newsAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the reference to the link LIstView in the layout
        ListView newsListView = (ListView)findViewById(R.id.list);

        //Create a new adapter that tekaes empty list of earthquakes as input
        newsAdapter = new NewsAdapter(this,new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface

        newsListView.setAdapter(newsAdapter);


        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                News currentNews = newsAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                //Then the android system will automatically recognize which browser to opem.
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
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
