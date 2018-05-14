package com.example.medicationtracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.medicationtracker.objects.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

// changes

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    public static final String KEY_TIMESTAMP = "TIMESTAMP";

    GregorianCalendar cal;
    GregorianCalendar temp; //used to avoid halfway-changes in cal
    EditText et_next_appt, et_next_appt_date;
    CountDownTimer timer;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_next_appt = (EditText) findViewById(R.id.et_next_appt);
        et_next_appt_date = (EditText) findViewById(R.id.et_next_appt_date);

        long timestamp = getTimestamp();
        cal = new GregorianCalendar();
        cal.setTimeInMillis(timestamp);
        temp = new GregorianCalendar();
        temp.setTimeInMillis(timestamp);

        et_next_appt_date.setText(simpleDateFormat.format(cal.getTime()));
        startCountdown(timestamp);
    }

    public void startCountdown(long timestamp) {
        if (timer != null) {
            timer.cancel();
        }
        long time_left = timestamp - System.currentTimeMillis();

        timer = new CountDownTimer(time_left, 1000) {

            public void onTick(long millisUntilFinished) {
                et_next_appt.setText(secondsToString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                et_next_appt.setText("@string/label_missed_appt");
            }
        }.start();
    }

    public void storeTimestamp(long timestamp) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(KEY_TIMESTAMP, timestamp);
        editor.apply();
    }

    public long getTimestamp() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        long default_value = System.currentTimeMillis();
        long timestamp = sharedPref.getLong(KEY_TIMESTAMP, default_value);
        return timestamp;
    }

    public void onEditClicked(View v) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, this, year, month, day);
        dpd.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        temp.set(Calendar.YEAR, year);
        temp.set(Calendar.MONTH, month);
        temp.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        TimePickerDialog tpd = new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_DARK, this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        tpd.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        temp.set(Calendar.HOUR_OF_DAY, hourOfDay);
        temp.set(Calendar.MINUTE, minute);
        Utility.zeroToMinute(temp);

        long timestamp = temp.getTimeInMillis();
        cal.setTimeInMillis(timestamp);
        storeTimestamp(timestamp);

        et_next_appt_date.setText(simpleDateFormat.format(cal.getTime()));
        startCountdown(timestamp);
    }

    public void onClickMedications(View v) {
        Intent i = new Intent(this, PrescriptionListActivity.class);
        startActivity(i);
    }

    public void onClickHistory(View v) {
        Intent i = new Intent(this, HistoryActivity.class);
        startActivity(i);
    }

    // inflate settings menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SessionManager sm = new SessionManager(this);
        if(sm.isLoggedIn()) {
            menu.findItem(R.id.menu_login).setVisible(false);
            menu.findItem(R.id.menu_logout).setVisible(true);
        } else {
            menu.findItem(R.id.menu_login).setVisible(true);
            menu.findItem(R.id.menu_logout).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch(item.getItemId()) {
            case R.id.menu_login:
                i = new Intent(this, LoginActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_logout:
                SessionManager sm = new SessionManager(this);
                sm.setLogin(false);
                invalidateOptionsMenu();
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    public static String secondsToString(long seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hour = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        return day + " days " + hour + " hours " + minute + " minutes " + second + " seconds";
    }
}
