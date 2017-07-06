package dd.com.myq.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dd.com.myq.Fragment.Friends.Friend;
import dd.com.myq.Fragment.Friends.FriendAdapter;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;

public class Fb_Friends extends AppCompatActivity {

    ArrayList<Friend> friends;
    ListView newsListView;
    private static FriendAdapter friendAdapter;
    public String POST_FRIEND_API = "http://myish.com:10011/api/addfriends";
    public String POST_FB_FRIENDS_URL = "http://myish.com:10011/api/facebookfriends/";

    public static  ArrayList<String> fb_al_name;
    public static ArrayList<String> fb_al_points;
    public static ArrayList<String> fb_al_id;

    SessionManager currentSession;
    ArrayList<String> al_point;
    View layout;
    ProgressDialog progress;

    public int Fbflag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb__friends);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentSession = new SessionManager(this);

        if((new LoginActivity()).fb_flag == 1 || Fbflag==1){

            Fbflag = 1;
        }

        fb_al_name = new ArrayList<String>();
        fb_al_id = new ArrayList<String>();
        fb_al_points = new ArrayList<String>();

        HashMap<String, String> user_details = currentSession.getUserDetails();
        final String user_id = user_details.get(SessionManager.KEY_UID);
//        REQUEST_GET_MYQ_FRIENDS = REQUEST_GET_MYQ_FRIENDS + user_id;

        POST_FB_FRIENDS_URL = POST_FB_FRIENDS_URL + user_id;

        Log.e("POST FB FRIENDs : ", POST_FB_FRIENDS_URL);

//        layout =  findViewById(R.id.find_fb_driends_prograss_bar);
//        layout.setVisibility(View.VISIBLE);

        progress = new ProgressDialog(this,  ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progress.setMessage("Loading Friends :) ");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        progress.setCancelable(false);

        progress.show();

        PostRawFriends();

    }

    public void PostRawFriends() {

        AsyncHttpClient client = new AsyncHttpClient();

        SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        String rawFriends = sp.getString("raw_friends", null);

        RequestParams requestParams = new RequestParams();
        requestParams.put("data", rawFriends);

        client.post(getApplicationContext(), POST_FB_FRIENDS_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                Log.e("FB POST response : ", String.valueOf(response));

                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject object = response.getJSONObject(i);

                        String username = object.getString("username");

                        String points = String.valueOf(object.getInt("points"));

                        String friendid = object.getString("_id");

                        fb_al_name.add(username);
                        fb_al_points.add(points);
                        fb_al_id.add(friendid);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                progress.hide();

                Log.d("fb name size ", String.valueOf(fb_al_name.size()));
                Log.d("fb points ", String.valueOf(fb_al_points.size()));
                Log.d("fb id ", String.valueOf(fb_al_points.size()));

                newsListView = (ListView) findViewById(R.id.list_find_fb_friends);
                friends = new ArrayList<>();

                for (int i = 0; i < (fb_al_name.size()); i++) {

                    friends.add(new Friend(fb_al_name.get(i),fb_al_points.get(i)));
                }

                friendAdapter = new FriendAdapter(friends, getApplicationContext());
                newsListView.setAdapter(friendAdapter);

                newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                        HashMap<String, String> user_details = currentSession.getUserDetails();
                        final String user_id = user_details.get(SessionManager.KEY_UID);

                        AddFriendToUser(user_id, fb_al_id.get(position), fb_al_name.get(position), fb_al_points.get(position), "");

                        Toast.makeText(getApplicationContext(), "You are now friend with " + (fb_al_name.get(position))
                                , Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.hide();

                Log.e("ResponsePoint Error", errorResponse.toString());
            }
        });
    }

    public void AddFriendToUser(String userid, String friendid, String friendname, String friendpoints, String friendprofilepictureURL) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("userid", userid);
        requestParams.put("friendid", friendid);
        requestParams.put("friendname", friendname);
        requestParams.put("friendpoints", friendpoints);
        requestParams.put("friendprofilepictureURL", friendprofilepictureURL);

        client.post(getApplicationContext(), POST_FRIEND_API, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.e("ResponsePoint Success", response.toString());
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ResponsePoint Error", errorResponse.toString());
            }

        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}