package com.appsomniac.newshour.Adapter;

/**
 * Created by absolutelysaurabh on 22/8/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.appsomniac.newshour.ClassFragments.TechFragment;
import com.appsomniac.newshour.Model.News;
import com.appsomniac.newshour.R;
import com.appsomniac.newshour.ViewHolder.TechFragViewHolder;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
/**
 * Adapter to display recycler view.
 */
public class TechFragContentAdapter extends RecyclerView.Adapter<TechFragViewHolder> {

    private static ArrayList<News> al_news;

    private Context context;

    public TechFragContentAdapter(Context context, ArrayList<News> al_news) {

        this.context = context;
        this.al_news = al_news;
    }

    @Override
    public TechFragViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TechFragViewHolder(LayoutInflater.from(parent.getContext()), parent, al_news);
    }

    @Override
    public void onBindViewHolder(TechFragViewHolder holder, int position) {

        try {

            Picasso.with(context).load(al_news.get(position).getUrlToImage()).error(R.drawable.recode_2).resize(300, 150).centerCrop()
                    .into(holder.picture);
            holder.title.setText(al_news.get(position).getTitle());
            holder.description.setText(al_news.get(position).getDescription());

        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        Log.e("position holder: ", String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return al_news.size();
    }
}