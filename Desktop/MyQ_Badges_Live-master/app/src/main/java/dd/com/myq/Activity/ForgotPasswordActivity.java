package dd.com.myq.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import dd.com.myq.App.Config;
import dd.com.myq.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText email;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        progress = new ProgressDialog(this);
        progress.setMessage("Sending email...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);

        email = (EditText) findViewById(R.id.email);
        ImageButton send = (ImageButton) findViewById(R.id.send_password);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_text = email.getText().toString().trim();
                if(!email_text.equals("")){

                    if(isValidEmail(email_text))
                    {
                        updatePassword(email_text);
                    }
                    else
                    {
                        email.setError("Please enter a valid email!");
                    }
                }
                else{
                    email.setError("Email is required!");
                }
            }
        });
    }


    public void updatePassword(String email){

        progress.show();

        AsyncHttpClient client = new AsyncHttpClient();

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("emailaddress", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(this, Config.ForgotPasswordAPIUrl , entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.e("Resp Forgot Pass: ", response.toString());

                try {
                    String message = response.getString("message");
                    if(message.contains("Password Updated"))
                    {
                        progress.hide();
                        Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else
                    {
                        progress.hide();
                        Toast.makeText(ForgotPasswordActivity.this, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseString) {
                super.onFailure(statusCode, headers, throwable, responseString);
                progress.hide();
                Toast.makeText(ForgotPasswordActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress.dismiss();
    }
}