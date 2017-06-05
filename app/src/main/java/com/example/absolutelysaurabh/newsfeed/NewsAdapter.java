package com.example.absolutelysaurabh.newsfeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by absolutelysaurabh on 31/3/17.
 */

public class NewsAdapter extends ArrayAdapter<News>{

    public NewsAdapter(@NonNull Context context, List<News> news) {

        super(context, 0,news);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Check if there is an existing list item view called convertView which we can reuse
        //otherwise, if convertView is null then inflate a new list iem layout

        //inflate is a system service that convert a View out of a XML layout
        View listItemView = convertView;
        if(listItemView  == null){

            //Inflate is a system service that creates a View out of an XML layout
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item,parent,false);

        }
        //Find the news at the given positiion in the list of the news
        News currentNews = getItem(position);

        String description = currentNews.getDescription();
        String title = currentNews.getTitle();
       // String url = currentNews.getUrl();
        String date = currentNews.getPublishedAt();

//        String date = currentNews.getPublishedAt();
//        Long newdate = Long.parseLong(date);

        TextView titleView = (TextView)listItemView.findViewById(R.id.title);
        titleView.setText(title);

        TextView descriptionView = (TextView)listItemView.findViewById(R.id.description);
        descriptionView.setText(description);

//        Date dateObject = new Date(newdate);
//
        TextView dateView = (TextView)listItemView.findViewById(R.id.date);
        String exactDate = "";
        for(int i=0;i<9;i++){

            exactDate = exactDate+date.charAt(i);

        }
        dateView.setText(date);
        String time="";
        for(int i=11;i<13;i++){

            time = time+date.charAt(i);
        }

//        TextView timeView = (TextView)listItemView.findViewById(R.id.time);
//        timeView.setText(time);
//
//        //Format the date
//        String formattedDate = formatDate(dateObject);
//        //Display the date
//        dateView.setText(formattedDate);
//
//        TextView timeView = (TextView)listItemView.findViewById(R.id.time);
//        //Format the string
//        String formattedTime = formatTime(dateObject);
//        //Display
//        timeView.setText(formattedTime);


        return listItemView;
    }

    private String formatTime(Date dateObject) {

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);

    }

    private String formatDate(Date dateObject) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyy");
        return dateFormat.format(dateObject);

    }
}
