package com.appsomniac.newshour.Adapter;

/**
 * Created by absolutelysaurabh on 22/8/17.
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appsomniac.newshour.ClassFragments.HeadlineFragment;
import com.appsomniac.newshour.ClassFragments.TechFragment;
import com.appsomniac.newshour.Model.News;
import com.appsomniac.newshour.R;
import com.appsomniac.newshour.ViewHolder.HeadLineFragViewHolder;
import com.appsomniac.newshour.ViewHolder.TechFragViewHolder;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Random;

/**
 * Adapter to display recycler view.
 */
public class HeadLineFragContentAdapter extends RecyclerView.Adapter<HeadLineFragViewHolder> {

    private static ArrayList<News> al_news;
    private Context context;
    private final Drawable[] mPlaceAvators;

    public HeadLineFragContentAdapter(Context context, ArrayList<News> al_news) {

        this.context = context;
        this.al_news = al_news;

        Resources resources = context.getResources();
        TypedArray a = resources.obtainTypedArray(R.array.headline_icons);


        mPlaceAvators = new Drawable[a.length()];
        for (int i = 0; i < mPlaceAvators.length; i++) {
            mPlaceAvators[i] = a.getDrawable(i);
        }
    }

    @Override
    public HeadLineFragViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HeadLineFragViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(HeadLineFragViewHolder holder, int position) {

        holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);

        try {
            holder.title.setText(al_news.get(position).getTitle());
            holder.description.setText(al_news.get(position).getDescription());
        } catch (IndexOutOfBoundsException e) {

            e.printStackTrace();
        }

        Log.e("position holder: ", String.valueOf(position));

    }

    @Override
    public int getItemCount() {
        return al_news.size();
    }
}