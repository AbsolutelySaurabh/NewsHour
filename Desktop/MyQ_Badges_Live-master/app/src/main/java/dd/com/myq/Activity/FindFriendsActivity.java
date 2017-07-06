package dd.com.myq.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.CallbackManager;
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

public class FindFriendsActivity extends AppCompatActivity {

    public ArrayList<String> al_name;
    public ArrayList<String> al_points;

    public ArrayList<String> fb_al_name;
    public ArrayList<String> fb_al_points;
    public ArrayList<String> fb_al_id;

    public ArrayList<String> al_friend_id;
    public ArrayList<String> al_friend_profilepicture;

    SessionManager currentSession;
    CallbackManager callbackManager;
    View layout;
    LoginActivity l = new LoginActivity();

    int j=0;
    ArrayList<Friend> friends;
    ListView newsListView;
    private static FriendAdapter friendAdapter;

    ProgressDialog progress;

    public int Fflag = 0;

    public String REQUEST_GET_MYQ_FRIENDS = "http://myish.com:10011/api/findfriends/";
    public String POST_FRIEND_API = "http://myish.com:10011/api/addfriends";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // facebookSDKInitialize();

        setContentView(R.layout.activity_find_friends);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentSession = new SessionManager(this);

        fb_al_name = new ArrayList<String>();
        fb_al_id = new ArrayList<String>();
        fb_al_points = new ArrayList<String>();

        al_name = new ArrayList<String>();
        al_points = new ArrayList<String>();
        al_friend_id = new ArrayList<String>();
        al_friend_profilepicture = new ArrayList<String>();

        HashMap<String, String> user_details = currentSession.getUserDetails();
        final String user_id = user_details.get(SessionManager.KEY_UID);
        REQUEST_GET_MYQ_FRIENDS = REQUEST_GET_MYQ_FRIENDS + user_id;

//        POST_FB_FRIENDS_URL = POST_FB_FRIENDS_URL + user_id;

        Log.e("user id : ", REQUEST_GET_MYQ_FRIENDS);

        newsListView = (ListView)findViewById(R.id.list_find_friends);
        friends= new ArrayList<>();

//        layout =  findViewById(R.id.find_driends_prograss_bar);
//        layout.setVisibility(View.VISIBLE);

        progress = new ProgressDialog(this,  ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progress.setMessage("Loading Friends :) ");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        progress.setCancelable(false);

        progress.show();

        findMyQFriends(REQUEST_GET_MYQ_FRIENDS);

        ImageView fb_button = (ImageView)findViewById(R.id.fb_button_find_friends);
        fb_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
                int myIntValue = sp.getInt("fb_login_flag", 0);

                SharedPreferences s = getSharedPreferences("normal_login_prefs", Activity.MODE_PRIVATE);
                int IntValue = s.getInt("normal_login", 0);

                if(myIntValue ==1) {

                    Intent intent = new Intent(getApplicationContext(), Fb_Friends.class);
                    startActivity(intent);

                }else{

                    Toast.makeText(getApplicationContext(), "Login with Facebook first !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void findMyQFriends(String REQUEST_GET_MYQ_FRIENDS){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, REQUEST_GET_MYQ_FRIENDS , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {

                try {

                    for (int i = 0; i < responseArray.length(); i++) {

                        JSONObject object = responseArray.getJSONObject(i);

                        String friend_name = object.getString("username");

                        int friend_points = object.getInt("points");

                        String friendid = object.getString("_id");
                        String friendprofilepicture = object.getString("profilepicture");

                        al_points.add(String.valueOf(friend_points));
                        al_name.add(friend_name);

                        al_friend_id.add(friendid);
                        al_friend_profilepicture.add(friendprofilepicture);

                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }

                progress.hide();

                for(int i=0;i<al_name.size();i++){

                    friends.add(new Friend(al_name.get(i), al_points.get(i)));
                }

                friendAdapter= new FriendAdapter(friends,getApplicationContext());
                newsListView.setAdapter(friendAdapter);

                newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                        HashMap<String, String> user_details = currentSession.getUserDetails();
                        final String user_id = user_details.get(SessionManager.KEY_UID);

                        AddFriendToUser(user_id, al_friend_id.get(position), al_name.get(position), al_points.get(position), al_friend_profilepicture.get(position));

                        Toast.makeText(getApplicationContext(), "You are now friend with "+ al_name.get(position), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                progress.hide();
                Toast.makeText(getApplicationContext(), "Error Loading Friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void AddFriendToUser(String userid, String friendid, String friendname, String friendpoints, String friendprofilepictureURL){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("userid", userid);
        requestParams.put("friendid", friendid);
        requestParams.put("friendname", friendname);
        requestParams.put("friendpoints", friendpoints);
        requestParams.put("friendprofilepictureURL", friendprofilepictureURL);

        client.post(getApplicationContext(), POST_FRIEND_API , requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.e("ResponsePoint Success",response.toString());            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ResponsePoint Error",errorResponse.toString());
            }

        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}