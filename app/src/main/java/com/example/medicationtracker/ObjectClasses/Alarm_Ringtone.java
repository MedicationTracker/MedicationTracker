package com.example.medicationtracker.ObjectClasses;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.medicationtracker.R;

/**
 * Created by Ryan on 22/6/2017.
 */

public class Alarm_Ringtone extends Service {

    MediaPlayer media_song;
    int startId;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId) {

        // fetch extra String values, update alarm state
        String state = intent.getExtras().getString("extra");

        assert state != null;

        switch (state) {
            case "on":
                startId = 1;
                break;
            case "off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        // consider different cases to determine behaviour of alarm
        // if no music playing, music should play if alarm on when time reach
        if (!this.isRunning && startId == 1) {
            // create instance of media player
            media_song = MediaPlayer.create(this, R.raw.huang_hun);
            media_song.start();
            this.isRunning=true;
            this.startId=0;
        }
        // if music playing, user press alarm off, music stop
        else if(this.isRunning && startId == 0) {
            media_song.stop();
            media_song.reset();
            this.isRunning=false;
            this.startId=0;
        }
        // if music not playing and user press unset, nothing happens
        else if(!this.isRunning && startId == 0){
            this.isRunning=false;
            this.startId=0;
        }
        // if music playing and user press set alarm, nothing happens
        else {
            this.isRunning=true;
            this.startId=1;
        }
        return START_NOT_STICKY;
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning=false;
    }
}
