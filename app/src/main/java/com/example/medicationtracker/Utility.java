package com.example.medicationtracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.medicationtracker.objects.TimeOfDay;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Utility class used to store all (static) utility methods
 */

public final class Utility {

    /*
    prevents construction of this class
     */
    private Utility() {}

    public static void zeroToMinute(Calendar c) {
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    public static String CalendarToDateString(Calendar c) {
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        return formatInt(day, 2) + formatInt(month, 2) + formatInt(year, 4);
    }

    public static String formatInt(int num, int digits) {
        return String.format("%0" + digits + "d", num);
    }

    public static String timeOfDayArrayToString(ArrayList<TimeOfDay> timings) {
        StringBuilder builder = new StringBuilder();
        for(TimeOfDay t : timings) {
            builder.append(t.getHour()).append(t.getMinute()).append(" ");
        }
        return builder.toString().trim();
    }

    public static ArrayList<TimeOfDay> stringToTimeOfDayArray(String timings) {
        ArrayList<TimeOfDay> result = new ArrayList<>();

        if (timings.trim().equals("")) { //timings is whitespace or blank
            return result;
        }

        String[] arr = timings.split(" ");
        for(String s : arr) {
            TimeOfDay t = new TimeOfDay(s.substring(0, 2), s.substring(2, 4));
            result.add(t);
        }
        return result;
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

    private static long dateStringToMillis(String ds) {
        int day = Integer.parseInt(ds.substring(0, 2));
        int month = Integer.parseInt(ds.substring(2, 4));
        int year = Integer.parseInt(ds.substring(4, 8));
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        return c.getTimeInMillis();
    }

    private static Calendar millisToCalendar(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c;
    }

}
