package com.example.medicationtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    // test commit again haha

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
    starts the PrescriptionListActivity
    used in button_medications onClick
     */
    public void onClickMedications(View v) {
        Intent intent = new Intent(this, PrescriptionListActivity.class);
        startActivity(intent);
    }
}