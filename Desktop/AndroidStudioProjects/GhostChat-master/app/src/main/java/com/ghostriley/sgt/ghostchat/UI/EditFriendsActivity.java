package com.ghostriley.sgt.ghostchat.UI;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ghostriley.sgt.ghostchat.utils.ParseConstants;
import com.ghostriley.sgt.ghostchat.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends ListActivity {

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    public static final String TAG=EditFriendsActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser=ParseUser.getCurrentUser();
        mFriendsRelation=mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);


        ParseQuery<ParseUser> query= ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USER);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter adapter = new ArrayAdapter(EditFriendsActivity.this, android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);

                    addFriendsCheckMark();
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }



    protected void onListItemClick (ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);

        if(getListView().isItemChecked(position)) {
            mFriendsRelation.add(mUsers.get(position));
        }
        else {
            mFriendsRelation.remove(mUsers.get(position));
        }

        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

    }
    private void addFriendsCheckMark() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if(e==null) {
                    for(int i=0; i<mUsers.size(); i++){
                        ParseUser user=mUsers.get(i);
                        for (ParseUser friend : friends) {
                            if(friend.getObjectId().equals(user.getObjectId())) {
                                getListView().setItemChecked(i, true);
                            }
                        }
                    }
            }
                else {
                Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}

