package com.appsomniac.newshour.ViewHolder;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.appsomniac.newshour.Activity.DetailsActivity;
import com.appsomniac.newshour.Activity.WebViewActivity;
import com.appsomniac.newshour.data.NewsDbHelper;
import com.appsomniac.newshour.Model.News;
import com.appsomniac.newshour.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by absolutelysaurabh on 22/8/17.
 */

public class TechFragViewHolder extends RecyclerView.ViewHolder {

    public Context context;
    public ImageView picture;
    public TextView title;
    public TextView description;
    private NewsDbHelper techDbHelper, bookmarksDbHelper;
    private ArrayList<News> al_news;


    public TechFragViewHolder(LayoutInflater inflater, ViewGroup parent, final ArrayList<News> al_news) {

        super(inflater.inflate(R.layout.item_tech, parent, false));

        this.context = inflater.getContext();
        this.al_news = al_news;

        picture = (ImageView) itemView.findViewById(R.id.card_image);
        title = (TextView) itemView.findViewById(R.id.card_title);
        description = (TextView) itemView.findViewById(R.id.card_text);

        bookmarksDbHelper = new NewsDbHelper(context);
        techDbHelper = new NewsDbHelper(context);

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

        Button button = (Button) itemView.findViewById(R.id.read_full_story_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = v.getContext();
                Intent intent = new Intent(context, WebViewActivity.class);

                intent.putExtra("articleUrl", al_news.get(getAdapterPosition()).getUrl() );
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

                        String title_in_db = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_TITLE));

                        if(title_in_db.equals(al_news.get(getAdapterPosition()).getTitle())){

                            flag=1;
                        }
                        rs.moveToNext();
                    }
                }
                if(flag==0){

                    SharedPreferences prefs = context.getSharedPreferences("bookmarksPrefs", MODE_PRIVATE);
                    int bookmarks_index = prefs.getInt("bookmarks_item_index", 0);

                    isInserted =  bookmarksDbHelper.insertNewsBookmark(al_news.get(getAdapterPosition()).getTitle()
                            , al_news.get(getAdapterPosition()).getDescription()
                            , al_news.get(getAdapterPosition()).getUrl()
                            , al_news.get(getAdapterPosition()).getUrlToImage()
                            , al_news.get(getAdapterPosition()).getPublishedAt()
                            , bookmarks_index );

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("bookmarks_item_index", bookmarks_index+1);
                    editor.commit();

                }

                if(isInserted){

                    boolean isBookmarked = techDbHelper.updateNewsTech(getAdapterPosition(),
                            al_news.get(getAdapterPosition()).getTitle(),
                            al_news.get(getAdapterPosition()).getDescription(),
                            al_news.get(getAdapterPosition()).getUrl(),
                            al_news.get(getAdapterPosition()).getUrlToImage(),
                            al_news.get(getAdapterPosition()).getPublishedAt(), 1);

                    if(isBookmarked){

                        favoriteImageButton.setClickable(false);
                        favoriteImageButton.setColorFilter(Color.rgb(30,144,255));
                        Snackbar.make(v, "Added to Bookmarks. ", Snackbar.LENGTH_SHORT).show();
                    }

                }else {

                    if(flag==1){

                        favoriteImageButton.setClickable(false);
                        favoriteImageButton.setColorFilter(Color.rgb(30,144,255));
                        Snackbar.make(v, "Already added! ", Snackbar.LENGTH_SHORT).show();
                    }else {

                        Snackbar.make(v, "Adding bookmark failed!! ", Snackbar.LENGTH_SHORT).show();
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
                sendIntent.putExtra(Intent.EXTRA_TEXT, al_news.get(getAdapterPosition()).getUrl());
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent,("Share news via:")));
                Snackbar.make(v, "Sharing....", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
