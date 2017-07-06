package dd.com.myq.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;
import dd.com.myq.App.Config;
import dd.com.myq.Fragment.AccountFragment;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;

import static dd.com.myq.Util.SessionManager.KEY_UID;

public class ChangePassword extends AppCompatActivity {
    EditText newpass,confirm;

    private ProgressDialog progress;
    private Button change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = new ProgressDialog(this);
        progress.setMessage("Updating Profile...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);

    }
    public void change(View view) {
        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        Object userid = user.get(KEY_UID);
        String uid = userid.toString();

        newpass = (EditText) findViewById(R.id.newpassword);
        confirm = (EditText) findViewById(R.id.confirmpassword);
        String confirms = confirm.getText().toString();

        String strUserName = newpass.getText().toString();

        if (TextUtils.isEmpty(strUserName)) {
            newpass.setError("This field cannot be empty!!");
            return;
        } else if (TextUtils.isEmpty(confirms)) {
            confirm.setError("This field cannot be empty!!");
            return;
        }
        else if(!confirms.equals(strUserName)) {
            newpass.setText("");
            confirm.setText("");
            Toast.makeText(ChangePassword.this, "Password Mismatch", Toast.LENGTH_SHORT).show();
        }
        else
        {
            login(uid, confirms);
        }
    }

    public void login(final String userid,String password){

        progress.show();

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams requestParams = new RequestParams();
        requestParams.put("userid", userid);
        requestParams.put("password", password);

        client.post(this, Config.changepasswordAPIUrl , requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progress.hide();

                Log.e("Response Login: ", response.toString());
                Toast.makeText(ChangePassword.this, "Password Changed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.hide();
                Toast.makeText(ChangePassword.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progress.hide();
                Toast.makeText(ChangePassword.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseString) {
                super.onFailure(statusCode, headers, throwable, responseString);
                progress.hide();
                Toast.makeText(ChangePassword.this, "Error Occurred", Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
