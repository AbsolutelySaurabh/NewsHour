package com.ghostriley.sgt.ghostchat;

import android.app.Application;
import android.util.Log;

import com.ghostriley.sgt.ghostchat.UI.MainActivity;
import com.ghostriley.sgt.ghostchat.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseAnalytics;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;


/**
 * Created by GhostRiley on 28-12-2015.
 */
public class GhostChatApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();

        Parse.initialize(this, "P0zIagKQvEGdopuoLZuucgzC7H4oz64U7GZkGe1n", "mT15MV8OFEMTDNQaGU5XdLHTXMsNxnUxJdXVp4O3");
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    public static void updateParseInstallation(ParseUser user) {

        ParseInstallation installation=ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

}
