package com.example.medicationtracker.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.medicationtracker.database.DatabaseOpenHelper;
import com.example.medicationtracker.objects.ConsumptionInstance;
import com.example.medicationtracker.objects.Prescription;
import com.example.medicationtracker.receivers.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;

import static android.R.attr.action;
import static android.R.attr.id;
import static com.example.medicationtracker.Utility.formatInt;
import static com.example.medicationtracker.Utility.getAlarmIntent;

/**
 * AlarmService
 *
 * Set and cancel alarms through this IntentService
 *
 * How to use:
 * Create an Intent to this class
 * Put an extra for ID with key "REQUEST_CODE", or leave null to ste alarm for all Prescriptions
 * set action to be either AlarmService.CREATE or alarmService.CANCEL
 */

public class AlarmService extends IntentService {
    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";

    private IntentFilter matcher;

    public AlarmService() {
        super("MedicationTracker.AlarmService");
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("tag111", "(in AlarmService)");
        String action = intent.getAction();

        Object request_code = intent.getExtras().get("REQUEST_CODE");

        if (request_code instanceof String) {
            setAllAlarms(action);
        } else {
            setOrCancelAlarm(action, (long) request_code);
        }
    }

    public void setOrCancelAlarm(String action, long id) {
        // fetch Prescription
        DatabaseOpenHelper db = DatabaseOpenHelper.getInstance(this);
        Prescription p = db.getPrescription(id);
        db.close();

        // obtain next ConsumptionInstance for this Prescription
        ConsumptionInstance next_instance = p.generateConsumptionInstances(0).get(0);
        Calendar cal = next_instance.getConsumptionTime();

        String timings = formatInt(cal.get(Calendar.HOUR_OF_DAY), 2) + formatInt(cal.get(Calendar.MINUTE), 2);

        PendingIntent pending = getAlarmIntent(this, id, timings);
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        if (matcher.matchAction(action)) {
            if (action.equals(CREATE)) {
                am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
                Log.d("tag111", "(in AlarmService) Alarm set for " + p.getDrug().getName() + " at time " + timings);
            } else if (action.equals(CANCEL)) {
                am.cancel(pending);
                Log.d("tag111", "(in AlarmService) Alarm cancelled for " + p.getDrug().getName());
            }
        }
    }

    public void setAllAlarms(String action) {
        DatabaseOpenHelper db = DatabaseOpenHelper.getInstance(this);
        ArrayList<Prescription> prescriptions = db.getAllPrescriptions();
        db.close();

        for(Prescription p : prescriptions) {
            // obtain next ConsumptionInstance for this Prescription
            ConsumptionInstance next_instance = p.generateConsumptionInstances(0).get(0);
            Calendar cal = next_instance.getConsumptionTime();

            String timings = formatInt(cal.get(Calendar.HOUR_OF_DAY), 2) + formatInt(cal.get(Calendar.MINUTE), 2);

            PendingIntent pending = getAlarmIntent(this, id, timings);
            AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

            if (matcher.matchAction(action)) {
                if (action.equals(CREATE)) {
                    am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
                    Log.d("tag111", "(in AlarmService) Alarm set for " + p.getDrug().getName() + " at time " + timings);
                } else if (action.equals(CANCEL)) {
                    am.cancel(pending);
                    Log.d("tag111", "(in AlarmService) Alarm cancelled for " + p.getDrug().getName());
                }
            }
        }
    }
}
