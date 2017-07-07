package com.example.medicationtracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Ryan on 27/6/2017.
 */

public class Settings extends Activity implements View.OnClickListener {

    private Button add_NOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.menu.layout_settings);
        add_NOK = (Button) findViewById(R.id.add_NOK);

        add_NOK.setOnClickListener(this);
    } // end of onCreate

    public void onClick(View v) {



    } // end of onClick
} // end of class
