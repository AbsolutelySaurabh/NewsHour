package dexter.appsomniac.newshour.adapter;

/**
 * Created by absolutelysaurabh on 22/8/17.
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import dexter.appsomniac.newshour.model.News;
import dexter.appsomniac.newshour.R;
import dexter.appsomniac.newshour.util.Utils;
import dexter.appsomniac.newshour.viewholder.HeadLineFragViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

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

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        requestOptions.error(R.mipmap.ic_launcher);

        Glide.with(context).load(al_news.get(position).getUrlToImage()).apply(requestOptions).thumbnail(0.5f).into(holder.avator);

        try {
            holder.title.setText(al_news.get(position).getTitle());
            holder.description.setText(al_news.get(position).getDescription());
            runEnterAnimation(holder.itemView);
        } catch (IndexOutOfBoundsException e) {

            e.printStackTrace();
        }

        Log.d("position holder: ", String.valueOf(position));

    }

    private void runEnterAnimation(View view) {
        view.setTranslationY(Utils.getScreenHeight(context));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    @Override
    public int getItemCount() {
        return al_news.size();
    }
}