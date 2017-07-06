package com.ghostriley.sgt.ghostchat.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ghostriley.sgt.ghostchat.adapters.MessageAdapter;
import com.ghostriley.sgt.ghostchat.utils.ParseConstants;
import com.ghostriley.sgt.ghostchat.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GhostRiley on 30-12-2015.
 */
public class InboxFragment extends ListFragment {

    protected List<ParseObject> mMessages;


        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
            return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveMessages();
    }

    public void retrieveMessages() {
        ParseQuery<ParseObject> query=new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENTS_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if(e==null) {
                    //successful
                    mMessages=messages;
                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }
                    if(getListView().getAdapter()==null) {
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                        setListAdapter(adapter);
                    }
                    else {
                        //refill adapter
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }
                }
                else {

                }

            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE_NAME);

        Uri fileUri = Uri.parse(file.getUrl());
        if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
            Intent intent=new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);

        }
        else {
            Intent intent=new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }
        //Delete message
        List<String> ids=message.getList(ParseConstants.KEY_RECIPIENTS_IDS);
        if(ids.size()==1){
            //delete
            message.deleteInBackground();
        }
        else {
            //remove user
            ids.remove(ParseUser.getCurrentUser().getObjectId());
            ArrayList<String> idsToRemove=new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
            message.removeAll(ParseConstants.KEY_RECIPIENTS_IDS, idsToRemove);
            message.saveInBackground();
        }
    }
}









