package dd.com.myq.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
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

public class GroupActivity extends AppCompatActivity {

    public String REQUEST_GROUP_MEMBERS = "http://myish.com:10011/api/getmembers/";
    public String POST_ADD_TO_GROUP = "http://myish.com:10011/api/adduserstogroup";
    public String UPDATE_MEMBERS = "http://myish.com:10011/api/updatemembers/";

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
    ArrayList<Friend> groupmembers;
    ListView groupsListView;
    private static FriendAdapter friendAdapter;

    ProgressDialog progress;

    public int Fflag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        final String gp_id = getIntent().getStringExtra("gp_id");

        UPDATE_MEMBERS = UPDATE_MEMBERS + gp_id;

        updateGroupMembers(UPDATE_MEMBERS);

        Log.e("RETURNING S UPDATE : ", UPDATE_MEMBERS);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.e("iddddd: ", gp_id);
        REQUEST_GROUP_MEMBERS = REQUEST_GROUP_MEMBERS + gp_id;

        currentSession = new SessionManager(this);

        al_name = new ArrayList<String>();
        al_points = new ArrayList<String>();
        al_friend_id = new ArrayList<String>();
        al_friend_profilepicture = new ArrayList<String>();

        groupsListView = (ListView)findViewById(R.id.list_groups);
        groupmembers= new ArrayList<>();

        progress = new ProgressDialog(this,  ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progress.setMessage("Loading Members :) ");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);


        for(int i=0;i<3;i++){

            updateGroupMembers(UPDATE_MEMBERS);
        }

        progress.setCancelable(false);
        progress.show();

        updateGroupMembers(UPDATE_MEMBERS);

        HashMap<String, String> user_details = currentSession.getUserDetails();
        final String user_id = user_details.get(SessionManager.KEY_UID);
        final String user_email = user_details.get(SessionManager.KEY_EMAIL);
        final String user_name = user_details.get(SessionManager.KEY_USERNAME);


        updateGroupMembers(UPDATE_MEMBERS);
        getGroupMembers(REQUEST_GROUP_MEMBERS);

        TextView findFriends = (TextView)findViewById(R.id.join_group);
        findFriends.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                AddToGroup(user_id, gp_id, user_name,user_email,"",0);

            }
        });
    }

    public void updateGroupMembers(String UPDATE_MEMBERS){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getApplicationContext(), UPDATE_MEMBERS , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {

                Log.e("SUCCESS CALLED ", "INSIDEE UPDATEEE");


            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                super.onFailure(statusCode, headers, throwable, object);

                Toast.makeText(getApplicationContext(), "Error Loading group members", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getGroupMembers(String REQUEST_GROUP_MEMBERS){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, REQUEST_GROUP_MEMBERS , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {

                try {
                    JSONObject responseObject = responseArray.getJSONObject(0);
                    JSONArray membersArray = responseObject.getJSONArray("memberList");

                    for(int j=0;j<membersArray.length();j++){

                        JSONObject object = membersArray.getJSONObject(j);

                        String uid = object.getString("uid");

                        String username = object.getString("username");

                        Log.e("username : ",username);

                        String points = object.getString("points");

                        Log.e("points : ",points);

                        al_points.add(String.valueOf(points));
                        al_name.add(username);
                        al_friend_id.add(uid);
                        al_friend_profilepicture.add("");

                    }


                }catch (JSONException e) {
                    e.printStackTrace();
                }

                progress.dismiss();


                for(int i=0;i<al_name.size();i++){

                    groupmembers.add(new Friend(al_name.get(i), al_points.get(i)));

                }

                friendAdapter= new FriendAdapter(groupmembers,getApplicationContext());
                groupsListView.setAdapter(friendAdapter);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Error Loading Friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void AddToGroup(String userid, String groupid, String username, String emailaddress, String profilePicture, int points){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("userid", userid);
        requestParams.put("groupid", groupid);
        requestParams.put("username", username);
        requestParams.put("emailaddress", emailaddress);
        requestParams.put("profilepictureURL", profilePicture);
        requestParams.put("points", 0);


        client.post(getApplicationContext(), POST_ADD_TO_GROUP , requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.e("ResponsePoint Success",response.toString());

                Toast.makeText(getApplicationContext(), "Successfuly added to this group", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Log.e("ResponsePoint Error",errorResponse.toString());
                Toast.makeText(getApplicationContext(), "Error adding to the group", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){

        finish();
        return true;
    }
}