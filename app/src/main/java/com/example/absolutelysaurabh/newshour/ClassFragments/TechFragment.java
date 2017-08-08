package com.example.absolutelysaurabh.newshour.ClassFragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.absolutelysaurabh.newshour.Config.Config;
import com.example.absolutelysaurabh.newshour.DetailsActivity;
import com.example.absolutelysaurabh.newshour.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by absolutelysaurabh on 6/8/17.
 */
public class TechFragment extends Fragment {

    public static String GET_NEWS_URL = "";

    public static ArrayList<String> al_news_title;
    public static ArrayList<String> al_news_desc;
    public static ArrayList<String> al_news_url;
    public static ArrayList<String> al_news_urlToImage;
    public static ArrayList<String> al_news_publishedAt;

    public static int index = 0;
    ContentAdapter adapter;
    RecyclerView recyclerView;
    View listItemView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        GET_NEWS_URL = Config.TECHCRUNCH_URL + Config.API_KEY;
        Log.e("GET NEWS URL: ", GET_NEWS_URL);

        al_news_desc = new ArrayList<>();
        al_news_title = new ArrayList<>();
        al_news_publishedAt = new ArrayList<>();
        al_news_url = new ArrayList<>();
        al_news_urlToImage = new ArrayList<>();

        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);

        listItemView = inflater.inflate(R.layout.item_tech, container, false);
        View loadingIndicator = listItemView.findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        getTechNews(GET_NEWS_URL);

        Log.e("Ran into TIME: ", "........");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView picture;
        public TextView title;
        public TextView description;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.item_tech, parent, false));
            picture = (ImageView) itemView.findViewById(R.id.card_image);
            title = (TextView) itemView.findViewById(R.id.card_title);
            description = (TextView) itemView.findViewById(R.id.card_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailsActivity.class);

                    Bundle bund = new Bundle();
                    bund.putInt("tab",3);
                    bund.putInt(DetailsActivity.EXTRA_POSITION,getAdapterPosition());

                    intent.putExtra("bundle", bund);
                    context.startActivity(intent);
                }
            });

            // Adding Snackbar to Action Button inside card
            Button button = (Button)itemView.findViewById(R.id.action_button);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Action is pressed",
                            Snackbar.LENGTH_LONG).show();
                }
            });

            ImageButton favoriteImageButton =
                    (ImageButton) itemView.findViewById(R.id.favorite_button);
            favoriteImageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Added to Favorite",
                            Snackbar.LENGTH_LONG).show();
                }
            });

            ImageButton shareImageButton = (ImageButton) itemView.findViewById(R.id.share_button);
            shareImageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Share article",
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of Card in RecyclerView.
        private static final int LENGTH = al_news_desc.size();

        private Context context;

        public ContentAdapter(Context context) {

            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            Picasso.with(context).load(al_news_urlToImage.get(position)).error(R.drawable.guardian).into(holder.picture);
            holder.title.setText(al_news_title.get(position));
            holder.description.setText(al_news_desc.get(position));
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

    public void getTechNews(String NEWS_REQUEST_URL){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), NEWS_REQUEST_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseObject) {
                try {

                    JSONArray responseArray = responseObject.getJSONArray("articles");
                    for(int i=0;i<responseArray.length();i++){

                        JSONObject currentNews = responseArray.getJSONObject(i);

                        String title = currentNews.getString("title");
                        String description = currentNews.getString("description");
                        String url = currentNews.getString("url");
                        String urlToImage = currentNews.getString("urlToImage");
                        String publishedAt = currentNews.getString("publishedAt");

                        al_news_desc.add(description);
                        al_news_urlToImage.add(urlToImage);
                        al_news_publishedAt.add(publishedAt);
                        al_news_title.add(title);
                        al_news_url.add(url);

                        Log.d("QUESTIONS: "+ String.valueOf(i), title);
                    }

                    View loadingIndicator = listItemView.findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);

                    adapter = new ContentAdapter(recyclerView.getContext());
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e){
                    Log.e("QueryUtils", "Problem parsing the News JSON results", e);
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

}
