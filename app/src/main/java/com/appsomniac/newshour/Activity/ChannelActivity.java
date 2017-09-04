package com.appsomniac.newshour.Activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.appsomniac.newshour.Adapter.ChannelActivityContentAdapter;
import com.appsomniac.newshour.data.NewsDbHelper;
import com.appsomniac.newshour.Config.Config;
import com.appsomniac.newshour.Model.News;
import com.appsomniac.newshour.R;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class ChannelActivity extends AppCompatActivity {

    public static String channels[] = {"Guardian","TOI", "ESPN", "TechCrunch", "MTVnews", "HackerNews", "TheHindu",
     "TechRadar", "CNN", "Time", "Mashable", "FoxSports", "Buzz-Feed", "National Geographic", "Business Insider", "Recode"};

    private NewsDbHelper bookmarksDbHelper;
    private NewsDbHelper simpleNewsDbHelper;
    public static ArrayList<News> al_news;

    public static final String EXTRA_POSITION = "position";
    public static String NEWS_URL = "";
    ChannelActivityContentAdapter adapter;
    RecyclerView recyclerView;
    View listItemView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        al_news = new ArrayList<>();

        bookmarksDbHelper = new NewsDbHelper(getApplicationContext());
        simpleNewsDbHelper = new NewsDbHelper(getApplicationContext());

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);

        int position = getIntent().getIntExtra("position", 0);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ImageView newsImage = (ImageView) findViewById(R.id.image);

        if(position == 0){

            NEWS_URL = Config.THEGUARDIAN_URL + Config.API_KEY;
            collapsingToolbar.setTitle("The Guardian");
//            Picasso.with(this).load(R.drawable.guardian_2).error(R.drawable.icon_5).into(newsImage);

            Glide.with(this).load(R.drawable.guardian_2).thumbnail(0.5f).into(newsImage);


        }else
            if(position == 1){

                NEWS_URL = Config.TOI_URL + Config.API_KEY;
                collapsingToolbar.setTitle("The Times of India");
//                Picasso.with(this).load(R.drawable.toi_collasping).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.toi_collasping).thumbnail(0.5f).into(newsImage);

            }else
            if(position == 2){

                NEWS_URL = Config.ESPN_URL + Config.API_KEY;
                collapsingToolbar.setTitle("ESPN");
//                Picasso.with(this).load(R.drawable.espn).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.espn).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 3){

                NEWS_URL = Config.TECHCRUNCH_URL + Config.API_KEY;
                collapsingToolbar.setTitle("TechCrunch");
//                Picasso.with(this).load(R.drawable.tcrunch).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.tcrunch).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 4){

                NEWS_URL = Config.MTVNEWS_URL + Config.API_KEY;
                collapsingToolbar.setTitle("MTV News");
//                Picasso.with(this).load(R.drawable.mtv_news).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.mtv_news).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 5){


                NEWS_URL = Config.RECODE_URL + Config.API_KEY;
                collapsingToolbar.setTitle("Recode");
//                Picasso.with(this).load(R.drawable.recode_2).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.recode_2).thumbnail(0.5f).into(newsImage);

            }else
            if(position == 6){

                NEWS_URL = Config.HINDU_URL + Config.API_KEY;
                collapsingToolbar.setTitle("The Hindu");
//                Picasso.with(this).load(R.drawable.hindu).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.hindu).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 7){

                NEWS_URL = Config.TECHRADAR_URL + Config.API_KEY;
                collapsingToolbar.setTitle("TechRadar");
//                Picasso.with(this).load(R.drawable.texh_radar_collasp).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.texh_radar_collasp).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 8){

                NEWS_URL = Config.CNN_URL + Config.API_KEY;
                collapsingToolbar.setTitle("CNN");
//                Picasso.with(this).load(R.drawable.cnn).error(R.drawable.icon_5).into(newsImage);

                Glide.with(this).load(R.drawable.cnn).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 9){

                NEWS_URL = Config.FOXSPORTS_URL + Config.API_KEY;
                collapsingToolbar.setTitle("Fox Sports");
//                Picasso.with(this).load(R.drawable.foxsports).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.foxsports).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 10){

                NEWS_URL = Config.BUZZ_FEEd_URL + Config.API_KEY;
                collapsingToolbar.setTitle("Buzz-Feed");
//                Picasso.with(this).load(R.drawable.buzz_feed_cpllasp).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.buzz_feed_cpllasp).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 11){

                NEWS_URL = Config.TIME_URL + Config.API_KEY;
                collapsingToolbar.setTitle("Time");
//                Picasso.with(this).load(R.drawable.time).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.time).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 12){

                NEWS_URL = Config.MASHABLE_URL + Config.API_KEY;
                collapsingToolbar.setTitle("Mashable");
//                Picasso.with(this).load(R.drawable.mashable_collasp).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.mashable_collasp).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 13){

                NEWS_URL = Config.NAT_GEO_URL + Config.API_KEY;
                collapsingToolbar.setTitle("National Geographic");

//                Picasso.with(this).load(R.drawable.nat_geo_collasp).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.nat_geo_collasp).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 14){

                NEWS_URL = Config.HACKERNEWS_URL + Config.API_KEY;
                collapsingToolbar.setTitle("HackerNews");
//                Picasso.with(this).load(R.drawable.hacker_news_collasping).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.hacker_news_collasping).thumbnail(0.5f).into(newsImage);


            }else
            if(position == 15){

                NEWS_URL = Config.BUSINESS_INSIDER_URL + Config.API_KEY;
                collapsingToolbar.setTitle("Business Insider");
//                Picasso.with(this).load(R.drawable.business_inside_2).error(R.drawable.icon_5).into(newsImage);
                Glide.with(this).load(R.drawable.business_inside_2).thumbnail(0.5f).into(newsImage);
            }

        Log.e("NEWS_URL: ", NEWS_URL);
        getChannelWiseNews(NEWS_URL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void getChannelWiseNews(String NEWS_REQUEST_URL){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, NEWS_REQUEST_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseObject) {
                try {

                    JSONArray responseArray = responseObject.getJSONArray("articles");
                    for(int i=0;i<responseArray.length();i++){

                        JSONObject currentNewsObject = responseArray.getJSONObject(i);

                        String title = currentNewsObject.getString("title");
                        String description = currentNewsObject.getString("description");
                        String url = currentNewsObject.getString("url");
                        String urlToImage = currentNewsObject.getString("urlToImage");
                        String publishedAt = currentNewsObject.getString("publishedAt");

                        News currentNews = new News(title, description, publishedAt, url, urlToImage);
                        al_news.add(currentNews);

                        Log.d("QUESTIONS: "+ String.valueOf(i), title);
                    }

                    adapter = new ChannelActivityContentAdapter(recyclerView.getContext(), al_news);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e){
                    Log.e("QueryUtils", "Problem parsing the News JSON results", e);
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){

        finish();
        return true;
    }

}
