package com.example.medicationtracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import com.example.medicationtracker.Utility;
import com.example.medicationtracker.objects.Prescription;
import java.util.*;

import static com.example.medicationtracker.Utility.getBytes;
import static com.example.medicationtracker.Utility.getImage;
import static com.example.medicationtracker.Utility.longArrayToString;


/**
 * Database for Prescriptions
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static DatabaseOpenHelper mInstance = null;
    private static final String DATABASE_NAME = "MEDICATION TRACKER";
    private static final String TABLE_NAME_PRESCRIPTION = "PRESCRIPTION";
    private static final int VERSION = 4;

    // column names
    private static final String COL_ID = "ID";
    private static final String COL_DRUG_NAME = "DRUGNAME";
    private static final String COL_THUMBNAIL = "THUMBNAIL"; //stores a byte array
    private static final String COL_START_DATE = "STARTDATE"; //stores epoch time in millis
    private static final String COL_DOSAGE = "DOSAGE";
    private static final String COL_TIMINGS = "TIMINGS"; //space-delimited string of 24hr timings e.g. "0800 1200 1800"
    private static final String COL_INTERVAL = "INTERVAL";
    private static final String COL_REMARKS = "REMARKS";
    private static final String COL_DELETED = "DELETED";

    // create table String
    private static final String CREATE_TABLE_PRESCRIPTION = "CREATE TABLE " + TABLE_NAME_PRESCRIPTION + " (" +
            COL_ID + " INTEGER PRIMARY KEY, " +
            COL_DRUG_NAME + " TEXT, " +
            COL_THUMBNAIL + " BLOB, " +
            COL_START_DATE + " BIGINT, " +
            COL_DOSAGE + " TEXT, " +
            COL_TIMINGS + " TEXT, " +
            COL_INTERVAL + " INTEGER, " +
            COL_REMARKS + " TEXT, " +
            COL_DELETED + " TEXT" + ");";

    private DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /*
    use this method to get instance of database, so as to avoid leakages
     */
    public static synchronized DatabaseOpenHelper getInstance(Context ctx) {
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
        //Log.d(Utility.TAG, "Adding Prescription " + p.getDrug().getName());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_DRUG_NAME, p.getDrug().getName());
        cv.put(COL_THUMBNAIL, getBytes(p.getDrug().getThumbnail()));
        cv.put(COL_START_DATE, p.getStartDate().getTimeInMillis());
        cv.put(COL_DOSAGE, p.getConsumptionInstruction().getDosage());
        cv.put(COL_TIMINGS, p.getTimingsString());
        cv.put(COL_INTERVAL, p.getInterval());
        cv.put(COL_REMARKS, p.getConsumptionInstruction().getRemarks());
        cv.put(COL_DELETED, Utility.longArrayToString(p.getDeleted()));

        long id;
        id = db.insert(TABLE_NAME_PRESCRIPTION, null, cv);
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

                    String remarks = c.getString(c.getColumnIndex(COL_REMARKS));
                    String dosage = c.getString(c.getColumnIndex(COL_DOSAGE));

                    long millis = c.getLong(c.getColumnIndex(COL_START_DATE));
                    int interval = c.getInt(c.getColumnIndex(COL_INTERVAL));

                    String timings = c.getString(c.getColumnIndex(COL_TIMINGS));
                    String deleted = c.getString(c.getColumnIndex(COL_DELETED));
                    Prescription p = new Prescription(id, drug_name, drug_thumbnail, dosage, remarks,
                            millis, interval, timings, deleted);
                    result.add(p);
                } while (c.moveToNext());
            }
            c.close();
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

            String drug_name = c.getString(c.getColumnIndex(COL_DRUG_NAME));
            Bitmap drug_thumbnail = getImage(c.getBlob(c.getColumnIndex(COL_THUMBNAIL)));
            String remarks = c.getString(c.getColumnIndex(COL_REMARKS));
            String dosage = c.getString(c.getColumnIndex(COL_DOSAGE));
            long millis = c.getLong(c.getColumnIndex(COL_START_DATE));
            int interval = c.getInt(c.getColumnIndex(COL_INTERVAL));
            String timings = c.getString(c.getColumnIndex(COL_TIMINGS));
            String deleted = c.getString(c.getColumnIndex(COL_DELETED));
            Prescription p = new Prescription(id, drug_name, drug_thumbnail, dosage, remarks,
                    millis, interval, timings, deleted);

            c.close();
            return p;
        }
        return null;
    }

    public void deletePrescription(Prescription p) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_PRESCRIPTION, COL_ID + " = ?", new String[] { String.valueOf(p.getId()) });
    }
//    public void deletePrescriptionById(long id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_NAME_PRESCRIPTION, COL_ID + " = ?", new String[] { String.valueOf(id) });
//    }

    /*
     * updates
     * does not require p to exist
     * returns the number of affected rows
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
        cv.put(COL_DELETED, longArrayToString(p.getDeleted()));

        int num_rows_modified;
        num_rows_modified = db.update(TABLE_NAME_PRESCRIPTION, cv, COL_ID + " = ?",
                new String[] { String.valueOf(p.getId()) });
        return num_rows_modified;
    }

    public int updateDeleted(Prescription p) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_DELETED, longArrayToString(p.getDeleted()));

        int num_rows_modified;
        num_rows_modified = db.update(TABLE_NAME_PRESCRIPTION, cv, COL_ID + " = ?",
                new String[] { String.valueOf(p.getId()) });
        return num_rows_modified;
    }
}