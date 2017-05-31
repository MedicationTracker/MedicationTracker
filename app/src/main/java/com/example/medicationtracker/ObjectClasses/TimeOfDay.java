package com.example.medicationtracker.ObjectClasses;

/**
 Class to store a time in 24 hour format
 */

public class TimeOfDay {
    int hour;
    int minute;

    public TimeOfDay(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    /*
    getters and setters
     */
    public int getHour() { return this.hour; }
    public void setHour(int hour) { this.hour = hour; }

    public int getMinute() { return this.minute; }
    public void setMinute(int minute) { this.minute = minute; }
}
