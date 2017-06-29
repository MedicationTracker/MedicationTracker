package com.example.medicationtracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.medicationtracker.services.AlarmService.KEY_ID;



public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("tag111", "(in AlarmReceiver)");

        long id = (long) intent.getExtras().get(KEY_ID);

        Intent i = new Intent();
        i.setClassName("com.example.medicationtracker", "com.example.medicationtracker.AlarmScreen");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(KEY_ID, id);

        context.startActivity(i);
    }
}


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

        // set next alarm
        //setAlarms(context, request_code);
        */