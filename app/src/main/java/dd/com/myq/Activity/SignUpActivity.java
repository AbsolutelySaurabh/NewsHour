package dd.com.myq.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
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

public class SignUpActivity extends AppCompatActivity {

    private EditText Name, Email, Password;
    private ProgressDialog progress;
    private LoginButton fb_signup;
    private  CallbackManager callbackManager;

    String str="";
    JSONObject datafb=new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_sign_up);

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

                                try {
                                    datafb.put("emailaddress", email);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String tempFile=email.replace("@", "");
                                try {
                                    datafb.put("profilepicture", Config.defaultImagePrefix +tempFile+".jpg");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    datafb.put("password", uid);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    datafb.put("gender", object.optString("gender"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    datafb.put("username", object.optString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    datafb.put("fbid", object.optString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    datafb.put("source", "facebook");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    datafb.put("devicetype", "android");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    datafb.put("deviceid", Config.token);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    datafb.put("profilepicture", Config.defaultprofilepic);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                AsyncHttpPostFB asyncHttpPostfb = new AsyncHttpPostFB(dat);
                                asyncHttpPostfb.execute(Config.socialLogin);
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
                                Toast.makeText(SignUpActivity.this, "Email already exits!", Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
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
}
