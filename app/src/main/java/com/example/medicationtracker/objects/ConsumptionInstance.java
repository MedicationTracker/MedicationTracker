package com.example.medicationtracker.objects;

import android.support.annotation.NonNull;

import java.util.GregorianCalendar;

public class ConsumptionInstance implements Comparable {

    private long id;
    private GregorianCalendar consumption_time;
    private Drug drug;
    private boolean deleted;

    ConsumptionInstance(long id, GregorianCalendar consumption_time, Drug drug) {
        this.id = id;
        this.consumption_time = consumption_time;
        this.drug = drug;
        this.deleted = false;
    }

    /*
     * getters
     */
    public long getId() { return this.id; }
    public GregorianCalendar getConsumptionTime() { return this.consumption_time; }
    public Drug getDrug() { return this.drug; }
    public boolean isDeleted() { return this.deleted; }

    public void setDeleted(boolean value) { this.deleted = value; }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof ConsumptionInstance) {
            GregorianCalendar other_calendar = ((ConsumptionInstance) o).getConsumptionTime();
            return consumption_time.compareTo(other_calendar);
        }
        return 1;
    }
}
