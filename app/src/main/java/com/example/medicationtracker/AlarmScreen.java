package com.example.medicationtracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.medicationtracker.database.DatabaseOpenHelper;
import com.example.medicationtracker.objects.Prescription;

import java.util.ArrayList;

public class AlarmScreen extends AppCompatActivity {
    public static final int ALARM_RING_DURATION = 15000; //in millis

    ListView lv;
    Button btn_stop;
    ArrayList<String> instance_names;
    Ringtone ringtone;
    Handler handler;
    Runnable countdown;
    DatabaseOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        Intent intent = getIntent();
        long request_code = (long) intent.getExtras().get("REQUEST_CODE");

        lv = (ListView) findViewById(R.id.alarm_screen_lv);
        btn_stop = (Button) findViewById(R.id.alarm_screen_btn_stop);

        instance_names = new ArrayList<>();
        addPrescriptionToList(request_code);

        Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        this.ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmTone);
        ringtone.play();

        handler = new Handler();
        startCountdown();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, instance_names);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("fck", "inside on new intent");
        super.onNewIntent(intent);

        setIntent(intent);

        long request_code = (long) intent.getExtras().get("REQUEST_CODE");
        addPrescriptionToList(request_code);

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) lv.getAdapter();
        adapter.notifyDataSetChanged();

        // refresh countdown
        stopCountdown();
        startCountdown();
    }

    private void addPrescriptionToList(long id) {
        db = DatabaseOpenHelper.getInstance(this);
        Prescription p = db.getPrescription(id);
        db.close();

        instance_names.add(p.getDrug().getName());
    }

    public void onStopClicked(View v) {
        ringtone.stop();
        finish();
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
        generateNotification();
        this.ringtone.stop();
        finish();
    }

    public void generateNotification() {
        String content_title = "Missed Medication";
        String content_text = "yolo hue hue"; // join together all the missed meds

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.delete)
                        .setContentTitle(content_title)
                        .setContentText(content_text);

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, mBuilder.build());
    }
}
