package com.example.medicationtracker.objects;

/**
 * Created by Jia Hao on 5/31/2017.
 */

public class ConsumptionInstruction {
    String dosage;
    String remarks;

    public ConsumptionInstruction() {
        this.dosage = "1 tablet";
    }

    public ConsumptionInstruction(String dosage, String remarks) {
        this.dosage = dosage;
        this.remarks = remarks;
    }

    /*
    getters and setters
     */
    public String getDosage() { return this.dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getRemarks() { return this.remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
