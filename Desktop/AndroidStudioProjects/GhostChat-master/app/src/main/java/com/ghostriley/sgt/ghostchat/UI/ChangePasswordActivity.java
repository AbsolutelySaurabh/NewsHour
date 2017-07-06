package com.ghostriley.sgt.ghostchat.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ghostriley.sgt.ghostchat.R;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ChangePasswordActivity extends AppCompatActivity {


    protected EditText mCurrentUsername;
    protected EditText mCurrentPassword;
    protected Button mContinueButton;
    protected String mCurrentUser;
    protected EditText mNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        mCurrentUsername =(EditText)findViewById(R.id.usernameField);
        mCurrentPassword =(EditText)findViewById(R.id.passwordField);
        mContinueButton =(Button)findViewById(R.id.ContinueButton);
        mNewPassword=(EditText)findViewById(R.id.newPasswordField);
        mCurrentUser=ParseUser.getCurrentUser().getUsername();

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mCurrentUsername.getText().toString();
                String password = mCurrentPassword.getText().toString();
                final String newPassword=mNewPassword.getText().toString();

                username = username.trim();
                password = password.trim();

                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                    builder.setMessage("Make sure you enter username, password")
                            .setTitle("Error")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    if(username.equals(mCurrentUser)) {
                        ParseUser.logInInBackground(username, password, new LogInCallback() {
                            @Override

                            public void done(ParseUser parseUser, ParseException e) {
                                if (e == null) {

                                    ParseUser.getCurrentUser().setPassword(newPassword);
                                    parseUser.saveInBackground();
                                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                                    builder.setMessage(e.getMessage())
                                            .setTitle("Error")
                                            .setPositiveButton(android.R.string.ok, null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }

                            }
                        });

                    }
                    else {
                        Toast.makeText(ChangePasswordActivity.this, "Enter the username logged in!", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }
}
