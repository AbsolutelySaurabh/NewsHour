package dexter.appsomniac.newshour.classfragments;

import android.content.SharedPreferences;
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

import dexter.appsomniac.newshour.adapter.TechFragContentAdapter;
import dexter.appsomniac.newshour.data.NewsDbHelper;
import dexter.appsomniac.newshour.config.Config;
import dexter.appsomniac.newshour.model.News;
import dexter.appsomniac.newshour.R;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by absolutelysaurabh on 6/8/17.
 */
public class TechFragment extends Fragment{

    private NewsDbHelper techDbHelper, bookmarksDbHelper;
    public static ArrayList<News> al_news;

    SharedPreferences.Editor editor;

    TechFragContentAdapter adapter;
    RecyclerView recyclerView;
    View listItemView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initViews(inflater, container);

        getTechNewsFromDatabase();

        return recyclerView;
    }

    private void initViews(LayoutInflater inflater, ViewGroup container){

        al_news = new ArrayList<>();
        bookmarksDbHelper = new NewsDbHelper(getContext());
        techDbHelper = new NewsDbHelper(getContext());

        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);

        listItemView = inflater.inflate(R.layout.item_tech, container, false);
        View loadingIndicator = listItemView.findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

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