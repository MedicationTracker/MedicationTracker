package com.example.medicationtracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import com.example.medicationtracker.ObjectClasses.Prescription;
import java.util.ArrayList;


import static com.example.medicationtracker.Utility.*;

/**
 * Created by Jia Hao on 5/31/2017.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static DatabaseOpenHelper mInstance = null;
    public static final String DATABASE_NAME = "MEDICATION TRACKER";
    public static final String TABLE_NAME_PRESCRIPTION = "PRESCRIPTION";
    public static final int VERSION = 2;

    /*
    column names
     */
    public static final String COL_ID = "ID";
    public static final String COL_DRUG_NAME = "DRUGNAME";
    public static final String COL_THUMBNAIL = "THUMBNAIL"; //stores a byte array
    public static final String COL_START_DATE = "STARTDATE"; //stores epoch time in millis
    public static final String COL_DOSAGE = "DOSAGE";
    public static final String COL_TIMINGS = "TIMINGS"; //space-delimited string of 24hr timings e.g. "0800 1200 1800"
    public static final String COL_INTERVAL = "INTERVAL";
    public static final String COL_REMARKS = "REMARKS";
    public static final String CREATE_TABLE_PRESCRIPTION = "CREATE TABLE " + TABLE_NAME_PRESCRIPTION + " (" +
            COL_ID + " INTEGER PRIMARY KEY, " +
            COL_DRUG_NAME + " TEXT, " +
            COL_THUMBNAIL + " BLOB, " +
            COL_START_DATE + " INTEGER, " +
            COL_DOSAGE + " TEXT, " +
            COL_TIMINGS + " TEXT, " +
            COL_INTERVAL + " INTEGER, " +
            COL_REMARKS + " TEXT);";

    private DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /*
    use this method to get instance of database, so as to avoid leakages
     */
    public static DatabaseOpenHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DatabaseOpenHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRESCRIPTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PRESCRIPTION);
        onCreate(db);
    }
    /*
    always call this method to close DB after using it
     */
    public void close() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    /*
    stores Prescription into db
     */
    public long addPrescription(Prescription p) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_DRUG_NAME, p.getDrug().getName());
        cv.put(COL_THUMBNAIL, getBytes(p.getDrug().getThumbnail()));
        cv.put(COL_START_DATE, p.getStartDate().getTimeInMillis());
        cv.put(COL_DOSAGE, p.getConsumptionInstruction().getDosage());
        cv.put(COL_TIMINGS, p.getTimingsString());
        cv.put(COL_INTERVAL, p.getInterval());
        cv.put(COL_REMARKS, p.getConsumptionInstruction().getRemarks());

        long id = db.insert(TABLE_NAME_PRESCRIPTION, null, cv);
        //p.setId(id);
        return id;
    }

    public ArrayList<Prescription> getAllPrescriptions() {
        ArrayList<Prescription> result = new ArrayList<>();
        String select_query = "SELECT * FROM " + TABLE_NAME_PRESCRIPTION;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(select_query, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    long id = c.getInt(c.getColumnIndex(COL_ID));

                    String drug_name = c.getString(c.getColumnIndex(COL_DRUG_NAME));
                    Bitmap drug_thumbnail = getImage(c.getBlob(c.getColumnIndex(COL_THUMBNAIL)));
                    //Drug d = new Drug(drug_name, drug_thumbnail);

                    String remarks = c.getString(c.getColumnIndex(COL_REMARKS));
                    String dosage = c.getString(c.getColumnIndex(COL_DOSAGE));
                    //ConsumptionInstruction ci = new ConsumptionInstruction(dosage, remarks);

                    //Calendar start_date = Calendar.getInstance();
                    long millis = c.getInt(c.getColumnIndex(COL_START_DATE));
                    //start_date.setTimeInMillis(millis);
                    int interval = c.getInt(c.getColumnIndex(COL_INTERVAL));
                    //ArrayList<TimeOfDay> timings = stringToTimeOfDayArray(c.getString(c.getColumnIndex(COL_TIMINGS)));
                    String timings = c.getString(c.getColumnIndex(COL_TIMINGS));
                    Prescription p = new Prescription(id, drug_name, drug_thumbnail, dosage, remarks,
                            millis, interval, timings);
                    result.add(p);
                } while (c.moveToNext());
            }
        }
        return result;
    }

    public Prescription getPrescription(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME_PRESCRIPTION + " WHERE "
                + COL_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        }
        String drug_name = c.getString(c.getColumnIndex(COL_DRUG_NAME));
        Bitmap drug_thumbnail = getImage(c.getBlob(c.getColumnIndex(COL_THUMBNAIL)));
        String remarks = c.getString(c.getColumnIndex(COL_REMARKS));
        String dosage = c.getString(c.getColumnIndex(COL_DOSAGE));
        long millis = c.getLong(c.getColumnIndex(COL_START_DATE));
        int interval = c.getInt(c.getColumnIndex(COL_INTERVAL));
        String timings = c.getString(c.getColumnIndex(COL_TIMINGS));
        Prescription p = new Prescription(id, drug_name, drug_thumbnail, dosage, remarks,
                millis, interval, timings);

        return p;
    }

    public void deletePrescription(Prescription p) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_PRESCRIPTION, COL_ID + " = ?", new String[] { String.valueOf(p.getId()) });
    }
    public void deletePrescriptionById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_PRESCRIPTION, COL_ID + " = ?", new String[] { String.valueOf(id) });
    }

    /*
    updates
    does not require p to exist
    returns the number of affected rows
     */
    public int updatePrescription(Prescription p) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_DRUG_NAME, p.getDrug().getName());
        cv.put(COL_THUMBNAIL, getBytes(p.getDrug().getThumbnail()));
        cv.put(COL_START_DATE, p.getStartDate().getTimeInMillis());
        cv.put(COL_DOSAGE, p.getConsumptionInstruction().getDosage());
        cv.put(COL_TIMINGS, p.getTimingsString());
        cv.put(COL_INTERVAL, p.getInterval());
        cv.put(COL_REMARKS, p.getConsumptionInstruction().getRemarks());

        int num_rows_modified = db.update(TABLE_NAME_PRESCRIPTION, cv, COL_ID + " = ?",
                new String[] { String.valueOf(p.getId()) });
        return num_rows_modified;
    }
}