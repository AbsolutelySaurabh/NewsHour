package com.ghostriley.sgt.ghostchat.UI;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ghostriley.sgt.ghostchat.R;
import com.ghostriley.sgt.ghostchat.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SendMessageActivity extends ListActivity {

    public static final String TAG=ChooseRecipientsActivity.class.getSimpleName();


    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected String mMessage;

    protected MenuItem mSendMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        setupActionBar();

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mMessage=getIntent().getExtras().getString(ParseConstants.TYPE_TEXT);
    }

    private void setupActionBar() {
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_message, menu);
        mSendMenuItem=menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_send:
                sendPushNotifications();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected ArrayList<String> getRecipientsIds() {
        ArrayList<String> recipientsIds = new ArrayList<String>();
        for (int i=0; i<getListView().getCount(); i++) {
            if(getListView().isItemChecked(i)) {
                recipientsIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientsIds;
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
                    ArrayAdapter adapter = new ArrayAdapter(SendMessageActivity.this, android.R.layout.simple_list_item_checked, usernames);
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(l.getCheckedItemCount()>0) {
            mSendMenuItem.setVisible(true);
        }
        else {
            mSendMenuItem.setVisible(false);
        }
    }
    protected void sendPushNotifications() {

        ParseQuery<ParseInstallation> query= ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientsIds());

        ParsePush push=new ParsePush();
        push.setQuery(query);
        push.setMessage(ParseUser.getCurrentUser().getUsername().toString() + ": " + mMessage);
        push.sendInBackground();
        Toast.makeText(SendMessageActivity.this, "Message sent!", Toast.LENGTH_LONG).show();
        Intent intent=new Intent(SendMessageActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
