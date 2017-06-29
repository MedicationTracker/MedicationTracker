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
import java.util.GregorianCalendar;

import static android.R.attr.action;
import static android.R.attr.id;
import static com.example.medicationtracker.Utility.formatInt;

/**
 * AlarmService
 *
 * Set and cancel alarms through this IntentService
 *
 * How to use:
 * Create an Intent to this class
 * Put an extra for ID with KEY_ID as id, or -1 to set alarm for all Prescriptions
 * set action to be either AlarmService.CREATE or alarmService.CANCEL
 */

public class AlarmService extends IntentService {
    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";

    public static final String KEY_ID = "ID";
    public static final String KEY_TIMESTAMP = "TIMESTAMP";
    public static final long ALL_ALARMS = -1;

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

        long id = (long) intent.getExtras().get(KEY_ID);
        long timestamp = (long) intent.getExtras().get(KEY_TIMESTAMP);

        if (id == ALL_ALARMS) {
            setAllAlarms();
        } else {
            setOrCancelAlarm(action, id, timestamp);
        }
    }

    private void setOrCancelAlarm(String action, long id, long timestamp) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(KEY_ID, id);

        // for debugging
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(timestamp);
        String timing = formatInt(cal.get(Calendar.HOUR_OF_DAY), 2) + formatInt(cal.get(Calendar.MINUTE), 2);
        intent.putExtra("TIMING_KEY", timing);

        // warning: contains cast from long to id
        PendingIntent pending = PendingIntent.getBroadcast(this, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        if (matcher.matchAction(action)) {
            if (action.equals(CREATE)) {
                am.setExact(AlarmManager.RTC_WAKEUP, timestamp, pending);
                Log.d("tag111", "(in AlarmService) Alarm set for drug ID " + id + " at time " + timing);
            } else if (action.equals(CANCEL)) {
                am.cancel(pending);
                Log.d("tag111", "(in AlarmService) Alarm cancelled for drug ID " + id);
            }
        }
    }

    private void setAllAlarms() {
        DatabaseOpenHelper db = DatabaseOpenHelper.getInstance(this);
        ArrayList<Prescription> prescriptions = db.getAllPrescriptions();
        db.close();

        for(Prescription p : prescriptions) {
            ConsumptionInstance next_instance = p.getNextInstance();
            Calendar cal = next_instance.getConsumptionTime();

            setOrCancelAlarm(CREATE, p.getId(), cal.getTimeInMillis());
        }
    }
}
