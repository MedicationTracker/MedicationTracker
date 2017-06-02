package com.example.medicationtracker.ObjectClasses;


import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Jia Hao on 5/31/2017.
 */

public class ConsumptionInstance {
    GregorianCalendar consumption_time;
    Drug drug;
    ConsumptionInstruction consumption_instruction;

    public ConsumptionInstance(GregorianCalendar consumption_time, Drug drug, ConsumptionInstruction ci) {
        this.consumption_time = consumption_time;
        this.drug = drug;
        this.consumption_instruction = ci;
    }

    /*
    getters
     */
    public GregorianCalendar getConsumptionTime() { return this.consumption_time; }
    public Drug getDrug() { return this.drug; }
    public ConsumptionInstruction getConsumptionInstruction() { return this.consumption_instruction; }
}
