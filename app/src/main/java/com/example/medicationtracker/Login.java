package com.example.medicationtracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Ryan on 27/6/2017.
 */

public class Login extends Activity {

    private EditText input_user, input_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.menu.layout_login);
        input_password = (EditText) findViewById(R.id.input_password);
        input_user = (EditText) findViewById(R.id.input_user);
    } // end of onCreate

    // check database for user / pass match
    public void onClick(View v) {

    }

} // end of class
