package com.ghostriley.sgt.ghostchat.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ghostriley.sgt.ghostchat.GhostChatApplication;
import com.ghostriley.sgt.ghostchat.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import static com.ghostriley.sgt.ghostchat.GhostChatApplication.updateParseInstallation;

public class LoginActivity extends AppCompatActivity {

    protected TextView mSignUpTextView;
    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected TextView mForgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mForgotPasswordText=(TextView)findViewById(R.id.forgotPasswordText);
        mForgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0=new Intent(LoginActivity.this, RecoverPasswordActivity.class);
                startActivity(intent0);
            }
        });

        mSignUpTextView=(TextView)findViewById(R.id.signUpText);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        mUsername=(EditText)findViewById(R.id.usernameField);
        mPassword=(EditText)findViewById(R.id.passwordField);
        mLoginButton=(Button)findViewById(R.id.loginButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                username = username.trim();
                password = password.trim();

                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Make sure you enter username, password")
                            .setTitle("Error")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    setProgressBarIndeterminateVisibility(true);
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override

                        public void done(ParseUser parseUser, ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null) {
                                //update Parse Installations

                                GhostChatApplication.updateParseInstallation(parseUser);

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle("Error")
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        }
                    });
                }
            }
        });
    }
}
