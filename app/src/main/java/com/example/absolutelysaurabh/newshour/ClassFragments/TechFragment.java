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
import com.example.absolutelysaurabh.newshour.BookMarks.BookmarksDbHelper;
import com.example.absolutelysaurabh.newshour.BookMarks.NewsDbHelper;
import com.example.absolutelysaurabh.newshour.Config.Config;
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
    private BookmarksDbHelper bookmarksDbHelper;
    private NewsDbHelper simpleNewsDbHelper;

    public static ArrayList<String> al_news_title;
    public static ArrayList<String> al_news_desc;
    public static ArrayList<String> al_news_url;
    public static ArrayList<String> al_news_urlToImage;
    public static ArrayList<String> al_news_publishedAt;

    public static int index = 0, isConnected=0;
    ContentAdapter adapter;
    RecyclerView recyclerView;
    View listItemView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GET_NEWS_URL_TechCrunch = Config.TECHCRUNCH_URL + Config.API_KEY;
        GET_NEWS_URL_TechRadar = Config.TECHRADAR_URL + Config.API_KEY ;
        GET_NEWS_URL_HackerNews= Config.HACKERNEWS_URL + Config.API_KEY;
        Log.e("GET NEWS URL: ", GET_NEWS_URL_TechCrunch);

        al_news_desc = new ArrayList<>();
        al_news_title = new ArrayList<>();
        al_news_publishedAt = new ArrayList<>();
        al_news_url = new ArrayList<>();
        al_news_urlToImage = new ArrayList<>();

        bookmarksDbHelper = new BookmarksDbHelper(getContext());
        simpleNewsDbHelper = new NewsDbHelper(getContext());

        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);

        listItemView = inflater.inflate(R.layout.item_tech, container, false);
        View loadingIndicator = listItemView.findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        if((new ConnectionDetector(getContext())).isConnectingToInternet()) {

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView picture;
        public TextView title;
        public TextView description;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.item_tech, parent, false));
            picture = (ImageView) itemView.findViewById(R.id.card_image);
            title = (TextView) itemView.findViewById(R.id.card_title);
            description = (TextView) itemView.findViewById(R.id.card_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailsActivity.class);

                    Bundle bund = new Bundle();
                    bund.putInt("tab", 3);
                    bund.putInt(DetailsActivity.EXTRA_POSITION, getAdapterPosition());

                    intent.putExtra("bundle", bund);
                    context.startActivity(intent);
                }
            });

            // Adding Snackbar to Action Button inside card
            Button button = (Button) itemView.findViewById(R.id.read_full_story_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = v.getContext();
                    Intent intent = new Intent(context, WebViewActivity.class);

                    intent.putExtra("articleUrl", al_news_url.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

            final ImageButton favoriteImageButton;favoriteImageButton =
                    (ImageButton) itemView.findViewById(R.id.favorite_button);
            favoriteImageButton.setClickable(true);

            favoriteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SQLiteDatabase db = bookmarksDbHelper.getReadableDatabase();
                    Cursor rs = db.rawQuery("SELECT * FROM bookmarks",null);
                    int flag=0;
                    boolean isInserted = false;
                    if(rs.moveToFirst()){

                        while(!rs.isAfterLast()){

                            String title_in_db = rs.getString(rs.getColumnIndex(BookmarksDbHelper.COLUMN_NEWS_TITLE));

                            if(title_in_db.equals(al_news_title.get(getAdapterPosition()))){

                                flag=1;
                            }
                            rs.moveToNext();
                        }
                    }
                    if(flag==0){

                        isInserted =  bookmarksDbHelper.insertNews(al_news_title.get(getAdapterPosition()), al_news_desc.get(getAdapterPosition()),
                                al_news_url.get(getAdapterPosition()), al_news_urlToImage.get(getAdapterPosition()),
                                al_news_publishedAt.get(getAdapterPosition()));
                    }

                    if(isInserted){

                        boolean isBookmarked = simpleNewsDbHelper.updateNews(getAdapterPosition(),al_news_title.get(getAdapterPosition()),
                                al_news_desc.get(getAdapterPosition()), al_news_url.get(getAdapterPosition()),
                                al_news_urlToImage.get(getAdapterPosition()), al_news_publishedAt.get(getAdapterPosition()), 1);

                        if(isBookmarked){

                            favoriteImageButton.setClickable(false);
                            favoriteImageButton.setColorFilter(Color.rgb(30,144,255));
                            Snackbar.make(v, "Added to Favorites ", Snackbar.LENGTH_LONG).show();
                        }

                    }else {

                        if(flag==1){

                            favoriteImageButton.setClickable(false);
                            favoriteImageButton.setColorFilter(Color.rgb(30,144,255));
                            Snackbar.make(v, "Already added! ", Snackbar.LENGTH_LONG).show();
                        }else {

                            Snackbar.make(v, "Adding bookmark failed!! ", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    rs.close();
                    db.close();
                }
            });

            ImageButton shareImageButton = (ImageButton) itemView.findViewById(R.id.share_button);
            shareImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, al_news_url.get(getAdapterPosition()));
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent,("Share news via:")));
                    Snackbar.make(v, "Sharing....", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of Card in RecyclerView.

        private int LENGTH = al_news_title.size();

        private Context context;

        public ContentAdapter(Context context) {

            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            try {
                Picasso.with(context).load(al_news_urlToImage.get(position)).error(R.drawable.guardian).into(holder.picture);
                holder.title.setText(al_news_title.get(position));
                holder.description.setText(al_news_desc.get(position));
            }catch(IndexOutOfBoundsException e){
                e.printStackTrace();
            }

            Log.e("position holder: ", String.valueOf(position));

        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

    public void getTechNewsJSON(String NEWS_REQUEST_URL) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), NEWS_REQUEST_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseObject) {
                try {

                    SQLiteDatabase db = simpleNewsDbHelper.getReadableDatabase();
                    Cursor rs = db.rawQuery("SELECT * FROM simpleNews",null);

                    JSONArray responseArray = responseObject.getJSONArray("articles");
                    for (int i = 0; i < responseArray.length(); i++) {

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

                                    String title_in_db = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_TITLE));

                                    if (title_in_db.equals(title)) {

                                        flag = 1;
                                    }
                                    rs.moveToNext();
                                }
                            }
                            if (flag == 0) {

                                simpleNewsDbHelper.insertNews(title, description, url, urlToImage, publishedAt, 0);
                            }

                            al_news_title.add(title);
                            al_news_desc.add(description);
                            al_news_url.add(url);
                            al_news_urlToImage.add(urlToImage);
                            al_news_publishedAt.add(publishedAt);
                        }

                        Log.d("QUESTIONS: " + String.valueOf(i), title);
                    }
                    Log.e("FROM OTHERS::: ","...");
                    rs.close();

                    adapter = new ContentAdapter(recyclerView.getContext());
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

                    SQLiteDatabase db = simpleNewsDbHelper.getReadableDatabase();
                    Cursor rs = db.rawQuery("SELECT * FROM simpleNews",null);

                    JSONArray responseArray = responseObject.getJSONArray("articles");
                    for (int i = 0; i < responseArray.length(); i++) {

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

                                    String title_in_db = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_TITLE));

                                    if (title_in_db.equals(title)) {

                                        flag = 1;
                                    }
                                    rs.moveToNext();
                                }
                            }
                            if (flag == 0) {

                                simpleNewsDbHelper.insertNews(title, description, url, urlToImage, publishedAt, 0);
                            }

                            al_news_title.add(title);
                            al_news_desc.add(description);
                            al_news_url.add(url);
                            al_news_urlToImage.add(urlToImage);
                            al_news_publishedAt.add(publishedAt);
                        }

                        Log.d("QUESTIONS: " + String.valueOf(i), title);
                    }
                    Log.e("FROM TECHCRUNCH::: ","...");

                    rs.close();

                    adapter = new ContentAdapter(recyclerView.getContext());
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

        SQLiteDatabase db = simpleNewsDbHelper.getReadableDatabase();
        Cursor rs = db.rawQuery("SELECT * FROM simpleNews",null);

        if (rs.moveToFirst()) {
            while (!rs.isAfterLast()) {

                String title = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_TITLE));
                String desc = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_DESC));
                String urlToImage = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_URLTOIMAGE));
                String url = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_URL));
                String publishedAt = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_PUBLISHEDAT));

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