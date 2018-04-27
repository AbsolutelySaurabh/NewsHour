package dexter.appsomniac.newshour.classfragments;

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

import dexter.appsomniac.newshour.adapter.HeadLineFragContentAdapter;
import dexter.appsomniac.newshour.data.NewsDbHelper;
import dexter.appsomniac.newshour.config.Config;
import dexter.appsomniac.newshour.model.News;
import dexter.appsomniac.newshour.R;

import java.util.ArrayList;

/**
 * Created by absolutelysaurabh on 6/8/17.
 */

public class HeadlineFragment extends Fragment {

    private NewsDbHelper headlineNewsDbHelper, bookmarksDbHelper;

    RecyclerView recyclerView;
    HeadLineFragContentAdapter adapter;
    public static int index = 0;
    public static ArrayList<News> al_news;

    View listItemView, loadingIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initViews(inflater, container);

        getHeadlinesFromDatabase();

        return recyclerView;
    }

    private void initViews(LayoutInflater inflater, ViewGroup container){

        al_news = new ArrayList<>();
        bookmarksDbHelper = new NewsDbHelper(getContext());
        headlineNewsDbHelper = new NewsDbHelper(getContext());

        listItemView = inflater.inflate(R.layout.item_headline, container, false);
        loadingIndicator = listItemView.findViewById(R.id.loading_indicator);
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        loadingIndicator.setVisibility(View.VISIBLE);

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