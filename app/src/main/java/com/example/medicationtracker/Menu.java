package com.example.medicationtracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Created by Ryan on 27/6/2017.
 */

public class Menu extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.menu.layout_menu);
    }

    // Handle item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this,Settings.class));
                return true;

            case R.id.login:
                startActivity(new Intent(this,Login.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
