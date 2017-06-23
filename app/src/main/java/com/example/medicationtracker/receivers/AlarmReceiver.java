package com.example.medicationtracker.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.medicationtracker.database.DatabaseOpenHelper;
import com.example.medicationtracker.objects.Alarm_Ringtone;
import com.example.medicationtracker.objects.ConsumptionInstance;
import com.example.medicationtracker.objects.Prescription;

import java.util.Calendar;

import static com.example.medicationtracker.PrescriptionListActivity.getAlarmIntent;
import static com.example.medicationtracker.Utility.formatInt;

/**
 * Created by Ryan on 22/6/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    DatabaseOpenHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {

        // get info to pass to AlarmScreen Activity
        long request_code = (long) intent.getExtras().get("REQUEST_CODE");
        String timings = (String) intent.getExtras().get("TIMINGS_KEY");

        // fetch Prescription
        db = DatabaseOpenHelper.getInstance(context);
        Prescription p = db.getPrescription(request_code);
        db.close();

        // obtain next ConsumptionInstance for this Prescription
        // WARNING: generateConsumptionInstance seems to be not working as expected. thus get(1)
        ConsumptionInstance next_instance = p.generateConsumptionInstances(0).get(1);
        Calendar cal = next_instance.getConsumptionTime();

        // convert to a string HHMM
        String next_timing = formatInt(cal.get(Calendar.HOUR_OF_DAY), 2) + formatInt(cal.get(Calendar.MINUTE), 2);

        // fire off the pending intent
        PendingIntent next_alarm_intent = getAlarmIntent(context, request_code, next_timing);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), next_alarm_intent);
        Log.d("tag111", "(in receiver) alarm set for " + next_timing);

        // debug log statement
        Log.d("tag111", "request code is : " + request_code + " alarm manager is " + (next_alarm_intent == null)
         + " timing is : " + next_timing);

        // start AlarmScreen Activity
        Intent i = new Intent();
        i.setClassName("com.example.medicationtracker", "com.example.medicationtracker.AlarmScreen");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("REQUEST_CODE", request_code);
        i.putExtra("TIMINGS_KEY", timings);
        context.startActivity(i);
    }

}
