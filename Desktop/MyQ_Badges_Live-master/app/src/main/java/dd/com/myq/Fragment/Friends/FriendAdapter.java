package dd.com.myq.Fragment.Friends;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dd.com.myq.R;

public class FriendAdapter extends ArrayAdapter<Friend> {


    private ArrayList<Friend> dataSet;
    Context mContext;

    private static class ViewHolder {

        TextView name;
        TextView points;

    }


    public FriendAdapter(ArrayList<Friend> data, Context context) {

        super(context, R.layout.friends_list, data);

        this.dataSet = data;
        this.mContext = context;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Friend friend = getItem(position);

        Log.d("PPPOOOSSSIIITTTON", String.valueOf(position));

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.friends_list, parent, false);

            viewHolder.name = (TextView) convertView.findViewById(R.id.friend_name);
            viewHolder.points = (TextView) convertView.findViewById(R.id.friend_points);


            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        viewHolder.name.setText(friend.getName());
        viewHolder.points.setText(friend.getPoints());

        return convertView;
    }

}

//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//
//        //Check if there is an existing list item view called convertView which we can reuse
//        //otherwise, if convertView is null then inflate a new list iem layout
//
//        //inflate is a system service that convert a View out of a XML layout
//        View listItemView = convertView;
//        if(listItemView  == null){
//
//            //Inflate is a system service that creates a View out of an XML layout
//            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.friends_list,parent,false);
//
//        }
//        //Find the news at the given positiion in the list of the news
//        Friend currentNews = getItem(position);
//
//        String name = currentNews.getName();
//        String points = currentNews.getPoints();
//
//
//        TextView name_text = (TextView)listItemView.findViewById(R.id.friend_name);
//        name_text.setText("Saurabh Singh");
//
//        TextView points_text = (TextView)listItemView.findViewById(R.id.friend_points);
//        points_text.setText("44");
//
//        return listItemView;
//    }

