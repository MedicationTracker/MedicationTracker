package com.example.medicationtracker.objects;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.example.medicationtracker.Utility.stringToTimeOfDayArray;
import static com.example.medicationtracker.Utility.timeOfDayArrayToString;

/**
 * Created by Jia Hao on 5/31/2017.
 */

public class Prescription {
    long id = 0; //default value for id. id is assigned when storing into db.
    GregorianCalendar start_date; //used to store the DD/MM/YYYY. the HH:MM field is not used
    int interval;
    ArrayList<TimeOfDay> timings;
    Drug drug;
    ConsumptionInstruction consumption_instruction;

    // default values
    public Prescription() {

    }

    public Prescription(GregorianCalendar start_date, int interval, ArrayList<TimeOfDay> timings,
                         Drug drug, ConsumptionInstruction ci) {
        this.start_date = start_date;
        this.interval = interval;
        this.timings = timings;
        this.drug = drug;
        this.consumption_instruction = ci;
    }

    public Prescription(long id, GregorianCalendar start_date, int interval, ArrayList<TimeOfDay> timings,
                        Drug drug, ConsumptionInstruction ci) {
        this.id = id;
        this.start_date = start_date;
        this.interval = interval;
        this.timings = timings;
        this.drug = drug;
        this.consumption_instruction = ci;
    }

    /*
    constructor which uses raw inputs
    dont need to make the objects by yourself
     */
    public Prescription(long id, String drug_name, Bitmap drug_thumbnail, String dosage, String remarks,
                        long millis, int interval, String timings) {
        this.id = id;

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);
        this.start_date = calendar;
        this.interval = interval;
        this.timings = stringToTimeOfDayArray(timings);
        this.drug = new Drug(drug_name, drug_thumbnail);
        this.consumption_instruction = new ConsumptionInstruction(dosage, remarks);
    }

    /*
    generate the next set of consumption instances
    used to create the chronological list of medications
    post cond: size of result is equal to number of timings
     */
    public ArrayList<ConsumptionInstance> generateConsumptionInstances(int skip) {
        GregorianCalendar now = new GregorianCalendar();
        ArrayList<ConsumptionInstance> result = new ArrayList<>();

        for(TimeOfDay tod : timings) {
            GregorianCalendar temp = (GregorianCalendar) start_date.clone();
            temp.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tod.getHour()));
            temp.set(Calendar.MINUTE, Integer.parseInt(tod.getMinute()));

            while(temp.compareTo(now) < 0) { //cycle forward to nearest future instance
                temp.add(Calendar.DAY_OF_MONTH, this.interval);
            }
            temp.add(Calendar.DAY_OF_MONTH, skip*this.interval); // add skip

            result.add(new ConsumptionInstance(this.id, temp, this.drug, this.consumption_instruction));
        }

        return result;
    }

    /*
    getters and setters
     */
    public long getId() { return this.id; }
    public void setId(long id) { this.id = id; }

    public GregorianCalendar getStartDate() { return this.start_date; }
    public void setStartDate(GregorianCalendar c) { this.start_date = c; }

    public int getInterval() { return this.interval; }
    public void setInterval(int interval) { this.interval = interval; }

    public ArrayList<TimeOfDay> getTimings() { return this.timings; }
    public String getTimingsString() {
        return timeOfDayArrayToString(this.timings);
    }
    public void setTimings(ArrayList<TimeOfDay> timings) { this.timings = timings; }

    public Drug getDrug() { return this.drug; }
    public void setDrug(Drug d) { this.drug = d; }

    public ConsumptionInstruction getConsumptionInstruction() { return this.consumption_instruction; }
    public void setConsumptionInstruction(ConsumptionInstruction ci) { this.consumption_instruction = ci; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Prescription) {
            return this.id == ((Prescription) obj).getId();
        }
        return false;
    }
}
