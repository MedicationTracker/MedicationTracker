package com.example.medicationtracker;

import android.app.AlarmManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

// changes

public class MainActivity extends AppCompatActivity {

    // instantiate variables
    Button btn_meds;
    AlarmManager alarm_manager;
    TimePicker time_picker;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign variables
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        time_picker = (TimePicker) findViewById(R.id.timePicker);
        calendar = Calendar.getInstance();
        btn_meds = (Button) findViewById(R.id.my_medications);
        btn_meds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMedications(v);
            }
        });

        // initialise buttons
        Button set_time = (Button) findViewById(R.id.set_time);
        Button unset_time = (Button) findViewById(R.id.unset_time);

        // onclicklistener to set alarm
        set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(this, "Hi", Toast.LENGTH_SHORT);
            }
            });

    }

    public void onClickMedications(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu,menu);
        return true;
    }
}
