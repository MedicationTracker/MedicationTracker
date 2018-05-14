package com.example.medicationtracker.objects;


public class ConsumptionInstruction {
    private String dosage;
    private String remarks;

    ConsumptionInstruction(String dosage, String remarks) {
        this.dosage = dosage;
        this.remarks = remarks;
    }

    /*
     * getters and setters
     */
    public String getDosage() { return this.dosage; }
    public String getRemarks() { return this.remarks; }
}
