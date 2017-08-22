package com.example.absolutelysaurabh.newshour.ClassFragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.absolutelysaurabh.newshour.Adapter.HeadLineFragContentAdapter;
import com.example.absolutelysaurabh.newshour.BookMarks.NewsDbHelper;
import com.example.absolutelysaurabh.newshour.Config.Config;
import com.example.absolutelysaurabh.newshour.Activity.DetailsActivity;
import com.example.absolutelysaurabh.newshour.Model.News;
import com.example.absolutelysaurabh.newshour.R;
import com.example.absolutelysaurabh.newshour.Util.ConnectionDetector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by absolutelysaurabh on 6/8/17.
 */

public class HeadlineFragment extends Fragment {

    public static String GET_NEWS_URL_TOI = "", GET_NEWS_URL_CNN = "", GET_NEWS_URL_HINDU = "",
            GET_NEWS_URL_GUARDIAN = "";

    private NewsDbHelper headlineNewsDbHelper, bookmarksDbHelper;

    RecyclerView recyclerView;
    HeadLineFragContentAdapter adapter;
    public static int index = 0;
    public static ArrayList<News> al_news;

    View listItemView, loadingIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GET_NEWS_URL_TOI = Config.TOI_URL + Config.API_KEY;
        GET_NEWS_URL_CNN = Config.CNN_URL + Config.API_KEY ;
        GET_NEWS_URL_HINDU = Config.HINDU_URL + Config.API_KEY;
        GET_NEWS_URL_GUARDIAN = Config.THEGUARDIAN_URL + Config.API_KEY;

        Log.e("GET NEWS URL TOI: ", GET_NEWS_URL_TOI);

        listItemView = inflater.inflate(R.layout.item_headline, container, false);
        loadingIndicator = listItemView.findViewById(R.id.loading_indicator);

        al_news = new ArrayList<>();

        bookmarksDbHelper = new NewsDbHelper(getContext());
        headlineNewsDbHelper = new NewsDbHelper(getContext());

        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        loadingIndicator.setVisibility(View.VISIBLE);


        if((new ConnectionDetector(getContext())).isConnectingToInternet()) {

            Log.e("Connected to internet: ", "..");

            headlineNewsDbHelper.deleteAllRecordHead();

            getHeadLines(GET_NEWS_URL_TOI);
            getHeadLines(GET_NEWS_URL_CNN);
            getHeadLines(GET_NEWS_URL_HINDU);
            getHeadLinesFinal(GET_NEWS_URL_GUARDIAN);

        }else {
            Log.e("You're offline now: ", "..");
            getHeadlinesFromDatabase();
        }

        Log.e("ANOTHER ONE: ", "........");
        return recyclerView;
    }

    public void getHeadLines(String NEWS_REQUEST_URL){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), NEWS_REQUEST_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseObject) {
                try {

                    SQLiteDatabase db = headlineNewsDbHelper.getReadableDatabase();
                    Cursor rs = db.rawQuery("SELECT * FROM headlineNews",null);

                    JSONArray responseArray = responseObject.getJSONArray("articles");
                    for(int i=0;i<responseArray.length();i++){

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

                                headlineNewsDbHelper.insertNewsHead(title, description, url, urlToImage, publishedAt, 0);
                            }

                            News currentNews = new News(title, description, publishedAt, url, urlToImage);
                            al_news.add(currentNews);
                        }

                        Log.d("QUESTIONS: "+ String.valueOf(i), title);
                    }

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


    public void getHeadLinesFinal(String NEWS_REQUEST_URL){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), NEWS_REQUEST_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseObject) {
                try {

                    SQLiteDatabase db = headlineNewsDbHelper.getReadableDatabase();
                    Cursor rs = db.rawQuery("SELECT * FROM headlineNews",null);

                    JSONArray responseArray = responseObject.getJSONArray("articles");
                    for(int i=0;i<responseArray.length();i++){

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

                                headlineNewsDbHelper.insertNewsHead(title, description, url, urlToImage, publishedAt, 0);
                            }

                            News currentNews = new News(title, description, publishedAt, url, urlToImage);
                            al_news.add(currentNews);
                        }

                        Log.d("QUESTIONS: "+ String.valueOf(i), title);
                    }

                    View loadingIndicator = listItemView.findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);

                    adapter = new HeadLineFragContentAdapter(recyclerView.getContext(), al_news);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

    public void getHeadlinesFromDatabase(){

        SQLiteDatabase db = headlineNewsDbHelper.getReadableDatabase();
        Cursor rs = db.rawQuery("SELECT * FROM headlineNews",null);

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

        adapter = new HeadLineFragContentAdapter(recyclerView.getContext(), al_news);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}