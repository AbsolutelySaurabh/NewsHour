package com.appsomniac.newshour.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsomniac.newshour.Activity.DetailsActivity;
import com.appsomniac.newshour.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by absolutelysaurabh on 23/8/17.
 */

public class HeadLineFragViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView avator;
    public TextView title;
    public TextView description;

    public HeadLineFragViewHolder(LayoutInflater inflater, ViewGroup parent) {

        super(inflater.inflate(R.layout.item_headline, parent, false));
        avator = (CircleImageView) itemView.findViewById(R.id.list_avatar);
        title = (TextView) itemView.findViewById(R.id.list_title);
        description = (TextView) itemView.findViewById(R.id.list_desc);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DetailsActivity.class);

                Bundle bund = new Bundle();
                bund.putInt("tab",1);
                bund.putInt(DetailsActivity.EXTRA_POSITION,getAdapterPosition());

                intent.putExtra("bundle", bund);
                context.startActivity(intent);
            }
        });
    }
}