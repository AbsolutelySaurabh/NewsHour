package com.ghostriley.sgt.ghostchat.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ghostriley.sgt.ghostchat.utils.ParseConstants;
import com.ghostriley.sgt.ghostchat.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by GhostRiley on 30-12-2015.
 */
public class FriendsFragment extends ListFragment {

    public static final String TAG=FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query=mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USER);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                mFriends = friends;

                if (e == null) {
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;

                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter adapter = new ArrayAdapter(getListView().getContext(), android.R.layout.simple_list_item_1, usernames);
                    setListAdapter(adapter);



                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String[] username = new String[mFriends.size()];
        String[] userId = new String[mFriends.size()];
        int i = 0;

        for (ParseUser user : mFriends) {
            userId[i] = user.getObjectId();
            username[i]=user.getUsername();
            i++;
        }

        Intent intent=new Intent(getContext(), SendTextActivity.class);
        intent.putExtra("Name", username[position]);
        intent.putExtra("ID", userId[position]);
        startActivity(intent);

    }
}