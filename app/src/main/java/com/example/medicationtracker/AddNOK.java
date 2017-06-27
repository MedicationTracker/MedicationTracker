package com.example.medicationtracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Ryan on 27/6/2017.
 */

public class AddNOK extends Activity implements View.OnClickListener {

    private EditText NOK_name, NOK_number;
    private Button accept_edit;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nok);
        NOK_name = (EditText)findViewById(R.id.input_name);
        NOK_number = (EditText)findViewById(R.id.input_number);
        accept_edit = (Button)findViewById(R.id.accept_edit);
        accept_edit.setOnClickListener(this);
    } // end of onCreate

    // save NOK details to database
    public void onClick(View v) {

        text = NOK_name.getText().toString();


    } // end of onClick
} // end of class
