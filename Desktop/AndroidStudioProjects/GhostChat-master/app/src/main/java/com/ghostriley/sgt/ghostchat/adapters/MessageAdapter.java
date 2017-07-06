package com.ghostriley.sgt.ghostchat.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghostriley.sgt.ghostchat.utils.ParseConstants;
import com.ghostriley.sgt.ghostchat.R;
import com.ghostriley.sgt.ghostchat.UI.FriendsFragment;
import com.ghostriley.sgt.ghostchat.UI.InboxFragment;
import com.parse.ParseObject;

import java.sql.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by GhostRiley on 07-01-2016.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_item, messages);
        mMessages=messages;
        mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
            convertView.setTag(holder);
        }
        else {
            holder=(ViewHolder)convertView.getTag();
        }

        ParseObject message=mMessages.get(position);
        java.util.Date createdAt=message.getCreatedAt();
        long now=new java.util.Date().getTime();
        String convertedDate= DateUtils.getRelativeTimeSpanString
                (createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();

        holder.timeLabel.setText(convertedDate);

        if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.drawable.ic_image);
        }
        else {
            holder.iconImageView.setImageResource(R.drawable.ic_video);
        }
        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));

        return convertView;
    }

    public void refill (List<ParseObject> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;
    }

    /**
     * Created by GhostRiley on 30-12-2015.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        protected Context mContext;

        public SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext=context;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new InboxFragment();

                case 1:
                    return new FriendsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return mContext.getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return mContext.getString(R.string.title_section2).toUpperCase(l);
        }
            return null;
        }
    }
}


