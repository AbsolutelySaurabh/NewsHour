package com.ghostriley.sgt.ghostchat.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ghostriley.sgt.ghostchat.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class RecoverPasswordActivity extends AppCompatActivity {

    protected EditText mEmail;
    protected Button mReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        mEmail=(EditText)findViewById(R.id.emailID);
        mReset=(Button)findViewById(R.id.reset);

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {;
                ParseUser.requestPasswordResetInBackground(mEmail.getText().toString(), new RequestPasswordResetCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            ParseUser.requestPasswordResetInBackground(mEmail.toString());
                            Toast.makeText(RecoverPasswordActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(RecoverPasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(RecoverPasswordActivity.this,
                                    "Please make sure your Email is correct and registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        }


        });
    }
}
