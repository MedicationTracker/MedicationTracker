package com.example.medicationtracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicationtracker.database.DatabaseOpenHelper;
import com.example.medicationtracker.objects.Prescription;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.example.medicationtracker.Utility.formatInt;
import static com.example.medicationtracker.Utility.setAlarm;
import static com.example.medicationtracker.services.AlarmService.KEY_ID;
import static com.example.medicationtracker.services.AlarmService.KEY_TIMESTAMP;

/**
 * Receives id and timestamp
 *
 * Displays them in a list
 *
 * Sets the next Alarm for the received Prescriptions
 *
 * Manages 60s countdown before auto-stopping
 */

public class AlarmScreen extends AppCompatActivity {
    public static final int ALARM_RING_DURATION = 60000; //in millis

    ListView lv;
    Button btn_stop;
    ArrayList<Prescription> prescriptions;
    ArrayList<String> time_strings;
    Ringtone ringtone;
    Handler handler;
    Runnable countdown;
    DatabaseOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        Intent intent = getIntent();
        long id = (long) intent.getExtras().get(KEY_ID);
        long timestamp = (long) intent.getExtras().get(KEY_TIMESTAMP);

        lv = (ListView) findViewById(R.id.alarm_screen_lv);

        this.prescriptions = new ArrayList<>();
        addPrescriptionToList(id);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(timestamp);
        String timing_string = formatInt(cal.get(Calendar.HOUR_OF_DAY), 2) + formatInt(cal.get(Calendar.MINUTE), 2);
        this.time_strings = new ArrayList<>();
        this.time_strings.add(timing_string);

        Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        this.ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmTone);
        ringtone.play();

        handler = new Handler();
        startCountdown();

        PrescriptionAdapter adapter = new PrescriptionAdapter(this, this.prescriptions);
        lv.setAdapter(adapter);

    }

    private class PrescriptionAdapter extends ArrayAdapter<Prescription> {

        private PrescriptionAdapter(Context context, List<Prescription> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.layout_alarm_list_item, parent, false);
            }

            TextView tv_name = (TextView) convertView.findViewById(R.id.layout_alarm_list_tv_name);
            TextView tv_timing = (TextView) convertView.findViewById(R.id.layout_alarm_list_tv_time);
            TextView tv_dosage = (TextView) convertView.findViewById(R.id.layout_alarm_list_tv_dosage);
            TextView tv_remarks = (TextView) convertView.findViewById(R.id.layout_alarm_list_tv_remarks);
            ImageView iv_thumbnail = (ImageView) convertView.findViewById(R.id.layout_alarm_list_iv_thumbnail);

            Prescription p = getItem(position);
            String s = time_strings.get(position);

            tv_name.setText(p.getDrug().getName());
            tv_timing.setText(s); //need to change
            tv_dosage.setText(p.getConsumptionInstruction().getDosage());
            tv_remarks.setText(p.getConsumptionInstruction().getRemarks());
            iv_thumbnail.setImageBitmap(p.getDrug().getThumbnail());

            return convertView;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //Log.d(Utility.TAG, "Inside AlarmScreen/onNewIntent");
        super.onNewIntent(intent);

        setIntent(intent);

        long id = (long) intent.getExtras().get(KEY_ID);
        long timestamp = (long) intent.getExtras().get(KEY_TIMESTAMP);

        addPrescriptionToList(id);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(timestamp);
        String timing_string = formatInt(cal.get(Calendar.HOUR_OF_DAY), 2) + formatInt(cal.get(Calendar.MINUTE), 2);
        this.time_strings.add(timing_string);

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) lv.getAdapter();
        adapter.notifyDataSetChanged();

        // refresh countdown
        stopCountdown();
        startCountdown();
    }

    private synchronized void addPrescriptionToList(long id) {
        db = DatabaseOpenHelper.getInstance(this);
        Prescription p = db.getPrescription(id);
        db.close();

        prescriptions.add(p);

        setAlarm(this, p);
    }

    public void onYesClicked(View v) {
        ringtone.stop();
        finish();
    }

    public void onNoClicked(View v) {
        onTimeUp();
    }

    /*
     * starts the timer to AlarmScreen activity being auto-stopped
     */
    public void startCountdown() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onTimeUp();
            }
        };

        this.countdown = runnable;
        this.handler.postDelayed(runnable, ALARM_RING_DURATION);
    }

    /*
     * cancels the countdown (so that can refresh the countdown)
     */
    public void stopCountdown() {
        this.handler.removeCallbacks(this.countdown);
    }


    public void onTimeUp() {
        for(Prescription p : prescriptions) {
            generateNotification(p);
        }
        this.ringtone.stop();
        finish();
    }

    public void generateNotification(Prescription p) {
        String content_title = "Missed Medication: " + p.getDrug().getName();
        String content_text = p.getConsumptionInstruction().getDosage() + " " + p.getConsumptionInstruction().getRemarks();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.apple_round)
                        .setContentTitle(content_title)
                        .setContentText(content_text)
                        .setAutoCancel(true);

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, (int) p.getId(), i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify((int) p.getId(), mBuilder.build());
    }
}
