package com.example.absolutelysaurabh.newsfeed;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.absolutelysaurabh.newsfeed.Json.NewsLoader;
import com.example.absolutelysaurabh.newsfeed.News.News;
import com.example.absolutelysaurabh.newsfeed.News.NewsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {

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

        ListView newsListView = (ListView)findViewById(R.id.list);

        newsAdapter = new NewsAdapter(this,new ArrayList<News>());

        newsListView.setAdapter(newsAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                News currentNews = newsAdapter.getItem(position);

                Intent i = new Intent(getApplicationContext(),WebviewActivity.class);
                i.putExtra("articleUrl",currentNews.getUrl());
                startActivity(i);
            }
        });


        //Get a reference to the LoaderManager , in order to interact with Loaders
        android.app.LoaderManager loaderManager = getLoaderManager();

        loaderManager.initLoader(NEWS_LOADER_ID,null,this);

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        // Create a new loader for the given URL
        return new NewsLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

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

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        newsAdapter.clear();

        if (earthquakes != null && !earthquakes.isEmpty()) {
            newsAdapter.addAll(earthquakes);
        }else{

            mEmptyStateTextView.setText("Error Occured!");
        }
    }
    @Override
    public void onLoaderReset(android.content.Loader<List<News>> loader) {

        // Loader reset, so we can clear out our existing data.
        newsAdapter.clear();
    }
}
