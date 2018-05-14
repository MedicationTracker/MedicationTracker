package com.example.medicationtracker;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medicationtracker.objects.SessionManager;
import com.example.medicationtracker.objects.TextManager;

public class HistoryActivity extends AppCompatActivity {
    EditText et;
    TextManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        tm = new TextManager(this);

        et = (EditText) findViewById(R.id.et_history);
        et.setText(tm.getText());
        SessionManager sm = new SessionManager(this);
        if(!sm.isLoggedIn()) {
            et.setFocusable(false);
            et.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(HistoryActivity.this, "Please Login for Edit Privileges", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onSaveClicked(View v) {
        tm.setText(et.getText().toString());
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isChangesMade()) {
            String title = "Leaving...";
            String msg = "Save Changes?";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title).setMessage(msg);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tm.setText(et.getText().toString());
                    finish();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.create().show();
        } else {
            finish();
        }
    }

    public boolean isChangesMade() {
        return !tm.getText().equals(et.getText().toString());
    }
}
