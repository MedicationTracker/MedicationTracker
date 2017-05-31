package com.example.medicationtracker.ObjectClasses;

import android.graphics.Bitmap;

/**
 * Created by Jia Hao on 5/31/2017.
 */

public class Drug {
    //is there a way to set default thumbnail??
    //static final int DEFAULT_IMAGE = R.mipmap.ic_launcher;
    //static final Bitmap DEFAULT_DRUG_THUMBNAIL

    int id = 0; //default value for now since not needed
    String name;
    Bitmap thumbnail;

    public Drug(String name, Bitmap thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    /*
    getters and setters
     */
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public Bitmap getThumbnail() { return this.thumbnail; }
    public void setThumbnail(Bitmap thumbnail) { this.thumbnail = thumbnail; }

    public boolean equals(Drug d) {
        return this.name.equals(d.getName());
    }
}
