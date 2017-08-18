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

import com.example.absolutelysaurabh.newshour.BookMarks.BookmarksDbHelper;
import com.example.absolutelysaurabh.newshour.BookMarks.HeadlinesDbHelper;
import com.example.absolutelysaurabh.newshour.BookMarks.NewsDbHelper;
import com.example.absolutelysaurabh.newshour.Config.Config;
import com.example.absolutelysaurabh.newshour.Activity.DetailsActivity;
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

    private BookmarksDbHelper bookmarksDbHelper;
    private HeadlinesDbHelper headlineNewsDbHelper;

    public static ArrayList<String> al_news_title;
    public static ArrayList<String> al_news_desc;
    public static ArrayList<String> al_news_url;
    public static ArrayList<String> al_news_urlToImage;
    public static ArrayList<String> al_news_publishedAt;

    RecyclerView recyclerView;
    ContentAdapter adapter;
    public static int index = 0;

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
        al_news_desc = new ArrayList<>();
        al_news_title = new ArrayList<>();
        al_news_publishedAt = new ArrayList<>();
        al_news_url = new ArrayList<>();
        al_news_urlToImage = new ArrayList<>();

        bookmarksDbHelper = new BookmarksDbHelper(getContext());
        headlineNewsDbHelper = new HeadlinesDbHelper(getContext());

        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        loadingIndicator.setVisibility(View.VISIBLE);


        if((new ConnectionDetector(getContext())).isConnectingToInternet()) {

            Log.e("Connected to internet: ", "..");

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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView avator;
        public TextView title;
        public TextView description;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.item_headline, parent, false));
            avator = (CircleImageView) itemView.findViewById(R.id.list_avatar);
            title = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailsActivity.class);

                    Bundle bund = new Bundle();
                    bund.putInt("tab",1);
                    bund.putInt(DetailsActivity.EXTRA_POSITION,getAdapterPosition());

                    intent.putExtra("bundle", bund);
                    context.startActivity(intent);
                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.
        private static final int LENGTH = al_news_desc.size();
        private Context context;
        private final Drawable[] mPlaceAvators;

        public ContentAdapter(Context context) {

            this.context = context;

            Resources resources = context.getResources();
            TypedArray a = resources.obtainTypedArray(R.array.places_picture);
            mPlaceAvators = new Drawable[a.length()];
            for (int i = 0; i < mPlaceAvators.length; i++) {
                mPlaceAvators[i] = a.getDrawable(i);
            }
            // a.recycle();

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
//            Picasso.with(context).load(R.drawable.a).into(holder.avator);

            try {
                holder.title.setText(al_news_title.get(position));
                holder.description.setText(al_news_desc.get(position));
            }catch (IndexOutOfBoundsException e){

                e.printStackTrace();
            }

            Log.e("position holder: ", String.valueOf(position));
            index++;

        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
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

                        JSONObject currentNews = responseArray.getJSONObject(i);

                        String title = currentNews.getString("title");
                        String description = currentNews.getString("description");
                        String url = currentNews.getString("url");
                        String urlToImage = currentNews.getString("urlToImage");
                        String publishedAt = currentNews.getString("publishedAt");


                        if(description.length()>5) {

                            int flag = 0;
                            if (rs.moveToFirst()) {

                                while (!rs.isAfterLast()) {

                                    String title_in_db = rs.getString(rs.getColumnIndex(HeadlinesDbHelper.COLUMN_NEWS_TITLE));

                                    if (title_in_db.equals(title)) {

                                        flag = 1;
                                    }
                                    rs.moveToNext();
                                }
                            }
                            if (flag == 0) {

                                headlineNewsDbHelper.insertNews(title, description, url, urlToImage, publishedAt, 0);
                            }


                            al_news_desc.add(description);
                            al_news_urlToImage.add(urlToImage);
                            al_news_publishedAt.add(publishedAt);
                            al_news_title.add(title);
                            al_news_url.add(url);
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
                    Cursor rs = db.rawQuery("SELECT * FROM simpleNews",null);

                    JSONArray responseArray = responseObject.getJSONArray("articles");
                    for(int i=0;i<responseArray.length();i++){

                        JSONObject currentNews = responseArray.getJSONObject(i);

                        String title = currentNews.getString("title");
                        String description = currentNews.getString("description");
                        String url = currentNews.getString("url");
                        String urlToImage = currentNews.getString("urlToImage");
                        String publishedAt = currentNews.getString("publishedAt");

                        if(description.length()>5) {


                            int flag = 0;
                            if (rs.moveToFirst()) {

                                while (!rs.isAfterLast()) {

                                    String title_in_db = rs.getString(rs.getColumnIndex(HeadlinesDbHelper.COLUMN_NEWS_TITLE));

                                    if (title_in_db.equals(title)) {

                                        flag = 1;
                                    }
                                    rs.moveToNext();
                                }
                            }
                            if (flag == 0) {

                                headlineNewsDbHelper.insertNews(title, description, url, urlToImage, publishedAt, 0);
                            }


                            al_news_desc.add(description);
                            al_news_urlToImage.add(urlToImage);
                            al_news_publishedAt.add(publishedAt);
                            al_news_title.add(title);
                            al_news_url.add(url);
                        }

                        Log.d("QUESTIONS: "+ String.valueOf(i), title);
                    }

                    View loadingIndicator = listItemView.findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);

                    adapter = new ContentAdapter(recyclerView.getContext());
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

                String title = rs.getString(rs.getColumnIndex(BookmarksDbHelper.COLUMN_NEWS_TITLE));
                String desc = rs.getString(rs.getColumnIndex(BookmarksDbHelper.COLUMN_NEWS_DESC));
                String urlToImage = rs.getString(rs.getColumnIndex(BookmarksDbHelper.COLUMN_NEWS_URLTOIMAGE));
                String url = rs.getString(rs.getColumnIndex(BookmarksDbHelper.COLUMN_NEWS_URL));
                String publishedAt = rs.getString(rs.getColumnIndex(BookmarksDbHelper.COLUMN_NEWS_PUBLISHEDAT));

                Log.e("title: ", title);

                al_news_title.add(title);
                al_news_desc.add(desc);
                al_news_url.add(url);
                al_news_urlToImage.add(urlToImage);
                al_news_publishedAt.add(publishedAt);

                rs.moveToNext();
            }
        }
        rs.close();

        adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}