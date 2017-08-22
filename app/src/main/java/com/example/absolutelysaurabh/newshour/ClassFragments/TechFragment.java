package com.example.absolutelysaurabh.newshour.ClassFragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.absolutelysaurabh.newshour.Activity.DetailsActivity;
import com.example.absolutelysaurabh.newshour.Activity.WebViewActivity;
import com.example.absolutelysaurabh.newshour.Adapter.TechFragContentAdapter;
import com.example.absolutelysaurabh.newshour.BookMarks.NewsDbHelper;
import com.example.absolutelysaurabh.newshour.Config.Config;
import com.example.absolutelysaurabh.newshour.Model.News;
import com.example.absolutelysaurabh.newshour.R;
import com.example.absolutelysaurabh.newshour.Util.ConnectionDetector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/**
 * Created by absolutelysaurabh on 6/8/17.
 */
public class TechFragment extends Fragment{

    public static String GET_NEWS_URL_TechCrunch = "", GET_NEWS_URL_HackerNews = "", GET_NEWS_URL_TechRadar = "";

    private NewsDbHelper techDbHelper, bookmarksDbHelper;
    public static ArrayList<News> al_news;

    public static int index = 0, isConnected=0, bookmark_table_item_index = 0, techNews_table_item_index = 0;
    TechFragContentAdapter adapter;
    RecyclerView recyclerView;
    View listItemView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GET_NEWS_URL_TechCrunch = Config.TECHCRUNCH_URL + Config.API_KEY;
        GET_NEWS_URL_TechRadar = Config.TECHRADAR_URL + Config.API_KEY ;
        GET_NEWS_URL_HackerNews= Config.HACKERNEWS_URL + Config.API_KEY;
        Log.e("GET NEWS URL: ", GET_NEWS_URL_TechCrunch);

        al_news = new ArrayList<>();

        bookmarksDbHelper = new NewsDbHelper(getContext());
        techDbHelper = new NewsDbHelper(getContext());

        bookmark_table_item_index = (new NewsDbHelper(getContext())).numberOfRowsInBookmarks();
        Log.e("bookmark table index: ", String.valueOf(bookmark_table_item_index));

        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);

        listItemView = inflater.inflate(R.layout.item_tech, container, false);
        View loadingIndicator = listItemView.findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        if((new ConnectionDetector(getContext())).isConnectingToInternet()) {

            techDbHelper.deleteAllRecordTech();

            Log.e("Connected to internet: ", "..");
            getTechNewsJSON(GET_NEWS_URL_TechCrunch);
            getTechNewsJSON(GET_NEWS_URL_HackerNews);
            getTechNewsJSONFinal(GET_NEWS_URL_TechRadar);

        }else {
            Log.e("You're offline now: ", "..");
            getTechNewsFromDatabase();
        }

        return recyclerView;
    }

    public void getTechNewsJSON(String NEWS_REQUEST_URL) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), NEWS_REQUEST_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseObject) {
                try {

                    SQLiteDatabase db = techDbHelper.getReadableDatabase();
                    Cursor rs = db.rawQuery("SELECT * FROM techNews",null);

                    JSONArray responseArray = responseObject.getJSONArray("articles");
                    for (int i = 0; i < responseArray.length(); i++) {

                        JSONObject currentNewsObject = responseArray.getJSONObject(i);

                        String title = currentNewsObject.getString("title");
                        String description = currentNewsObject.getString("description");
                        String url = currentNewsObject.getString("url");
                        String urlToImage = currentNewsObject.getString("urlToImage");
                        String publishedAt = currentNewsObject.getString("publishedAt");

                        if(description.length()>5) {

                            int flag = 0;
                            if (rs.moveToFirst()) {

                                while (!rs.isAfterLast()) {

                                    String title_in_db = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_TITLE));

                                    if (title_in_db.equals(title)) {

                                        flag = 1;
                                    }
                                    rs.moveToNext();
                                }
                            }
                            if (flag == 0) {

                                techDbHelper.insertNewsTech(title, description, url, urlToImage, publishedAt, 0, techNews_table_item_index);

                                techNews_table_item_index++;

                            }

                            News currentNews = new News(title, description, publishedAt, url, urlToImage);
                            al_news.add(currentNews);

                        }

                        Log.d("QUESTIONS: " + String.valueOf(i), title);
                    }
                    Log.e("FROM OTHERS::: ","...");
                    rs.close();

                    adapter = new TechFragContentAdapter(recyclerView.getContext(), al_news);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                } catch (JSONException e) {
                    Log.e("QueryUtils", "Problem parsing the News JSON results", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }



    public void getTechNewsJSONFinal(String NEWS_REQUEST_URL) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), NEWS_REQUEST_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseObject) {
                try {

                    SQLiteDatabase db = techDbHelper.getReadableDatabase();
                    Cursor rs = db.rawQuery("SELECT * FROM techNews",null);

                    JSONArray responseArray = responseObject.getJSONArray("articles");
                    for (int i = 0; i < responseArray.length(); i++) {

                        JSONObject currentNewsObject = responseArray.getJSONObject(i);

                        String title = currentNewsObject.getString("title");
                        String description = currentNewsObject.getString("description");
                        String url = currentNewsObject.getString("url");
                        String urlToImage = currentNewsObject.getString("urlToImage");
                        String publishedAt = currentNewsObject.getString("publishedAt");

                        if(description.length()>5) {

                            int flag = 0;
                            if (rs.moveToFirst()) {

                                while (!rs.isAfterLast()) {

                                    String title_in_db = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_TITLE));

                                    if (title_in_db.equals(title)) {

                                        flag = 1;
                                    }
                                    rs.moveToNext();
                                }
                            }
                            if (flag == 0) {

                                techDbHelper.insertNewsTech(title, description, url, urlToImage, publishedAt, 0, techNews_table_item_index);

                                techNews_table_item_index++;
                            }

                            News currentNews = new News(title, description, publishedAt, url, urlToImage);
                            al_news.add(currentNews);
                        }

                        Log.d("QUESTIONS: " + String.valueOf(i), title);
                    }
                    Log.e("FROM TECHCRUNCH::: ","...");

                    rs.close();

                    adapter = new TechFragContentAdapter(recyclerView.getContext(), al_news);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                } catch (JSONException e) {
                    Log.e("QueryUtils", "Problem parsing the News JSON results", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }


    public void getTechNewsFromDatabase(){

        SQLiteDatabase db = techDbHelper.getReadableDatabase();
        Cursor rs = db.rawQuery("SELECT * FROM techNews",null);

        if (rs.moveToFirst()) {
            while (!rs.isAfterLast()) {

                String title = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_TITLE));
                String description = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_DESC));
                String urlToImage = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_URLTOIMAGE));
                String url = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_URL));
                String publishedAt = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_PUBLISHEDAT));

                Log.e("title: ", title);

                News currentNews = new News(title, description, publishedAt, url, urlToImage);
                al_news.add(currentNews);
                rs.moveToNext();
            }
        }
        rs.close();

        adapter = new TechFragContentAdapter(recyclerView.getContext(), al_news);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}