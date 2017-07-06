package com.ghostriley.sgt.ghostchat.UI;

import android.app.ActionBar;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.ghostriley.sgt.ghostchat.R;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

public class ViewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);


        Toast.makeText(this, "Loading image. Please wait...\nDo not press back button or the message would be lost",
                Toast.LENGTH_SHORT).show();


        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Uri imageUri = getIntent().getData();
        Picasso.with(this).load(imageUri.toString()).into(imageView);

    }
}