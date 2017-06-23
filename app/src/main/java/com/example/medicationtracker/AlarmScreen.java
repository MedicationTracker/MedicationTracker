package com.example.medicationtracker;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
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

    ListView lv;
    Button btn_stop;
    ArrayList<String> instance_names;
    Ringtone ringtone;
    Handler handler;
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
}
