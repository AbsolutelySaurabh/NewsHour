package com.ghostriley.sgt.ghostchat.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ghostriley.sgt.ghostchat.R;
import com.ghostriley.sgt.ghostchat.utils.ParseConstants;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;

public class SendTextActivity extends AppCompatActivity {

    public static final String TAG=ChooseRecipientsActivity.class.getSimpleName();

    protected EditText mMessageText;
    protected Button mSendButton;

    protected ParseUser mRecieverID;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    protected TextView mRecieverName;
    protected String mRecieverId;
    protected String mRecieverUsername;
    protected String mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_text);

        mRecieverName=(TextView)findViewById(R.id.recieverId);

        mMessageText=(EditText)findViewById(R.id.messageText);
        mSendButton=(Button)findViewById(R.id.button);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                mRecieverId = null;
                mRecieverUsername=null;
            } else {
                mRecieverId = extras.getString("ID");
                mRecieverUsername = extras.getString("Name");

            }
        } else {
            mRecieverId = (String) savedInstanceState.getSerializable("ID");
            mRecieverUsername = (String) savedInstanceState.getSerializable("Name");
        }

        mRecieverName.setText(" "+ mRecieverUsername);


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMessageText.getText().toString().equals(null)) {
                    Toast.makeText(SendTextActivity.this, "Please type a message", Toast.LENGTH_SHORT).show();
                } else {
                    mMessage=mMessageText.getText().toString();
                    sendPushNotifications();
                }
            }
        });
    }
    protected ArrayList<String> getRecipientsIds() {
        ArrayList<String> recipient= new ArrayList<String>();
        recipient.add(mRecieverId);
        return recipient;
    }

    protected void sendPushNotifications() {

        ParseQuery<ParseInstallation> query= ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientsIds());

        ParsePush push=new ParsePush();
        push.setQuery(query);
        push.setMessage(ParseUser.getCurrentUser().getUsername().toString() + ": " + mMessage);
        push.sendInBackground();
        Toast.makeText(SendTextActivity.this, "Message sent!", Toast.LENGTH_LONG).show();
        Intent intent=new Intent(SendTextActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
