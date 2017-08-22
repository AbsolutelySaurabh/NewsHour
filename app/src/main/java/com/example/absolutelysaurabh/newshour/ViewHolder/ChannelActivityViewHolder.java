package com.example.absolutelysaurabh.newshour.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.absolutelysaurabh.newshour.Activity.DetailsActivity;
import com.example.absolutelysaurabh.newshour.Activity.WebViewActivity;
import com.example.absolutelysaurabh.newshour.BookMarks.NewsDbHelper;
import com.example.absolutelysaurabh.newshour.Model.News;
import com.example.absolutelysaurabh.newshour.R;

import java.util.ArrayList;

/**
 * Created by absolutelysaurabh on 23/8/17.
 */

public class ChannelActivityViewHolder extends RecyclerView.ViewHolder {

    public ImageView picture;
    public TextView title;
    public TextView description;
    public Context context;
    public ArrayList<News> al_news;
    private NewsDbHelper bookmarksDbHelper;
    private NewsDbHelper simpleNewsDbHelper;
    public static int bookmark_table_item_index = 0, techNews_table_item_index = 0;

    public ChannelActivityViewHolder(LayoutInflater inflater, ViewGroup parent, final ArrayList<News> al_news) {

        super(inflater.inflate(R.layout.item_tech, parent, false));
        this.context = inflater.getContext();
        this.al_news = al_news;

        bookmarksDbHelper = new NewsDbHelper(context);
        simpleNewsDbHelper = new NewsDbHelper(context);

        picture = (ImageView) itemView.findViewById(R.id.card_image);
        title = (TextView) itemView.findViewById(R.id.card_title);
        description = (TextView) itemView.findViewById(R.id.card_text);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DetailsActivity.class);

                Bundle bund = new Bundle();
                bund.putInt("tab",2);
                bund.putInt(DetailsActivity.EXTRA_POSITION,getAdapterPosition());

                intent.putExtra("bundle", bund);
                context.startActivity(intent);
            }
        });

        // Adding Snackbar to Action Button inside card
        Button button = (Button)itemView.findViewById(R.id.read_full_story_button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("articleUrl", al_news.get(getAdapterPosition()).getUrl());
                context.startActivity(intent);
            }
        });

        final ImageButton favoriteImageButton = (ImageButton) itemView.findViewById(R.id.favorite_button);
        favoriteImageButton.setOnClickListener(new View.OnClickListener(){
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

                    isInserted =  bookmarksDbHelper.insertNewsBookmark(al_news.get(getAdapterPosition()).getTitle()
                            , al_news.get(getAdapterPosition()).getDescription()
                            , al_news.get(getAdapterPosition()).getUrl()
                            , al_news.get(getAdapterPosition()).getUrlToImage()
                            , al_news.get(getAdapterPosition()).getPublishedAt()
                            , bookmark_table_item_index );

                    bookmark_table_item_index++;

                }

                if(isInserted){
                    boolean isBookmarked = simpleNewsDbHelper.updateNewsTech(getAdapterPosition(),
                            al_news.get(getAdapterPosition()).getTitle(),
                            al_news.get(getAdapterPosition()).getDescription(),
                            al_news.get(getAdapterPosition()).getUrl(),
                            al_news.get(getAdapterPosition()).getUrlToImage(),
                            al_news.get(getAdapterPosition()).getPublishedAt(), 1);

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
            }
        });

        ImageButton shareImageButton = (ImageButton) itemView.findViewById(R.id.share_button);
        shareImageButton.setOnClickListener(new View.OnClickListener(){
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
