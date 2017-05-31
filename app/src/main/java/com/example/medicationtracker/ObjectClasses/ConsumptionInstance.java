package com.example.medicationtracker.ObjectClasses;


import java.util.Calendar;

/**
 * Created by Jia Hao on 5/31/2017.
 */

public class ConsumptionInstance {
    Calendar consumption_time;
    Drug drug;
    ConsumptionInstruction consumption_instruction;

    public ConsumptionInstance(Calendar consumption_time, Drug drug, ConsumptionInstruction ci) {
        this.consumption_time = consumption_time;
        this.drug = drug;
        this.consumption_instruction = ci;
    }

    /*
    getters
     */
    public Calendar getConsumptionTime() { return this.consumption_time; }
    public Drug getDrug() { return this.drug; }
    public ConsumptionInstruction getConsumptionInstruction() { return this.consumption_instruction; }
}
