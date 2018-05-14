package com.example.medicationtracker;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicationtracker.dialogs.ExpandedDialog;
import com.example.medicationtracker.dialogs.TimeListDialog;
import com.example.medicationtracker.objects.ConsumptionInstance;
import com.example.medicationtracker.objects.Prescription;
import com.example.medicationtracker.database.DatabaseOpenHelper;
import com.example.medicationtracker.objects.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.example.medicationtracker.Utility.setAlarm;
import static com.example.medicationtracker.Utility.cancelAlarm;
import static com.example.medicationtracker.Utility.formatInt;

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

        /* used to refresh list every 15s
        // needed because of deleted drugs
        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                Log.d(Utility.TAG, "refreshing list");
                updateListView();
                handler.postDelayed(this, 15 * 1000);
            }
        }, 15 * 1000);
        */

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshChronoList();
    }

    /*
     * toggles between view by Prescription, or view by Chronological order
     *
     * Post-conditions:
     * chronological order is generated here, so it will be up to date
     */
    public void onToggleViewClicked(View v) {
        if (lv_prescriptions.getAdapter() instanceof PrescriptionsAdapter) {
            refreshChronoList();

            // display Consumption Instances
            CIAdapter ci_adapter = new CIAdapter(this, consumption_instances);
            lv_prescriptions.setAdapter(ci_adapter);
        } else {
            PrescriptionsAdapter adapter = new PrescriptionsAdapter(this, prescriptions);
            lv_prescriptions.setAdapter(adapter);
        }
    }

    /*
     * generates 1 day of instances
     */
    private ArrayList<ConsumptionInstance> generateChronoList(long start_time) {
        ArrayList<ConsumptionInstance> result = new ArrayList<>();
        for (Prescription p : prescriptions) {
            ArrayList<ConsumptionInstance> temp = p.generateConsumptionInstances(start_time, start_time + Utility.MILLIS_IN_DAY);
            result.addAll(temp);
        }
        Collections.sort(result);
        return result;
    }

    /*
     * top-up consumption_instances
     * after being called, consumption instance will have at least 20 items, all in the future
     */
    private void fillChronoList() {
        if (consumption_instances.isEmpty()) {
            long now = System.currentTimeMillis();
            while (consumption_instances.size() < 20) {
                consumption_instances.addAll(generateChronoList(now));
                now += Utility.MILLIS_IN_DAY;
            }
        } else {
            ConsumptionInstance last = consumption_instances.get(consumption_instances.size() - 1);
            long last_millis = last.getConsumptionTime().getTimeInMillis() + 1;
            long latest = Math.max(last_millis, System.currentTimeMillis());

            while (consumption_instances.size() < 20) {
                consumption_instances.addAll(generateChronoList(latest));
                latest += Utility.MILLIS_IN_DAY;
            }
        }
    }

    /*
     * removes expired instances
     * removes expired instances from Prescriptions
     * refills consumption_instances if too little
     */
    private void refreshChronoList() {
        long now = System.currentTimeMillis();
        while (!consumption_instances.isEmpty() && consumption_instances.get(0).getConsumptionTime().getTimeInMillis() <= now) {
            ConsumptionInstance top = consumption_instances.remove(0);
        }

        for (Prescription p : this.prescriptions) {
            p.clean();
            db.updateDeleted(p);
        }
        if (consumption_instances.size() < 10) {
            fillChronoList();
        }

        updateListView();
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

            final Prescription p = getItem(position);

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
            iv_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onExpandClicked(p);
                }
            });

            return convertView;
        }
    }

    public void onEditClicked(Prescription p) {
        SessionManager sm = new SessionManager(this);
        if (sm.isLoggedIn()) {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(EXTRA_KEY_ID, p.getId());
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        } else {
            Toast.makeText(this, "Please Login for Edit Privileges", Toast.LENGTH_SHORT).show();
        }
    }

    public void onDeleteClicked(Prescription p) {
        SessionManager sm = new SessionManager(this);
        if (sm.isLoggedIn()) {
            db.deletePrescription(p);
            db.close();

            cancelAlarm(this, p);

            ArrayAdapter<Prescription> adapter = (ArrayAdapter<Prescription>) lv_prescriptions.getAdapter();
            adapter.remove(p);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Please Login for Edit Privileges", Toast.LENGTH_SHORT).show();
        }
    }

    public void onExpandClicked(Prescription p) {
        FragmentManager fm = getFragmentManager();
        ExpandedDialog dialog = ExpandedDialog.newInstance(p.getId());
        dialog.show(fm, ExpandedDialog.TAG);
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
            if (ci.isDeleted()) {
                convertView.setBackgroundColor(getResources().getColor(R.color.colorRed));
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.colorEmpty));
            }
            String time = formatInt(ci.getConsumptionTime().get(Calendar.HOUR_OF_DAY), 2) +
                    formatInt(ci.getConsumptionTime().get(Calendar.MINUTE), 2);
            tv_time.setText(time);

            // listener
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ci.isDeleted()) {
                        ci.setDeleted(false);
                        v.setBackgroundColor(getResources().getColor(R.color.colorEmpty));
                        //update prescription
                        for (Prescription p : prescriptions) {
                            if (p.getId() == ci.getId()) {
                                long millis = ci.getConsumptionTime().getTimeInMillis();
                                p.getDeleted().remove(millis);
                                String msg = "restored : " + p.getDrug().getName() + " at " + Utility.timestampToString(millis);
                                Toast.makeText(PrescriptionListActivity.this, msg, Toast.LENGTH_SHORT).show();
                                setAlarm(v.getContext(), p);

                                db.updateDeleted(p);
                                db.close();
                            }
                        }
                    } else {
                        ci.setDeleted(true);
                        v.setBackgroundColor(getResources().getColor(R.color.colorRed));
                        for (Prescription p : prescriptions) {
                            if (p.getId() == ci.getId()) {
                                long millis = ci.getConsumptionTime().getTimeInMillis();
                                p.getDeleted().add(millis);
                                String msg = "deleted : " + p.getDrug().getName() + " at " + Utility.timestampToString(millis);
                                Toast.makeText(PrescriptionListActivity.this, msg, Toast.LENGTH_SHORT).show();
                                setAlarm(v.getContext(), p);

                                db.updateDeleted(p);
                                db.close();
                            }
                        }
                    }
                    updateListView();
                }
            });

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
                if (resultCode == RESULT_OK) {
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
            setAlarm(this, new_p);
        }
        consumption_instances.clear();
        updateListView();
    }

    private void updateListView() {
        ListAdapter adapter = lv_prescriptions.getAdapter();
        if (adapter instanceof PrescriptionsAdapter) {
            ((PrescriptionsAdapter) adapter).notifyDataSetChanged();
        } else if (adapter instanceof CIAdapter) {
            ((CIAdapter) adapter).notifyDataSetChanged();
        }
    }

    public Prescription getPrescription(long id) {
        for(Prescription p : prescriptions) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}