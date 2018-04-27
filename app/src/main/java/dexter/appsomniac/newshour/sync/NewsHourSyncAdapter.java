package dexter.appsomniac.newshour.sync;

/**
 * Created by Saurabh on 27-08-2017.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import cz.msebera.android.httpclient.Header;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import dexter.appsomniac.newshour.activity.MainActivity;
import dexter.appsomniac.newshour.data.NewsDbHelper;
import dexter.appsomniac.newshour.config.Config;
import dexter.appsomniac.newshour.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsHourSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = NewsHourSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the news, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 600 * 1000;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 15;
    private static final int NEWS_HEAD_NOTIFICATION_ID = 3004;
    private static final int NEWS_TECH_NOTIFICATION_ID = 3005;

    public static String GET_NEWS_URL_TOI = "", GET_NEWS_URL_CNN = "", GET_NEWS_URL_HINDU = "",
            GET_NEWS_URL_GUARDIAN = "", GET_NEWS_URL_TechCrunch = "", GET_NEWS_URL_HackerNews = "", GET_NEWS_URL_TechRadar = "";

    Context context;
    public static int techNews_table_item_index = 0;
    private NewsDbHelper newsDbHelper;

    public NewsHourSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        makeUrls();
        erasePreviousData();

        buildUrl(GET_NEWS_URL_HINDU, 0);
        buildUrl(GET_NEWS_URL_TOI, 0);
        buildUrl(GET_NEWS_URL_TechRadar, 1);
        buildUrl(GET_NEWS_URL_GUARDIAN, 0);
        buildUrl(GET_NEWS_URL_CNN, 0);
        buildUrl(GET_NEWS_URL_TechCrunch, 1);
        buildUrl(GET_NEWS_URL_HackerNews, 1);

    }

    private void erasePreviousData(){
        newsDbHelper = new NewsDbHelper(getContext());
        newsDbHelper.deleteAllRecordHead();
        newsDbHelper.deleteAllRecordTech();
    }

    private void makeUrls(){
        GET_NEWS_URL_TOI = Config.TOI_URL + Config.API_KEY;
        GET_NEWS_URL_CNN = Config.CNN_URL + Config.API_KEY ;
        GET_NEWS_URL_HINDU = Config.HINDU_URL + Config.API_KEY;
        GET_NEWS_URL_GUARDIAN = Config.THEGUARDIAN_URL + Config.API_KEY;
        GET_NEWS_URL_TechCrunch = Config.TECHCRUNCH_URL + Config.API_KEY;
        GET_NEWS_URL_TechRadar = Config.TECHRADAR_URL + Config.API_KEY ;
        GET_NEWS_URL_HackerNews= Config.HACKERNEWS_URL + Config.API_KEY;
    }

    private void buildUrl(String NEWS_REQUEST_URL, int flag){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {

            Uri builtUri = Uri.parse(NEWS_REQUEST_URL);

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            forecastJsonStr = buffer.toString();
            if(flag==0) {
                getNewsFromJSON(forecastJsonStr, 0);
            }else
                if(flag==1){

                    getNewsFromJSON(forecastJsonStr, 1);
                }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;

    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getNewsFromJSON(String forecastJsonStr, int another_flag) throws JSONException {

        try {
            SQLiteDatabase db = newsDbHelper.getReadableDatabase();
            Cursor rs = null;
            if(another_flag == 0) {
                 rs = db.rawQuery("SELECT * FROM headlineNews", null);
            }else
                if(another_flag == 1){
                     rs = db.rawQuery("SELECT * FROM techNews", null);
                }

            JSONObject forecastJson = new JSONObject(forecastJsonStr);

            JSONArray responseArray = forecastJson.getJSONArray("articles");
            for(int i=0;i<responseArray.length();i++){

                JSONObject currentNewsObject = responseArray.getJSONObject(i);

                String title = currentNewsObject.getString("title");
                String description = currentNewsObject.getString("description");
                String url = currentNewsObject.getString("url");
                String urlToImage = currentNewsObject.getString("urlToImage");
                String publishedAt = currentNewsObject.getString("publishedAt");

                if(description.length()>5) {

                    int flag = 0;
                    if (rs.moveToFirst()) {

                        while (!rs.isAfterLast()) {

                            String title_in_db = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_TITLE));

                            if (title_in_db.equals(title)) {

                                flag = 1;
                            }
                            rs.moveToNext();
                        }
                    }
                    if (flag == 0) {

                        if(another_flag == 0) {
                            newsDbHelper.insertNewsHead(title, description, url, urlToImage, publishedAt, 0);
                        }else
                            if(another_flag == 1){

                                newsDbHelper.insertNewsTech(title, description, url, urlToImage, publishedAt, 0, techNews_table_item_index);
                                techNews_table_item_index++;
                            }
                    }
                }
                Log.d("QUESTIONS: "+ String.valueOf(i), title);
            }
            notifyNewsTech();
            notifyNewsHead();

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    public void notifyNewsHead(){

        context = getContext();

                SQLiteDatabase db = newsDbHelper.getReadableDatabase();
                Cursor rs = db.rawQuery("SELECT * FROM headlineNews", null);

        if (rs.moveToFirst()) {

                String title = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_TITLE));
                String description = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_DESC));
                // String urlToImage = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_URLTOIMAGE));

                Log.e("Notification title: ", title);

                Resources resources = getContext().getResources();
                Bitmap largeIcon = BitmapFactory.decodeResource(resources,
                        R.mipmap.ic_launcher);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getContext())
                                .setColor(resources.getColor(R.color.white))
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(largeIcon)
                                .setContentTitle(title)
                                .setContentText(description);

                Intent resultIntent = new Intent(context, MainActivity.class);
                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager =
                        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(NEWS_HEAD_NOTIFICATION_ID, mBuilder.build());

                //rs.moveToNext();
                rs.close();

        }

    }

    public void notifyNewsTech(){

        context = getContext();

        SQLiteDatabase db = newsDbHelper.getReadableDatabase();
                Cursor rs = db.rawQuery("SELECT * FROM techNews", null);

                if (rs.moveToFirst()) {

                        String title = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_TITLE));
                        String description = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_DESC));
                        // String urlToImage = rs.getString(rs.getColumnIndex(NewsDbHelper.COLUMN_NEWS_URLTOIMAGE));

                        Resources resources = context.getResources();
                        Bitmap largeIcon = BitmapFactory.decodeResource(resources,
                                R.mipmap.ic_launcher);

                        String no_title = context.getString(R.string.app_name);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getContext())
                                        .setColor(resources.getColor(R.color.colorPrimary))
                                        .setSmallIcon(R.mipmap.ic_launcher)

                                        .setLargeIcon(largeIcon)
                                        .setContentTitle(title)
                                        .setContentText(description);


                        // Make something interesting happen when the user clicks on the notification.
                        // In this case, opening the app is sufficient.
                        Intent resultIntent = new Intent(context, MainActivity.class);
                        // The stack builder object will contain an artificial back stack for the
                        // started Activity.
                        // This ensures that navigating backward from the Activity leads out of
                        // your application to the Home screen.
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );

                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager mNotificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        // NEWS_NOTIFICATION_ID allows you to update the notification later on.

                        mNotificationManager.notify(NEWS_TECH_NOTIFICATION_ID, mBuilder.build());

                        //rs.moveToNext();
                        rs.close();

                    }

    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        NewsHourSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}