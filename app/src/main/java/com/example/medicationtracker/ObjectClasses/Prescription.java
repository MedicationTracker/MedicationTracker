package com.example.medicationtracker.ObjectClasses;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Jia Hao on 5/31/2017.
 */

public class Prescription {
    int id;
    Calendar start_date;
    int interval;
    ArrayList<TimeOfDay> timings;
    Drug drug;
    ConsumptionInstruction consumption_instruction;

    public Prescription(int id, Calendar start_date, int interval, ArrayList<TimeOfDay> timings,
                        Drug drug, ConsumptionInstruction ci) {
        this.id = id;
        this.start_date = start_date;
        this.interval = interval;
        this.timings = timings;
        this.drug = drug;
        this.consumption_instruction = ci;
    }

    public ArrayList<ConsumptionInstance> generateConsumptionInstances(int skip) {

    }

    /*
    getters and setters
     */
    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public Calendar getStartDate() { return this.start_date; }
    public void setStartDate(Calendar c) { this.start_date = c; }

    public int getInterval() { return this.interval; }
    public void setInterval(int interval) { this.interval = interval; }

    public ArrayList<TimeOfDay> getTimings() { return this.timings; }
    public void setTimings(ArrayList<TimeOfDay> timings) { this.timings = timings; }

    public Drug getDrug() { return this.drug; }
    public void setDrug(Drug d) { this.drug = d; }

    public ConsumptionInstruction getConsumptionInstruction() { return this.consumption_instruction; }
    public void setConsumptionInstruction(ConsumptionInstruction ci) { this.consumption_instruction = ci; }

}
