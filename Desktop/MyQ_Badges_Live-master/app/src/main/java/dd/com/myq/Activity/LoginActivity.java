package dd.com.myq.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;
import dd.com.myq.App.Config;
import dd.com.myq.R;
import dd.com.myq.Util.ConnectionDetector;
import dd.com.myq.Util.SessionManager;

import static dd.com.myq.R.id.password;

public class LoginActivity extends AppCompatActivity {

    private EditText Email, Password;
    private ProgressDialog progress;
    JSONObject datafb = new JSONObject();
    private CallbackManager callbackManager;
    String str;
    private SessionManager sessionManager;

    public static ArrayList<String> friends_list;

    public static ArrayList<String> friend_id_list;

    public static JSONArray rawFriends;

    public int fb_flag;

    private RadioButton rb1, rb2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        AppEventsLogger.activateApp(this);//////////
        sessionManager = new SessionManager(this);

        progress = new ProgressDialog(this);
        progress.setMessage("Logging in...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);

        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(password);
        ImageButton login = (ImageButton) findViewById(R.id.login);
        TextView forgotPassword = (TextView) findViewById(R.id.forgot_password);
        TextView register = (TextView) findViewById(R.id.no_account);

        LoginButton fb_login = (LoginButton) findViewById(R.id.facebook_login);
        fb_login.setReadPermissions(Arrays.asList("email","user_friends"));

        //////////////////////////////////////////////////////////////////

        CheckBox check=(CheckBox) findViewById(R.id.showhidecheck);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true)
                {
                    Password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else
                {
                    Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                Password.setSelection(Password.length());

            }
        });
        /////////////////////////////////////////////////////////////////////


        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(Config.InternetMsgTitle);
            alertDialogBuilder
                    .setMessage(Config.InternetMsg)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }
                    );
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }


        fb_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("status", "success");

                fb_flag=1;

                friends_list = new ArrayList<String>();

                friend_id_list = new ArrayList<String>();


                GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(

                        AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {

                                    JSONArray rawName = response.getJSONObject().getJSONArray("data");

                                    rawFriends = rawName;

                                    SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("raw_friends", String.valueOf(rawFriends));
                                    editor.commit();

                                    Log.e("rawwnamee ", String.valueOf(rawName));

                                    for (int l=0; l < rawName.length(); l++) {

                                        String name = (rawName.getJSONObject(l).getString("name"));
                                        Log.d("name ",name);

                                        String id = (rawName.getJSONObject(l).getString("id"));
                                        Log.d("name ",name);

                                        friends_list.add(name);

                                        friend_id_list.add(id);


                                    }

                                    Log.e("friend_list size : ", String.valueOf(friends_list.size()));
                                    Log.e("friend id list : ", String.valueOf(friend_id_list.size()));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();


                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {


                                String email = object.optString("email");
                                String uid = object.optString("id");

                                Log.e("response", "" + response);
                                Log.e("object", "" + object);
                                HashMap<String, String> dat = new HashMap<String, String>();

                                socialfb(uid, object.optString("name"), "ffff", email, "newbie", "2017/04/11", object.optString("gender"));


                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,name,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

                fb_flag = 1;

                SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("fb_login_flag", 1);
                editor.commit();

            }

            @Override
            public void onCancel() {
                Log.e("status", "cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e("status", "error" + e);
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();

                if (email.equals("")) {
                    Email.setError("Email is required!");
                } else if (password.equals("")) {
                    Password.setError("Password is required!");
                } else if (!email.equals("") && !password.equals("")) {
                    if (isValidEmail(email)) {
                        login(email, password);
                    } else {
                        Email.setError("Please enter a valid email!");
                    }
                }
            }
        });

        TextView bottom_text = (TextView) findViewById(R.id.bottom_text);
        bottom_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), TermsService.class);
                startActivity(intent);

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void login(final String email, String password) {

        progress.show();

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams requestParams = new RequestParams();
        requestParams.put("emailaddress", email);
        requestParams.put("password", password);


        client.get(this, Config.LoginAPIUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                Log.e("Response Login: ", response.toString());
                progress.hide();

                try {

                    String username = null;
                    String profilepicture = null;
                    String _id = null;
                    String emai = null;
                    String aboutme = null;
                    String dob = null;
                    String gender = null;

                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            username = jsonObject.getString("username");
                            profilepicture = jsonObject.getString("profilepicture");
                            emai = jsonObject.getString("emailaddress");//////
                            _id = jsonObject.getString("_id");
                            aboutme = jsonObject.getString("aboutme");

                            dob = jsonObject.getString("dob");
                            gender = jsonObject.getString("gender");
                        }

                        sessionManager.createLoginSession(_id, username, profilepicture, emai, aboutme, dob, gender);
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);

                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } else {
                        progress.hide();
                        Toast.makeText(LoginActivity.this, "Please check your credentials!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences sp = getSharedPreferences("normal_login_prefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("normal_login", 1);
                editor.commit();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.hide();
                Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progress.hide();
                Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseString) {
                super.onFailure(statusCode, headers, throwable, responseString);
                progress.hide();
                Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private class AsyncHttpPostFB extends AsyncTask<String, String, String> {
        private HashMap<String, String> mData = null;

        public AsyncHttpPostFB(HashMap<String, String> data) {
            mData = data;
        }

        @Override
        protected String doInBackground(String... params) {
            byte[] result = null;
            str = "";
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(params[0]);// in this case, params[0] is URL
            Log.e("1", "1");
            try {
                // set up post data
                StringEntity param = new StringEntity(datafb.toString());
                Log.e("1", "2");
                post.setEntity(param);
                Log.e("1", "3");
                ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                Iterator<String> it = mData.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
                }

                post.setHeader("Content-Type", "application/json");
                HttpResponse response = client.execute(post);
                StatusLine statusLine = response.getStatusLine();
                Log.e("status", "" + statusLine.getStatusCode() + params[0]);
                if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    result = EntityUtils.toByteArray(response.getEntity());
                    str = new String(result, "UTF-8");
                    Log.e("res", "res" + str);
                }

                Log.d("SaurabhResponse", response.toString());

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {

            JSONArray reader = null;
            JSONObject reader1 = null;
            JSONArray reader2 = null;
            JSONArray reader4 = null;
            JSONObject reader3 = null;
            JSONObject reader5 = null;
            String uid = null;
            try {
                reader = new JSONArray(result);
                reader1 = reader.getJSONObject(0);
                uid = reader1.getString("_id");
                Log.e("Incorrect", "" + uid);
                Config.uid = uid;
                if (reader1.has("postscreatedcount"))
                    Config.posts = reader1.getString("postscreatedcount");
                if (reader1.has("profilepicture")) {
                    Log.e("profilepicture", reader1.getString("profilepicture"));
                    Config.profilePic = reader1.getString("profilepicture");
                }
                if (reader1.has("followerscount")) {
                    Log.e("followerscount", reader1.getString("followerscount"));
                    Config.followersCount = reader1.getString("followerscount");
                }
                if (reader1.has("followingcount")) {
                    Log.e("followingcount", reader1.getString("followingcount"));
                    Config.followingCount = reader1.getString("followingcount");
                }
                if (reader1.has("aboutme")) {
                    Log.e("aboutme", reader1.getString("aboutme"));
                    Config.aboutMe = reader1.getString("aboutme");
                }
                if (reader1.has("username")) {
                    Config.username = reader1.getString("username");
                }
                if (reader1.has("gender"))
                    Config.gender = reader1.getString("gender");
                Config.useremail = reader1.getString("emailaddress");


//                reader2 = new JSONArray(reader1.getString("postscreated"));
                reader4 = new JSONArray(reader1.getString("following"));

                storeTempData(uid,
                        Config.profilePic,
                        Config.followersCount,
                        Config.followingCount,
                        Config.aboutMe,
                        Config.username,
                        Config.gender,
                        Config.useremail,
//                        reader2,
                        reader4,
                        Config.posts);

                Log.e("reader4", "" + reader4);
                if (reader4.length() > 0) {
                    reader5 = reader4.getJSONObject(0);
                    Log.e("reader5", reader5.getString("followingid"));

                    for (int i = 0; i < reader4.length(); i++) {
                        reader5 = reader4.getJSONObject(i);
                        reader5.getString("followingid");
                        if (reader5.has("followingid")) {
                            Config.userFollowing.add(reader5.getString("followingid"));
                        }
                    }
                }

                if (reader2.length() > 0) {
                    for (int i = 0; i < reader2.length(); i++) {
                        reader3 = reader2.getJSONObject(i);
                        if (reader3.has("postimage")) {
                            Config.usersPosts.add(reader3.getString("postimage"));
                        }
                    }
                }

                sessionManager.createLoginSession(Config.uid, Config.username, Config.profilePic, Config.useremail, Config.aboutMe, Config.dob, Config.gender);
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void storeTempData(String uid,
                               String profilePic,
                               String followersCount,
                               String followingCount,
                               String aboutMe,
                               String username,
                               String gender,
                               String useremail,
                               JSONArray reader4,
                               String posts) {
        SharedPreferences preferences = getSharedPreferences("temp", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("uids", uid);
        editor.putString("profilePic", profilePic);
        editor.putString("followersCount", followersCount);
        editor.putString("followingCount", followingCount);
        editor.putString("aboutMe", aboutMe);
        editor.putString("username", username);
        editor.putString("gender", gender);
        editor.putString("useremail", useremail);
//        editor.putString("reader2", ""+reader2);
        editor.putString("reader4", "" + reader4);
        editor.putString("posts", "" + posts);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void socialfb(final String userid, final String username, final String profilepic, final String email, final String aboutme, final String dob, final String gender) {
        progress.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();


        requestParams.put("emailaddress", email);
        requestParams.put("password", "1234");
        requestParams.put("username", username);
        requestParams.put("gender", gender);
        requestParams.put("aboutme", "newbie");
        requestParams.put("dob", "2017/04/11");
        requestParams.put("devicetype", "android");
        requestParams.put("deviceid", "1234");
        requestParams.put("source", "facebook");
        requestParams.put("profilepicture", Config.defaultprofilepic);
        requestParams.put("fbid", userid);
        requestParams.put("googleplusid", "");
        String s="http://myish.com:10011/api/sociallogin";

        client.post(this, s, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("Response Login: ", response.toString());
                progress.hide();

                try {

                    String username = null;
                    String profilepicture = null;
                    String _id = null;
                    String emai = null;
                    String aboutme = null;
                    String dob = null;
                    String gender = null;

                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            username = jsonObject.getString("username");
                            profilepicture = jsonObject.getString("profilepicture");
                            emai = jsonObject.getString("emailaddress");//////
                            _id = jsonObject.getString("_id");
                            aboutme = jsonObject.getString("aboutme");

                            dob = jsonObject.getString("dob");
                            gender = jsonObject.getString("gender");
                        }

                        sessionManager.createLoginSession(_id, username, profilepicture, emai, aboutme, dob, gender);
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);

                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } else {
                        progress.hide();
                        Toast.makeText(LoginActivity.this, "Please check your credentials!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.hide();
                Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                Log.d("risherror",errorResponse.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progress.hide();
                Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                Log.d("risherror",responseString.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseString) {
                super.onFailure(statusCode, headers, throwable, responseString);
                progress.hide();
                Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                Log.d("risherror",responseString.toString());

            }

        });

    }
}