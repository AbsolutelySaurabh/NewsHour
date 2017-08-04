package com.example.absolutelysaurabh.newsfeed.News;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.absolutelysaurabh.newsfeed.News.News;
import com.example.absolutelysaurabh.newsfeed.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NewsAdapter extends ArrayAdapter<News>{

    public NewsAdapter(@NonNull Context context, List<News> news) {

        super(context, 0,news);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //inflate is a system service that convert a View out of a XML layout
        View listItemView = convertView;
        if(listItemView  == null){

            //Inflate is a system service that creates a View out of an XML layout
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item,parent,false);

        }
        News currentNews = getItem(position);

        String description = currentNews.getDescription();
        String title = currentNews.getTitle();
        String url = currentNews.getUrl();
        String date = currentNews.getPublishedAt();

        TextView titleView = (TextView)listItemView.findViewById(R.id.title);
        titleView.setText(title);

        ImageView news_image = (ImageView) listItemView.findViewById(R.id.news_image);
        Log.e("url: ", url);
        Picasso.with(getContext()).load(url).resize(600,330).centerCrop().into(news_image);

        TextView descriptionView = (TextView)listItemView.findViewById(R.id.description);
        descriptionView.setText(description);

        return listItemView;
    }
}
