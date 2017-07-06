package com.ghostriley.sgt.ghostchat.UI;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ghostriley.sgt.ghostchat.utils.FileHelper;
import com.ghostriley.sgt.ghostchat.utils.ParseConstants;
import com.ghostriley.sgt.ghostchat.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChooseRecipientsActivity extends ListActivity {

    public static final String TAG=ChooseRecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected Uri mMediaUri;
    protected String mFileType;

    protected MenuItem mSendMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_recipients);
        setupActionBar();

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mMediaUri=getIntent().getData();
        mFileType=getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

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
                ArrayAdapter adapter = new ArrayAdapter(ChooseRecipientsActivity.this, android.R.layout.simple_list_item_checked, usernames);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_recipients, menu);
        mSendMenuItem=menu.getItem(0);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_send:
                ParseObject message=createMessage();
                //send(message);
                if(message==null) {
                    //error message
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setMessage("Please select a different file.")
                            .setTitle("Error in file")
                            .setPositiveButton("Ok", null);
                    AlertDialog dailog=builder.create();
                    dailog.show();;
                }
                else {
                    send(message);
                    Toast.makeText(this, "Sending...", Toast.LENGTH_LONG).show();
                    finish();
                }
                return true;

        }

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
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
    private void setupActionBar() {

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    protected ParseObject createMessage() {
        ParseObject message=new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENTS_IDS, getRecipientsIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes= FileHelper.getByteArrayFromFile(this, mMediaUri);

        if(fileBytes==null) {
            return null;
        }

        else {
            if(mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes= FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName=FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile parseFile=new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE_NAME, parseFile);
        }
        return message;
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

    protected void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null) {
                    Toast.makeText(ChooseRecipientsActivity.this, "Message sent!", Toast.LENGTH_LONG).show();
                    sendPushNotifications();
                }
                else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChooseRecipientsActivity.this);
                    builder.setMessage("File couldn't be send.")
                            .setTitle("Error")
                            .setPositiveButton("Ok", null);
                    AlertDialog dailog=builder.create();
                    dailog.show();
                    
                }
            }
        });
    }

    protected void sendPushNotifications() {

        ParseQuery<ParseInstallation> query= ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientsIds());

        ParsePush push=new ParsePush();
        push.setQuery(query);
        push.setMessage("New media message from " + ParseUser.getCurrentUser().getUsername().toString());
        push.sendInBackground();
    }
}
