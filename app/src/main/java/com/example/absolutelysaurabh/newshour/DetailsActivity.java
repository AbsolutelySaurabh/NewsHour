package com.example.absolutelysaurabh.newshour;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.absolutelysaurabh.newshour.ClassFragments.HeadlineFragment;
import com.example.absolutelysaurabh.newshour.ClassFragments.TechFragment;
import com.example.absolutelysaurabh.newshour.R;
import com.squareup.picasso.Picasso;

/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        Bundle bundle = getIntent().getBundleExtra("bundle");

        int position = bundle.getInt(EXTRA_POSITION);
        int tab_opened = bundle.getInt("tab");

        Resources resources = getResources();

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        TextView newsDescription = (TextView) findViewById(R.id.place_detail);
        ImageView newsImage = (ImageView) findViewById(R.id.image);

        if(tab_opened == 1){

            collapsingToolbar.setTitle(HeadlineFragment.al_news_title.get(position));
            newsDescription.setText(HeadlineFragment.al_news_desc.get(position));
            Picasso.with(this).load(HeadlineFragment.al_news_urlToImage.get(position)).error(R.drawable.guardian).into(newsImage);

        }else
            if(tab_opened == 3){

                collapsingToolbar.setTitle(TechFragment.al_news_title.get(position));
                newsDescription.setText(TechFragment.al_news_desc.get(position));
                Picasso.with(this).load(TechFragment.al_news_urlToImage.get(position)).error(R.drawable.guardian).into(newsImage);

            }

        String[] placeLocations = resources.getStringArray(R.array.place_locations);
        TextView placeLocation =  (TextView) findViewById(R.id.place_location);
        placeLocation.setText(placeLocations[position % placeLocations.length]);

    }

    @Override
    public boolean onSupportNavigateUp(){

        finish();
        return true;
    }

}
