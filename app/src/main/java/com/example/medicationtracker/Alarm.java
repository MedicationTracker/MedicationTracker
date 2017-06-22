package com.example.medicationtracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.medicationtracker.ObjectClasses.Alarm_Receiver;

/**
 * Created by Ryan on 21/6/2017.
 */

public class Alarm extends Activity {

    // instantiate variables
    AlarmManager alarm_manager;
    TimePicker time_picker;
    Context context;
    PendingIntent pending_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        this.context = this;


            // initialise variables
            alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            time_picker = (TimePicker) findViewById(R.id.timePicker);
            final Calendar calendar = Calendar.getInstance();
            final Intent alarm_receiver_intent = new Intent(this.context, Alarm_Receiver.class);

            // initialise alarm buttons
            Button set_time = (Button) findViewById(R.id.set_time);
            Button unset_time = (Button) findViewById(R.id.unset_time);

            // set alarm
            set_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // set calendar instance with time chosen
                    calendar.set(Calendar.HOUR_OF_DAY,time_picker.getHour());
                    calendar.set(Calendar.MINUTE,time_picker.getMinute());

                    int hour = time_picker.getHour();
                    int minute = time_picker.getMinute();

                    // convert time int value to string
                    String hour_string = String.valueOf(hour);
                    String minute_string = String.valueOf(minute);

                    // modify intent to recognise button press
                    alarm_receiver_intent.putExtra("extra","on");

                    // create pending intent to execute alarm in the future
                    pending_intent = PendingIntent.getBroadcast(Alarm.this,0,alarm_receiver_intent,PendingIntent.FLAG_UPDATE_CURRENT);

                    // set alarm manager
                    alarm_manager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pending_intent);
                }
            });

            // unset alarm
            unset_time.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    // cancel alarm
                    alarm_manager.cancel(pending_intent);

                    // modify intent to recognise button press
                    alarm_receiver_intent.putExtra("extra","off");

                    // stop ringtone
                    sendBroadcast(alarm_receiver_intent);



                }
            });
        }
    }
