package com.example.medicationtracker.ObjectClasses;

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
}
