package com.example.absolutelysaurabh.newshour.Adapter;

/**
 * Created by absolutelysaurabh on 23/8/17.
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.absolutelysaurabh.newshour.Model.News;
import com.example.absolutelysaurabh.newshour.R;
import com.example.absolutelysaurabh.newshour.ViewHolder.ChannelActivityViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter to display recycler view.
 */
public class ChannelActivityContentAdapter extends RecyclerView.Adapter<ChannelActivityViewHolder> {

    ArrayList<News> al_news;
    private Context context;
    Drawable[] channelsPictures;

    public ChannelActivityContentAdapter(Context context, ArrayList<News> al_news) {

        this.context = context;
        this.al_news = al_news;

        Resources resources = context.getResources();
        TypedArray a = resources.obtainTypedArray(R.array.places_picture);
        channelsPictures = new Drawable[a.length()];
        for (int i = 0; i < channelsPictures.length; i++) {
            channelsPictures[i] = a.getDrawable(i);
        }

    }

    @Override
    public ChannelActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ChannelActivityViewHolder(LayoutInflater.from(parent.getContext()), parent, al_news);
    }

    @Override
    public void onBindViewHolder(ChannelActivityViewHolder holder, int position) {

        try {
            Log.e("OnBindViiewHolder: ", al_news.get(position).getTitle());
            Picasso.with(context).load(al_news.get(position).getUrlToImage())
                    .error(channelsPictures[position % channelsPictures.length]).into(holder.picture);

            holder.title.setText(al_news.get(position).getTitle());
            holder.description.setText(al_news.get(position).getDescription());
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return al_news.size();
    }
}

