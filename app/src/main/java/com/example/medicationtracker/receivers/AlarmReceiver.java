package com.example.medicationtracker.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.medicationtracker.database.DatabaseOpenHelper;
import static com.example.medicationtracker.PrescriptionListActivity.setAlarms;

/**
 * Created by Ryan on 22/6/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    DatabaseOpenHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {

        // get info to pass to AlarmScreen Activity
        long request_code = (long) intent.getExtras().get("REQUEST_CODE");
        String timings = (String) intent.getExtras().get("TIMING_KEY");

        /* deprecated
        // fetch Prescription
        db = DatabaseOpenHelper.getInstance(context);
        Prescription p = db.getPrescription(request_code);
        db.close();

        // obtain next ConsumptionInstance for this Prescription
        ConsumptionInstance next_instance = p.generateConsumptionInstances(0).get(0);
        Calendar cal = next_instance.getConsumptionTime();

        // convert to a string HHMM
        String next_timing = formatInt(cal.get(Calendar.HOUR_OF_DAY), 2) + formatInt(cal.get(Calendar.MINUTE), 2);

        // fire off the pending intent
        PendingIntent next_alarm_intent = getAlarmIntent(context, request_code, next_timing);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), next_alarm_intent);

        Log.d("tag111", "(in receiver) firing alarm for " + p.getDrug().getName() + " at time " + timings);
        Log.d("tag111", "(in receiver) " + p.getDrug().getName() + ": alarm set for " + next_timing);
        */

        // set next alarm
        setAlarms(context, request_code);

        // start AlarmScreen Activity
        Intent i = new Intent();
        i.setClassName("com.example.medicationtracker", "com.example.medicationtracker.AlarmScreen");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("REQUEST_CODE", request_code);
        i.putExtra("TIMINGS_KEY", timings);
        context.startActivity(i);
    }

}
