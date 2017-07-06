package dd.com.myq.Activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

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

public class SignUpActivity extends AppCompatActivity {

    private EditText Name, Email, Password;
    private ProgressDialog progress;
    private LoginButton fb_signup;
    private  CallbackManager callbackManager;
    private SessionManager sessionManager;

    String str="";
    JSONObject datafb=new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_sign_up);

        sessionManager = new SessionManager(this);

        progress = new ProgressDialog(this);
        progress.setMessage("Signing up...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);

        Name = (EditText) findViewById(R.id.full_name);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        ImageButton signup = (ImageButton) findViewById(R.id.signup);
        TextView login = (TextView) findViewById(R.id.no_account);

        fb_signup = (LoginButton) findViewById(R.id.facebook_signup);
        fb_signup.setReadPermissions(Arrays.asList("email", "user_birthday"));



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


        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        if(!isInternetPresent) {
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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();
                String name = Name.getText().toString().trim();

                if(email.equals("") ){
                    Email.setError( "Email is required!" );
                }
                else if(password.equals("") ){
                    Password.setError( "Password is required!" );
                }
                else if(name.equals("") ){
                    Name.setError( "Name is required!" );
                }
                else if (!email.equals("") && !password.equals("") && !name.equals(""))
                {
                    if(isValidEmail(email))
                    {
                        register(email, password, name);
                    }
                    else
                    {
                        Email.setError("Please enter a valid email!");
                    }
                }
            }
        });

        fb_signup.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("status", "success");
                //requestUserProfile(loginResult);
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                String email = object.optString("email");
                                String uid = object.optString("id");
                                Log.e("response",""+response);
                                Log.e("object", "" + object);
                                HashMap<String, String> dat = new HashMap<String, String>();



                                socialfbsignup(uid, object.optString("name"), "ffff", email, "newbie", "2017/04/11", object.optString("gender"));

                                //       AsyncHttpPostFB asyncHttpPostfb = new AsyncHttpPostFB(dat);
                                //     asyncHttpPostfb.execute(Config.socialLogin);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,name,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("status","cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e("status","error");
            }
        });

    }


    public void register(String emailaddress, String password, String username){

        progress.show();

        AsyncHttpClient client = new AsyncHttpClient();

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("username", username);
            jsonParams.put("password", password);
            jsonParams.put("emailaddress", emailaddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }

        client.post(this, Config.SignUpAPIUrl , entity, "application/json", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progress.hide();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                if (responseString != null) {
                    progress.hide();

                    JSONObject jsonOnject;

                    try {
                        jsonOnject = new JSONObject(responseString);
                        int code = jsonOnject.getInt("code");

                        if(code==11000)
                        {
                            Toast.makeText(SignUpActivity.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(SignUpActivity.this, "Account Successfully Created. Login now!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                    catch(JSONException ex){
                        Toast.makeText(SignUpActivity.this, "Account Successfully Created. Login now!", Toast.LENGTH_SHORT).show();
                        Log.d("efuckrror===========",ex.toString());
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                } else {
                    progress.hide();
                    Toast.makeText(SignUpActivity.this, "Please try again later!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
//    protected void onDestroy() {
//        progress.dismiss();
//    }
//
    public void onDestroy() {
        super.onDestroy();
        //eventsData.close();
        progress.dismiss();
    }
    private class AsyncHttpPostFB extends AsyncTask<String, String, String> {
        private HashMap<String, String> mData = null;// post data

        /**
         * constructor
         */
        public AsyncHttpPostFB(HashMap<String, String> data) {
            mData = data;
        }

        /**
         * background
         */
        @Override
        protected String doInBackground(String... params) {
            byte[] result = null;
            str = "";
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(params[0]);// in this case, params[0] is URL
            Log.e("1","1");
            try {
                // set up post data
                StringEntity param = new StringEntity(datafb.toString());
                post.setEntity(param);
                ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                Iterator<String> it = mData.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
                }

                //post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
                post.setHeader("Content-Type", "application/json");
                HttpResponse response = client.execute(post);
                StatusLine statusLine = response.getStatusLine();
                Log.e("status", "" + statusLine.getStatusCode() + params[0]);
                if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
                    result = EntityUtils.toByteArray(response.getEntity());
                    str = new String(result, "UTF-8");
                    Log.e("res","res"+str);
                }
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
            }
            return str;
        }

        /**
         * on getting result
         */
        @Override
        protected void onPostExecute(String result) {
            // something...

            JSONArray reader=null;
            JSONObject reader1=null;
            JSONArray reader2=null;
            JSONArray reader4=null;
            JSONObject reader3=null;
            JSONObject reader5=null;
            String uid=null;
            try {
                reader = new JSONArray(result);
                reader1=reader.getJSONObject(0);
                uid=reader1.getString("_id");
                Log.e("Incorrect",""+uid);
                Config.uid=uid;
                if(reader1.has("postscreatedcount"))
                    Config.posts=reader1.getString("postscreatedcount");
                if(reader1.has("profilepicture")) {
                    Log.e("profilepicture",reader1.getString("profilepicture"));
                    Config.profilePic = reader1.getString("profilepicture");
                }
                if(reader1.has("followerscount")) {
                    Log.e("followerscount",reader1.getString("followerscount"));
                    Config.followersCount = reader1.getString("followerscount");
                }
                if(reader1.has("followingcount")) {
                    Log.e("followingcount",reader1.getString("followingcount"));
                    Config.followingCount = reader1.getString("followingcount");
                }
                if(reader1.has("aboutme")){
                    Log.e("aboutme",reader1.getString("aboutme"));
                    Config.aboutMe=reader1.getString("aboutme");
                }
                if(reader1.has("username")) {
                    Config.username = reader1.getString("username");
                }
                if(reader1.has("gender"))
                    Config.gender=reader1.getString("gender");
                Config.useremail=reader1.getString("emailaddress");



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
                        reader4);

                Log.e("reader4",""+reader4);
                if(reader4.length()>0){
                    reader5 = reader4.getJSONObject(0);
                    Log.e("reader5",reader5.getString("followingid"));

                    for(int i=0; i<reader4.length(); i++) {
                        reader5 = reader4.getJSONObject(i);
                        reader5.getString("followingid");
                        if(reader5.has("followingid")){
                            Config.userFollowing.add(reader5.getString("followingid"));
                        }
                    }}

                if(reader2.length()>0){
                    for(int i=0; i<reader2.length(); i++) {
                        reader3 = reader2.getJSONObject(i);
                        if(reader3.has("postimage")){
                            Config.usersPosts.add(reader3.getString("postimage"));
                        }
                    }}

                Toast.makeText(SignUpActivity.this, "Account Successfully Created. Login now!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);

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
//                               JSONArray reader2,
                               JSONArray reader4)
    {
        SharedPreferences preferences = getSharedPreferences("temp", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("uids", uid);
        editor.putString("profilePic", profilePic);
        editor.putString("followersCount", followersCount);
        editor.putString("followingCount", followingCount);
        editor.putString("aboutMe", aboutMe);
        editor.putString("aboutMe", aboutMe);
        editor.putString("username", username);
        editor.putString("gender", gender);
        editor.putString("useremail", useremail);
//        editor.putString("reader2", ""+reader2);
        editor.putString("reader4", ""+reader4);
        editor.commit();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    public void socialfbsignup(final String userid, final String username, final String profilepic, final String email, final String aboutme, final String dob, final String gender) {
        progress.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();


        requestParams.put("emailaddress", email);
        requestParams.put("password", "1234");
        requestParams.put("username", username);

        client.post(this, Config.SignUpAPIUrl , requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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
                        //  JSONObject jsonObject = response.getJSONObject(String.valueOf(i));
                        username = response.getString("username");
                        profilepicture = response.getString("profilepicture");
                        emai = response.getString("emailaddress");//////
                        _id = response.getString("_id");
                        aboutme = response.getString("aboutme");

                        dob = response.getString("dob");
                        gender = response.getString("gender");

                        sessionManager.createLoginSession(_id, username, profilepicture, emai, aboutme, dob, gender);
                        Intent i = new Intent(SignUpActivity.this, HomeActivity.class);

                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }

                    else {
                        progress.hide();
                        Toast.makeText(SignUpActivity.this, "Please check your credentials!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.hide();
                Toast.makeText(SignUpActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                Log.d("risherror",errorResponse.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progress.hide();
                Toast.makeText(SignUpActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                Log.d("risherror",responseString.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseString) {
                super.onFailure(statusCode, headers, throwable, responseString);
                progress.hide();
                Toast.makeText(SignUpActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                Log.d("risherror",responseString.toString());

            }

        });

    }


}
