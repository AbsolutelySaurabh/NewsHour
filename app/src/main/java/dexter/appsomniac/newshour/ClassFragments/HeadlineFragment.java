package dexter.appsomniac.newshour.ClassFragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dexter.appsomniac.newshour.Adapter.HeadLineFragContentAdapter;
import dexter.appsomniac.newshour.data.NewsDbHelper;
import dexter.appsomniac.newshour.Config.Config;
import dexter.appsomniac.newshour.Model.News;
import dexter.appsomniac.newshour.R;

import java.util.ArrayList;

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

        //Log.e("GET NEWS URL TOI: ", GET_NEWS_URL_TOI);

        listItemView = inflater.inflate(R.layout.item_headline, container, false);
        loadingIndicator = listItemView.findViewById(R.id.loading_indicator);

        al_news = new ArrayList<>();

        bookmarksDbHelper = new NewsDbHelper(getContext());
        headlineNewsDbHelper = new NewsDbHelper(getContext());

        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        loadingIndicator.setVisibility(View.VISIBLE);

        getHeadlinesFromDatabase();
        return recyclerView;
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
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}