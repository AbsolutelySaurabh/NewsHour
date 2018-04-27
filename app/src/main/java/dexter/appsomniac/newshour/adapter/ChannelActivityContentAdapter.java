package dexter.appsomniac.newshour.adapter;

/**
 * Created by absolutelysaurabh on 23/8/17.
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import dexter.appsomniac.newshour.model.News;
import dexter.appsomniac.newshour.R;
import dexter.appsomniac.newshour.viewholder.ChannelActivityViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.splash_back_2);
            requestOptions.error(R.drawable.splash_back_2);

            Glide.with(context).load(al_news.get(position).getUrlToImage()).apply(requestOptions).thumbnail(0.5f).into(holder.picture);

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

