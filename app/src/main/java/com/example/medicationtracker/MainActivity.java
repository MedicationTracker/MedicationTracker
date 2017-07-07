package com.example.medicationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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

    // inflate settings menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    // Handle setting click
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        startActivity(new Intent(this,Settings.class));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        return true;
    }
}
