package com.example.medicationtracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by Jia Hao on 5/31/2017.
 */

public class DatabaseAccess {
    private SQLiteDatabase database;
    private DatabaseOpenHelper openHelper;
    private static volatile DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static synchronized DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public void save(Drug drug) {
        ContentValues values = new ContentValues();
        values.put("name", drug.getName());
        values.put("dosage", drug.getDosage());
        values.put("remarks", drug.getRemarks());
        values.put("hour", drug.getHour());
        values.put("minute", drug.getMinute());
        values.put("thumbnail", getBytes(drug.getThumbnail()));
        database.insert(DatabaseOpenHelper.TABLE, null, values);
    }

    public void update(Drug drug) {
        ContentValues values = new ContentValues();
        String name = drug.getName();
        values.put("name", name);
        values.put("dosage", drug.getDosage());
        values.put("remarks", drug.getRemarks());
        values.put("hour", drug.getHour());
        values.put("minute", drug.getMinute());
        values.put("thumbnail", getBytes(drug.getThumbnail()));
        database.update(DatabaseOpenHelper.TABLE, values, "name = ?", new String[]{ name });
    }

    public void delete(Drug drug) {
        String name = drug.getName();
        database.delete(DatabaseOpenHelper.TABLE, "name = ?", new String[]{ name });
    }

    public List getAllDrugs() {
        List drugs = new ArrayList<Drug>();
        Cursor cursor = database.rawQuery("SELECT * From drugs ORDER BY name", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            String dosage = cursor.getString(1);
            String remarks = cursor.getString(2);
            int hour = cursor.getInt(3);
            int minute = cursor.getInt(4);
            byte[] thumbnail = cursor.getBlob(5);

            Drug new_drug = new Drug(name, dosage, remarks, hour, minute);
            new_drug.setThumbnail(getImage(thumbnail));

            drugs.add(new_drug);

            cursor.moveToNext();
        }
        cursor.close();
        return drugs;
    }

    // ghetto af?
    public Drug getDrugByName(String name) {
        Drug new_drug = null;
        Cursor cursor = database.rawQuery("SELECT * From drugs ORDER BY name", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String cur_name = cursor.getString(0);

            if (cur_name.equals(name)) {
                String dosage = cursor.getString(1);
                String remarks = cursor.getString(2);
                int hour = cursor.getInt(3);
                int minute = cursor.getInt(4);
                byte[] thumbnail = cursor.getBlob(5);

                new_drug = new Drug(name, dosage, remarks, hour, minute);
                new_drug.setThumbnail(getImage(thumbnail));
                break;
            } else {
                cursor.moveToNext();
            }
        }
        cursor.close();
        return new_drug;
    }


    // convert from Bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray(); // does this release resources?
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
