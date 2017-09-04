package com.appsomniac.newshour.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appsomniac.newshour.ClassFragments.HeadlineFragment;
import com.appsomniac.newshour.ClassFragments.TechFragment;
import com.appsomniac.newshour.Model.News;
import com.appsomniac.newshour.R;
import com.appsomniac.newshour.ViewHolder.HeadLineFragViewHolder;
import com.appsomniac.newshour.data.NewsDbHelper;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    private NewsDbHelper bookmarksDbHelper;
    private ArrayList<News> al_news;
    public static FloatingActionButton favoriteImageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        Bundle bundle = getIntent().getBundleExtra("bundle");

        bookmarksDbHelper = new NewsDbHelper(this);

        favoriteImageButton = (FloatingActionButton) findViewById(R.id.fab);

        final int position = bundle.getInt(EXTRA_POSITION);
        final int tab_opened = bundle.getInt("tab");

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(false);
        myWebView.setWebViewClient(new WebViewClient());


        final Resources resources = getResources();

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        final TextView newsDescription = (TextView) findViewById(R.id.place_detail);
        ImageView newsImage = (ImageView) findViewById(R.id.image);
        final TextView news_title = (TextView) findViewById(R.id.news_titile);

        if(tab_opened == 1){

            myWebView.loadUrl(HeadlineFragment.al_news.get(position).getUrl());

            news_title.setText(HeadlineFragment.al_news.get(position).getTitle());
            collapsingToolbar.setTitle(HeadlineFragment.al_news.get(position).getTitle());
            newsDescription.setText(HeadlineFragment.al_news.get(position).getDescription());

            Glide.with(this).load(HeadlineFragment.al_news.get(position).getUrlToImage()).thumbnail(0.5f).into(newsImage);


            favoriteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SQLiteDatabase db = bookmarksDbHelper.getReadableDatabase();

                    addNewsToBookmark(bookmarksDbHelper, db, HeadlineFragment.al_news, position,
                            getApplicationContext(), v);
                }
            });


        }else
        if(tab_opened == 2){

            myWebView.loadUrl(ChannelActivity.al_news.get(position).getUrl());
            news_title.setText(ChannelActivity.al_news.get(position).getTitle());

            collapsingToolbar.setTitle(ChannelActivity.al_news.get(position).getTitle());
            newsDescription.setText(ChannelActivity.al_news.get(position).getDescription());

            Glide.with(this).load(ChannelActivity.al_news.get(position).getUrlToImage()).thumbnail(0.5f).into(newsImage);


            favoriteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SQLiteDatabase db = bookmarksDbHelper.getReadableDatabase();

                    addNewsToBookmark(bookmarksDbHelper, db, ChannelActivity.al_news, position,
                            getApplicationContext(), v);
                }
            });


        }else
            if(tab_opened == 3){

                myWebView.loadUrl(TechFragment.al_news.get(position).getUrl());
                news_title.setText(TechFragment.al_news.get(position).getTitle());

                collapsingToolbar.setTitle(TechFragment.al_news.get(position).getTitle());
                newsDescription.setText(TechFragment.al_news.get(position).getDescription());

                Glide.with(this).load(TechFragment.al_news.get(position).getUrlToImage()).thumbnail(0.5f).into(newsImage);


                favoriteImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SQLiteDatabase db = bookmarksDbHelper.getReadableDatabase();

                        addNewsToBookmark(bookmarksDbHelper, db, TechFragment.al_news, position,
                                getApplicationContext(), v);
                    }
                });


            }else
                if(tab_opened==4){

                    myWebView.loadUrl(BookmarksActivity.al_news.get(position).getUrl());
                    news_title.setText(BookmarksActivity.al_news.get(position).getTitle());

                    collapsingToolbar.setTitle(BookmarksActivity.al_news.get(position).getTitle());
                    newsDescription.setText(BookmarksActivity.al_news.get(position).getDescription());

                    Glide.with(this).load(BookmarksActivity.al_news.get(position).getUrlToImage()).thumbnail(0.5f).into(newsImage);


                    favoriteImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            SQLiteDatabase db = bookmarksDbHelper.getReadableDatabase();

                            addNewsToBookmark(bookmarksDbHelper, db, BookmarksActivity.al_news, position,
                                    getApplicationContext(), v);
                        }
                    });

                }
    }

    public static void addNewsToBookmark(NewsDbHelper bookmarksDbHelper, SQLiteDatabase db,
                                         ArrayList<News> al_news, int position, Context context, View v){

        bookmarksDbHelper = new NewsDbHelper(context);

        Cursor rs = db.rawQuery("SELECT * FROM bookmarks",null);
        int flag=0;
        boolean isInserted = false;
        if(rs.moveToFirst()){

            while(!rs.isAfterLast()){

                String title_in_db = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_TITLE));

                if(title_in_db.equals(al_news.get(position).getTitle())){

                    flag=1;
                }
                rs.moveToNext();
            }
        }
        if(flag==0){

            SharedPreferences prefs = context.getSharedPreferences("bookmarksPrefs", MODE_PRIVATE);
            int bookmarks_index = prefs.getInt("bookmarks_item_index", 0);

            isInserted =  bookmarksDbHelper.insertNewsBookmark(al_news.get(position).getTitle()
                    , al_news.get(position).getDescription()
                    , al_news.get(position).getUrl()
                    , al_news.get(position).getUrlToImage()
                    , al_news.get(position).getPublishedAt()
                    , bookmarks_index );

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("bookmarks_item_index", bookmarks_index+1);
            editor.commit();


        }

        if(isInserted){

                favoriteImageButton.setClickable(false);
                favoriteImageButton.setColorFilter(Color.rgb(255,255,255));
                Snackbar.make(v, "Added to Bookmarks. ", Snackbar.LENGTH_SHORT).show();

        }else {

            if(flag==1){

                favoriteImageButton.setClickable(false);
                favoriteImageButton.setColorFilter(Color.rgb(255,255,255));
                Snackbar.make(v, "Already added! ", Snackbar.LENGTH_SHORT).show();
            }else {

                Snackbar.make(v, "Adding bookmark failed!! ", Snackbar.LENGTH_SHORT).show();
            }
        }

        rs.close();
        db.close();
    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}