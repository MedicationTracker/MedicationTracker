package com.example.medicationtracker.objects;

import android.graphics.Bitmap;

public class Drug {
    private String name;
    private Bitmap thumbnail;

    Drug(String name, Bitmap thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    /*
     * getters and setters
     */
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public Bitmap getThumbnail() { return this.thumbnail; }
}