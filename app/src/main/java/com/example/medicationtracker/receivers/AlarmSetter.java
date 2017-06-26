package com.example.medicationtracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.medicationtracker.services.AlarmService;

import static android.R.attr.start;


/**
 * Created by Jia Hao on 6/25/2017.
 */

public class AlarmSetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("tag111", "(in AlarmSetter)");
        Intent i = new Intent(context, AlarmService.class);
        i.putExtra("REQUEST_CODE", "ALL");
        i.setAction(AlarmService.CREATE);
        context.startService(i);
    }
}
