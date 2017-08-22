package com.example.absolutelysaurabh.newshour.Activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.absolutelysaurabh.newshour.ClassFragments.HeadlineFragment;
import com.example.absolutelysaurabh.newshour.ClassFragments.TechFragment;
import com.example.absolutelysaurabh.newshour.R;
import com.example.absolutelysaurabh.newshour.ViewHolder.HeadLineFragViewHolder;
import com.squareup.picasso.Picasso;

/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        Bundle bundle = getIntent().getBundleExtra("bundle");

        final int position = bundle.getInt(EXTRA_POSITION);
        final int tab_opened = bundle.getInt("tab");

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());

        final Resources resources = getResources();

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        final TextView newsDescription = (TextView) findViewById(R.id.place_detail);
        ImageView newsImage = (ImageView) findViewById(R.id.image);

        if(tab_opened == 1){

            myWebView.loadUrl(HeadlineFragment.al_news.get(position).getUrl());

            collapsingToolbar.setTitle(HeadlineFragment.al_news.get(position).getTitle());
            newsDescription.setText(HeadlineFragment.al_news.get(position).getDescription());
            Picasso.with(this).load(HeadlineFragment.al_news.get(position).getUrlToImage())
                    .error(R.drawable.guardian).into(newsImage);

        }else
        if(tab_opened == 2){

            myWebView.loadUrl(ChannelActivity.al_news.get(position).getUrl());

            collapsingToolbar.setTitle(ChannelActivity.al_news.get(position).getTitle());
            newsDescription.setText(ChannelActivity.al_news.get(position).getDescription());
            Picasso.with(this).load(ChannelActivity.al_news.get(position).getUrlToImage())
                    .error(R.drawable.guardian).into(newsImage);

        }else
            if(tab_opened == 3){

                myWebView.loadUrl(TechFragment.al_news.get(position).getUrl());

                collapsingToolbar.setTitle(TechFragment.al_news.get(position).getTitle());
                newsDescription.setText(TechFragment.al_news.get(position).getDescription());
                Picasso.with(this).load(TechFragment.al_news.get(position).getUrlToImage())
                        .error(R.drawable.guardian).into(newsImage);

            }else
                if(tab_opened==4){

                    myWebView.loadUrl(BookmarksActivity.al_news_url.get(position));

                    collapsingToolbar.setTitle(BookmarksActivity.al_news_title.get(position));
                    newsDescription.setText(BookmarksActivity.al_news_desc.get(position));
                    Picasso.with(this).load(BookmarksActivity.al_news_urlToImage.get(position)).
                            error(R.drawable.guardian).into(newsImage);
                }
    }
    @Override
    public boolean onSupportNavigateUp(){

        finish();
        return true;
    }
}