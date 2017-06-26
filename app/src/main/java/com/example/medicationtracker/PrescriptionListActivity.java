package com.example.medicationtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicationtracker.objects.ConsumptionInstance;
import com.example.medicationtracker.objects.ConsumptionInstruction;
import com.example.medicationtracker.objects.Drug;
import com.example.medicationtracker.objects.Prescription;
import com.example.medicationtracker.objects.TimeOfDay;
import com.example.medicationtracker.database.DatabaseOpenHelper;
import com.example.medicationtracker.receivers.AlarmReceiver;
import com.example.medicationtracker.services.AlarmService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static android.R.attr.id;
import static android.media.CamcorderProfile.get;
import static com.example.medicationtracker.R.layout.alarm;
import static com.example.medicationtracker.Utility.formatInt;
import static com.example.medicationtracker.Utility.getAlarmIntent;
import static com.example.medicationtracker.Utility.zeroToMinute;

public class PrescriptionListActivity extends AppCompatActivity {
    public static final String EXTRA_KEY_ID = "ID";
    public static final int REQUEST_CODE_ADD = 1;
    public static final int REQUEST_CODE_EDIT = 2;

    DatabaseOpenHelper db;
    ListView lv_prescriptions;
    ArrayList<Prescription> prescriptions;
    ArrayList<ConsumptionInstance> consumption_instances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_list);

        consumption_instances = new ArrayList<>();

        // fetch prescriptions
        db = DatabaseOpenHelper.getInstance(this);
        prescriptions = db.getAllPrescriptions();
        db.close();

        // set adapter
        lv_prescriptions = (ListView) findViewById(R.id.lv_prescriptions);
        PrescriptionsAdapter adapter = new PrescriptionsAdapter(this, prescriptions);
        lv_prescriptions.setAdapter(adapter);
    }

    /*
     * toggles between view by Prescription, or view by Chronological order
     *
     * Post-conditions:
     * chronological order is generated here, so it will be up to date
     */
    public void onToggleViewClicked(View v) {
        if (lv_prescriptions.getAdapter() instanceof PrescriptionsAdapter) {
            consumption_instances = generateChronoList();

            // display Consumption Instances
            CIAdapter ci_adapter = new CIAdapter(this, consumption_instances);
            lv_prescriptions.setAdapter(ci_adapter);
        } else {
            PrescriptionsAdapter adapter = new PrescriptionsAdapter(this, prescriptions);
            lv_prescriptions.setAdapter(adapter);
        }
    }

    /*
     * not yet finalized. only generates the next set if ConsumptionInstances for each medication
     */
    private ArrayList<ConsumptionInstance> generateChronoList() {
        ArrayList<ConsumptionInstance> result = new ArrayList<>();
        for (Prescription p : prescriptions) {
            ArrayList<ConsumptionInstance> temp = p.generateConsumptionInstances(0);
            result.addAll(temp);
        }
        Collections.sort(result);
        return result;
    }


    private class PrescriptionsAdapter extends ArrayAdapter<Prescription> {

        private PrescriptionsAdapter(Context context, List<Prescription> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.layout_prescription_list_by_drug_name, parent, false);
            }

            // convert to viewHolder pattern?
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tv_timing = (TextView) convertView.findViewById(R.id.tv_timing);
            TextView tv_dosage = (TextView) convertView.findViewById(R.id.tv_dosage);
            TextView tv_remarks = (TextView) convertView.findViewById(R.id.tv_remarks);

            ImageView iv_thumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
            ImageView iv_edit = (ImageView) convertView.findViewById(R.id.iv_edit);
            ImageView iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);

            final Prescription p = prescriptions.get(position);

            tv_name.setText(p.getDrug().getName());
            tv_timing.setText(p.getTimingsString());
            tv_dosage.setText(p.getConsumptionInstruction().getDosage());
            tv_remarks.setText(p.getConsumptionInstruction().getRemarks());
            iv_thumbnail.setImageBitmap(p.getDrug().getThumbnail());

            iv_edit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onEditClicked(p);
                        }
                    }
            );
            iv_delete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onDeleteClicked(p);
                        }
                    }
            );

            return convertView;
        }
    }

    public void onEditClicked(Prescription p) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EXTRA_KEY_ID, p.getId());
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    public void onDeleteClicked(Prescription p) {
        db.deletePrescription(p);
        db.close();

        cancelAlarms(p);

        ArrayAdapter<Prescription> adapter = (ArrayAdapter<Prescription>) lv_prescriptions.getAdapter();
        adapter.remove(p); // I am assuming that this.prescriptions is updated by adapter.remove
        adapter.notifyDataSetChanged();
    }

    private class CIAdapter extends ArrayAdapter<ConsumptionInstance> {

        private CIAdapter(Context context, List<ConsumptionInstance> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.layout_prescription_list_chronological, parent, false);
            }

            // convert to viewHolder pattern?
            TextView tv_name = (TextView) convertView.findViewById(R.id.layout_prescription_list_chrono_tv_name);
            TextView tv_time = (TextView) convertView.findViewById(R.id.layout_prescription_list_chrono_tv_time);

            final ConsumptionInstance ci = consumption_instances.get(position);

            tv_name.setText(ci.getDrug().getName());
            tv_time.setText(
                    formatInt(ci.getConsumptionTime().get(Calendar.HOUR_OF_DAY), 2) +
                    formatInt(ci.getConsumptionTime().get(Calendar.MINUTE), 2));

            return convertView;
        }
    }

    public void onAddClicked(View v) {
        Intent intent = new Intent(this, EditActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Prescription new_p = null;

        switch (requestCode) {
            case REQUEST_CODE_ADD:
                if(resultCode == RESULT_OK) {
                    long id = (Long) data.getExtras().get(EXTRA_KEY_ID);
                    new_p = db.getPrescription(id);
                    this.prescriptions.add(new_p);
                }
                break;

            case REQUEST_CODE_EDIT:
                if (resultCode == RESULT_OK) {
                    long id = (Long) data.getExtras().get(EXTRA_KEY_ID);
                    new_p = db.getPrescription(id);
                    this.prescriptions.set(this.prescriptions.indexOf(new_p), new_p);
                }
                break;
        }

        // unset and reset the alarms
        if (new_p != null) {
            //cancelAlarms(new_p);
            // override old alarms if any
            setAlarms(this, new_p.getId());
        }
        updateListView();
    }

    private void updateListView() {
        ListAdapter adapter = lv_prescriptions.getAdapter();
        if (adapter instanceof PrescriptionsAdapter) {
            ((PrescriptionsAdapter) adapter).notifyDataSetChanged();
        } else if (adapter instanceof  CIAdapter) {
            ((CIAdapter) adapter).notifyDataSetChanged();
        }
    }

    /*
     * Sets an alarm for a ConsumptionInstance.
     * The request code of the PendingIntent is the ID
     */
    private void setAlarm(ConsumptionInstance instance) {
        Calendar cal = instance.getConsumptionTime();

        String timing = formatInt(cal.get(Calendar.HOUR_OF_DAY), 2) + formatInt(cal.get(Calendar.MINUTE), 2);
        long alarm_millis = cal.getTimeInMillis();
        long request_code = instance.getId();

        // set Broadcast at specified time with request_code
        PendingIntent pending = getAlarmIntent(this, request_code, timing);
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.setExact(AlarmManager.RTC_WAKEUP, alarm_millis, pending);

        Log.d("tag111", "(" + instance.getDrug().getName() + ") alarm set for " + timing + " at millis: " + alarm_millis);
    }

    /*
     * Sets alarm for next ConsumptionInstance of Prescription
     *
     * Pre-conditions:
     * Prescription must have a valid set of ConsumptionInstances
     */
    public static void setAlarms(Context ctx, long id) {

        Intent i = new Intent(ctx, AlarmService.class);
        i.putExtra("REQUEST_CODE", id);
        i.setAction(AlarmService.CREATE);
        ctx.startService(i);

        /* deprecated
        ArrayList<ConsumptionInstance> instances = p.generateConsumptionInstances(0);
        if (instances.size() > 0) { // must have at least 1 ConsumptionInstances
            ConsumptionInstance first_instance = instances.get(0);
            setAlarm(first_instance);
        } else {
            Log.e("error", "Error in setAlarms: empty consumption instances");
        }
        */
    }

    /*
     * Cancels the alarm cycle for this prescription
     */
    private void cancelAlarms(Prescription p) {
        Intent i = new Intent(this, AlarmService.class);
        i.putExtra("REQUEST_CODE", p.getId());
        i.setAction(AlarmService.CANCEL);
        startService(i);

        /* deprecated
        // create exact same pendingIntent
        PendingIntent pending = getAlarmIntent(this, p.getId(), "0000");
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        am.cancel(pending);

        Log.d("tag111", "alarm canceled for " + p.getId());
        */
    }

    public void onSetAlarmsClicked(View v) {
        Toast.makeText(this, "alarms set", Toast.LENGTH_SHORT).show();

        for(Prescription p : this.prescriptions) {
            setAlarms(this, p.getId());
        }
    }









    public ArrayList<Prescription> getTestList() {
        Bitmap img = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Drug d1 = new Drug("panadol", img);
        Drug d2 = new Drug("soficloasdasdr", img);
        ConsumptionInstruction ci1 = new ConsumptionInstruction("2 tablets", "after food");
        ConsumptionInstruction ci2 = new ConsumptionInstruction("10ml", "nil");
        GregorianCalendar c1 = new GregorianCalendar();
        GregorianCalendar c2 = new GregorianCalendar();
        ArrayList<TimeOfDay> t1 = new ArrayList<>();
        t1.add(new TimeOfDay("10", "00"));
        t1.add(new TimeOfDay("12", "30"));
        ArrayList<TimeOfDay> t2 = new ArrayList<>();
        t2.add(new TimeOfDay("14", "03"));
        t2.add(new TimeOfDay("18", "05"));

        Prescription p1 = new Prescription(c1, 1, t1, d1, ci1);
        Prescription p2 = new Prescription(c2, 2, t2, d2, ci2);

        ArrayList<Prescription> result = new ArrayList<>();
        result.add(p1);
        result.add(p2);
        return result;
    }
}
