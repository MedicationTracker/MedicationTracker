package com.example.medicationtracker;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import static com.example.medicationtracker.R.id.set_time;

// changes

public class MainActivity extends AppCompatActivity {

    // instantiate variables
    Button btn_medications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign variables
        // btn_medications = (Button) findViewById(R.id.my_medications);

    }

    public void onClickMedications(View v) {
        Intent i = new Intent(this, PrescriptionListActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        return true;
    }
}
