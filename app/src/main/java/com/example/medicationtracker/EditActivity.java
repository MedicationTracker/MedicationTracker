package com.example.medicationtracker;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.medicationtracker.dialogs.TimeListDialog;
import com.example.medicationtracker.objects.Prescription;
import com.example.medicationtracker.objects.TimeOfDay;
import com.example.medicationtracker.database.DatabaseOpenHelper;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.media.CamcorderProfile.get;
import static com.example.medicationtracker.PrescriptionListActivity.EXTRA_KEY_ID;
import static com.example.medicationtracker.Utility.*;
import static com.example.medicationtracker.dialogs.TimeListDialog.TAG_TIMELISTDIALOG;

public class EditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimeListDialog.TimeListDialogListener {
    private static final int TAKE_PHOTO_REQUEST_CODE = 3;
    private static final String date_format_pattern = "dd-MM-yyyy";

    private EditText et_name, et_dosage, et_remarks, et_start_date, et_frequency, et_timings;
    private ImageView iv_thumbnail;

    private Prescription p;
    private boolean isEditing = false; //boolean to track
    private DatabaseOpenHelper db;
    private SimpleDateFormat df;
    private Context mContext = this;
    private DatePickerDialog.OnDateSetListener dateSetListener = this;
    private TimeListDialog.TimeListDialogListener time_dialog_listener = this;
    private GregorianCalendar date_picker_calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        this.et_name = (EditText) findViewById(R.id.activity_edit_et_name);
        this.et_dosage = (EditText) findViewById(R.id.activity_edit_et_dosage);
        this.et_remarks = (EditText) findViewById(R.id.activity_edit_et_remarks);
        this.et_start_date = (EditText) findViewById(R.id.activity_edit_et_start_date);
        this.et_frequency = (EditText) findViewById(R.id.activity_edit_et_frequency);
        this.et_timings = (EditText) findViewById(R.id.activity_edit_et_timings);
        this.iv_thumbnail = (ImageView) findViewById(R.id.activity_edit_iv_thumbnail);
        date_picker_calendar = new GregorianCalendar();
        this.df = new SimpleDateFormat(date_format_pattern);
        this.isEditing = isEditing();

        if (this.isEditing) {
            getPrescriptionToBeEdited();
            fillUpFields();
        }
        setDateListener();
        setTimeListener();
        setThumbnailListener();

        FragmentManager fm = getFragmentManager();
        TimeListDialog retainedFragment = (TimeListDialog) fm.findFragmentByTag(TimeListDialog.TAG_TIMELISTDIALOG);
        if(retainedFragment != null) {
            Log.d("tag111", "retainedfragment exists");
            retainedFragment.setTimeListDialogListener(this.time_dialog_listener);
            //retainedFragment.show(fm, TimeListDialog.TAG_TIMELISTDIALOG);
        }
    }

    private boolean isEditing() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Long id = (Long) extras.get(EXTRA_KEY_ID);
            if (id != null) {
                return true;
            }
        }
        return false;
    }
    /*
     * If Activity started because of Edit, then obtain the Prescription that is currently being edited.
     * if not edit, this.p is null
     */
    private void getPrescriptionToBeEdited() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Long id = (Long) extras.get(EXTRA_KEY_ID);
            if (id != null) {
                this.db = DatabaseOpenHelper.getInstance(this);
                this.p = this.db.getPrescription(id);
                db.close();
                this.isEditing = true;
            }
        }
    }

    /*
     * calling this method to fill up the fields in the case that EditActivity is started thru edit button, not add
     */
    private void fillUpFields() {
        if (this.p != null) {
            this.et_name.setText(p.getDrug().getName());
            this.et_dosage.setText(p.getConsumptionInstruction().getDosage());
            this.et_remarks.setText(p.getConsumptionInstruction().getRemarks());
            this.et_frequency.setText(String.valueOf(p.getInterval()));
            this.et_start_date.setText(df.format(p.getStartDate().getTime()));
            this.et_timings.setText(timeOfDayArrayToString(p.getTimings()));
            this.iv_thumbnail.setImageBitmap(p.getDrug().getThumbnail());
            this.date_picker_calendar = p.getStartDate();
        }
    }

    /*
     * Opens DatePickerDialog when user clicks on et_start_date
     */
    private void setDateListener() {
        this.et_start_date.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int day = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datepicker = new DatePickerDialog(mContext,  dateSetListener, year, month, day);
                        datepicker.show();
                    }
                }
        );
    }

    /*
     * callback for when user picks a date
     */
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.date_picker_calendar.set(year, month, dayOfMonth, 0, 0); //set to the dd-MM-yyyy 00:00
        zeroToMinute(date_picker_calendar);
        String date = dayOfMonth + "-" + (month+1) + "-" + year;

        try {
            this.et_start_date.setText(df.format(df.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
            this.et_start_date.setText("01-01-1970");
        }
    }

    private void setTimeListener() {
        this.et_timings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeListDialog();
            }
        });
    }

    /*
     * Passes the current contents of et_timings to the TimeListDialog
     */
    private void openTimeListDialog() {
        String inputs = et_timings.getText().toString();
        FragmentManager fm = getFragmentManager();
        TimeListDialog dialog = TimeListDialog.newInstance(inputs);
        dialog.setTimeListDialogListener(this.time_dialog_listener); // EditActivity can receive callbacks from TimeListDialog
        dialog.show(fm, TimeListDialog.TAG_TIMELISTDIALOG);
    }

    /*
     * callback for TimeListDialog
     */
    public void onTimeListDialogFinishedListener(ArrayList<TimeOfDay> xs) {
        String s = timeOfDayArrayToString(xs);
        this.et_timings.setText(s);
    }

    private void setThumbnailListener() {
        this.iv_thumbnail.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePictureWithCamera();
                    }
                }
        );
    }
    private void takePictureWithCamera() {
        PackageManager pm = this.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) { //check device for camera
            // create intent to capture image from camera
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST_CODE);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv_thumbnail.setImageBitmap(imageBitmap);
        }
    }

    public void onCancelClicked(View v) {
        Intent data = new Intent();
        setResult(RESULT_CANCELED,data);
        finish();
    }

    public void onSaveClicked(View v) {
        if (!validateInputs()) {
            return;
        }

        this.db = DatabaseOpenHelper.getInstance(this);
        Prescription new_p = makePrescriptionFromFields();

        if(!isEditing) {
            // activity was started by ADD new drug, so add new drug
            // future: include checks for invalid fields
            long id = db.addPrescription(new_p);
            new_p.setId(id);
        } else {
            // activity started by edit drug
            // Update this drug
            long id = this.p.getId();
            new_p.setId(id);
            db.updatePrescription(new_p);
        }
        this.p = new_p;
        db.close();

        Intent result = new Intent();
        //return id of new/edited Prescription for Calling activity to retrieve
        result.putExtra(EXTRA_KEY_ID, p.getId());
        setResult(RESULT_OK,result);
        finish();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(et_name.getText().toString())) {
            et_name.setError("Name required");
            isValid = false;
        }

        if (TextUtils.isEmpty(et_dosage.getText().toString())) {
            et_dosage.setError("Dosage required");
            isValid = false;
        }

        if (TextUtils.isEmpty(et_frequency.getText().toString())) {
            et_frequency.setError("Interval required");
            isValid = false;
        }

        if (TextUtils.isEmpty(et_timings.getText().toString())) {
            et_timings.setFocusable(true);
            et_timings.setFocusableInTouchMode(true);
            et_timings.setError("At least 1 timing required");
            isValid = false;
        }
        return isValid;
    }

    /*
     * assumes that all fields are filled and valid
     */
    private Prescription makePrescriptionFromFields() {
        String drug_name = et_name.getText().toString();
        Bitmap drug_thumbnail = ((BitmapDrawable) iv_thumbnail.getDrawable()).getBitmap();
        String remarks = et_remarks.getText().toString();
        String dosage = et_dosage.getText().toString();
        int interval = Integer.parseInt(et_frequency.getText().toString());
        String timings = et_timings.getText().toString();
        GregorianCalendar c;
        if (date_picker_calendar == null) {
            c = new GregorianCalendar();
        } else {
            c = (GregorianCalendar) date_picker_calendar.clone();
        }

        return new Prescription(0, drug_name, drug_thumbnail, dosage, remarks,
                c.getTimeInMillis(), interval, timings, "");
    }


}
