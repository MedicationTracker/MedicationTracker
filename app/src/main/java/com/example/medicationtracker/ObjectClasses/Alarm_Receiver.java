package com.example.medicationtracker.ObjectClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ryan on 22/6/2017.
 */

public class Alarm_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // fetch extra string from intent
        String get_string = intent.getExtras().getString("extra");

    // create intent to ringtone service
        Intent service_intent = new Intent(context,Alarm_Ringtone.class);

        // pass extra string from MainActivity to Alarm_Ringtone
        service_intent.putExtra("extra",get_string);

        // start ringtone service
        context.startService(service_intent);

    }
}
