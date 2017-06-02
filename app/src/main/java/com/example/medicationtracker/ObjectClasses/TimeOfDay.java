package com.example.medicationtracker.ObjectClasses;

/**
 Class to store a time in 24 hour format
 */

public class TimeOfDay {
    String hour; //2 char string e.g. "08"
    String minute;

    /*
    future: include check for validity of String parameters
     */
    public TimeOfDay(String hour, String minute) {
        this.hour = hour;
        this.minute = minute;
    }

    /*
    getters and setters
     */
    public String getHour() { return this.hour; }
    public void setHour(String hour) { this.hour = hour; }

    public String getMinute() { return this.minute; }
    public void setMinute(String minute) { this.minute = minute; }

    public String toString() { return hour + minute; }
}
