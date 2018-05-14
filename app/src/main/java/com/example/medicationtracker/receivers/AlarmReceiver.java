package com.example.medicationtracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.medicationtracker.Utility;

import static android.R.attr.id;
import static com.example.medicationtracker.services.AlarmService.KEY_ID;
import static com.example.medicationtracker.services.AlarmService.KEY_TIMESTAMP;

/**
 * Starts AlarmScreen Activity
 * Transfers id and timestamp
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Utility.TAG, "Inside AlarmReceiver/onReceive");

        long id = (long) intent.getExtras().get(KEY_ID);
        long timestamp = (long) intent.getExtras().get(KEY_TIMESTAMP);

        Log.d(Utility.TAG, "Inside AlarmReceiver/onReceive: Drug id: " + id + ", timestamp: " + timestamp);

        Intent i = new Intent();
        i.setClassName("com.example.medicationtracker", "com.example.medicationtracker.AlarmScreen");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(KEY_ID, id);
        i.putExtra(KEY_TIMESTAMP, timestamp);

        context.startActivity(i);
    }
}