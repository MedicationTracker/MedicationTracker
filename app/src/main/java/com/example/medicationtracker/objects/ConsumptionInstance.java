package com.example.medicationtracker.objects;


import android.support.annotation.NonNull;

import java.util.GregorianCalendar;

/**
 * Created by Jia Hao on 5/31/2017.
 */

public class ConsumptionInstance implements Comparable {
    long id;
    GregorianCalendar consumption_time;
    Drug drug;
    ConsumptionInstruction consumption_instruction;

    public ConsumptionInstance(long id, GregorianCalendar consumption_time, Drug drug, ConsumptionInstruction ci) {
        this.id = id;
        this.consumption_time = consumption_time;
        this.drug = drug;
        this.consumption_instruction = ci;
    }

    /*
    getters
     */
    public long getId() { return this.id; }
    public GregorianCalendar getConsumptionTime() { return this.consumption_time; }
    public Drug getDrug() { return this.drug; }
    public ConsumptionInstruction getConsumptionInstruction() { return this.consumption_instruction; }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof ConsumptionInstance) {
            GregorianCalendar other_calendar = ((ConsumptionInstance) o).getConsumptionTime();
            return consumption_time.compareTo(other_calendar);
        }
        return 1;
    }
}
