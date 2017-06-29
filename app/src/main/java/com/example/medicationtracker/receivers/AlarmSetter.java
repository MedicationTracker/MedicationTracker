package com.example.medicationtracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.medicationtracker.Utility;

import static com.example.medicationtracker.Utility.setAllAlarms;



public class AlarmSetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Utility.TAG, "(entry AlarmSetter)");

        setAllAlarms(context);

        Log.d(Utility.TAG, "(exit AlarmSetter)");
    }
}
