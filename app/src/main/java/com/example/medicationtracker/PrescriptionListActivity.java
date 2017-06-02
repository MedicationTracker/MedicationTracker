package com.example.medicationtracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicationtracker.ObjectClasses.ConsumptionInstruction;
import com.example.medicationtracker.ObjectClasses.Drug;
import com.example.medicationtracker.ObjectClasses.Prescription;
import com.example.medicationtracker.ObjectClasses.TimeOfDay;
import com.example.medicationtracker.database.DatabaseOpenHelper;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class PrescriptionListActivity extends AppCompatActivity {
    public static final String EXTRA_KEY_ID = "ID";
    public static final int REQUEST_CODE_ADD = 1;
    public static final int REQUEST_CODE_EDIT = 2;

    DatabaseOpenHelper db;
    ArrayList<Prescription> prescriptions;
    ListView lv_prescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_list);

        // fetch prescriptions
        db = DatabaseOpenHelper.getInstance(this);
        prescriptions = db.getAllPrescriptions();
        db.close();
        // set adapter
        lv_prescriptions = (ListView) findViewById(R.id.lv_prescriptions);
        PrescriptionsAdapter adapter = new PrescriptionsAdapter(this, prescriptions);
        lv_prescriptions.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }





    private class PrescriptionsAdapter extends ArrayAdapter<Prescription> {

        public PrescriptionsAdapter(Context context, List<Prescription> objects) {
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
    public void onDeleteClicked(Prescription p) {
        db.deletePrescription(p);
        db.close();

        ArrayAdapter<Prescription> adapter = (ArrayAdapter<Prescription>) lv_prescriptions.getAdapter();
        adapter.remove(p);
        adapter.notifyDataSetChanged();
    }

    public void onEditClicked(Prescription p) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EXTRA_KEY_ID, p.getId());
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    public void onAddClicked(View v) {
        Intent intent = new Intent(this, EditActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Prescription new_p;
        if (requestCode == REQUEST_CODE_ADD) {
            if(resultCode == RESULT_OK) {
                long id = (Long) data.getExtras().get(EXTRA_KEY_ID);
                new_p = db.getPrescription(id);
                this.prescriptions.add(new_p);
            }
        } else if (requestCode == REQUEST_CODE_EDIT) {
            if (resultCode == RESULT_OK) {
                long id = (Long) data.getExtras().get(EXTRA_KEY_ID);
                new_p = db.getPrescription(id);
                this.prescriptions.set(this.prescriptions.indexOf(new_p), new_p);
            }
        }
        ArrayAdapter<Prescription> adapter = (ArrayAdapter<Prescription>) lv_prescriptions.getAdapter();
        adapter.notifyDataSetChanged();
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
