package dexter.appsomniac.newshour.adapter;

/**
 * Created by absolutelysaurabh on 22/8/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import dexter.appsomniac.newshour.model.News;
import dexter.appsomniac.newshour.R;
import dexter.appsomniac.newshour.util.Utils;
import dexter.appsomniac.newshour.viewholder.TechFragViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.splash_back_2);
            requestOptions.error(R.drawable.splash_back_2);

            Glide.with(context).load(al_news.get(position).getUrlToImage()).apply(requestOptions).thumbnail(0.5f).into(holder.picture);

            holder.title.setText(al_news.get(position).getTitle());
            holder.description.setText(al_news.get(position).getDescription());
            runEnterAnimation(holder.itemView);

        }catch(IndexOutOfBoundsException e){
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