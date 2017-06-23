package com.example.medicationtracker.dialogs;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.medicationtracker.objects.TimeOfDay;
import com.example.medicationtracker.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.medicationtracker.Utility.formatInt;
import static com.example.medicationtracker.Utility.stringToTimeOfDayArray;


public class TimeListDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    ListView timings_dialog_lv;
    Button btn_add, btn_clear, btn_save;
    ArrayList<TimeOfDay> timings;
    TimePickerDialog.OnTimeSetListener listener = this; // use this to receive from TimePickerDialog
    TimeListDialogListener mListener; // send messages to this guy using the interface

    public static TimeListDialog newInstance(String timeString) {
        TimeListDialog dialog = new TimeListDialog();

        // Supply timeString which specifies the timings to be displayed upon initialization
        Bundle args = new Bundle();
        args.putString("timestring", timeString);
        dialog.setArguments(args);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_timings_dialog, container, false);

        timings = stringToTimeOfDayArray(getArguments().getString("timestring"));

        timings_dialog_lv = (ListView) v.findViewById(R.id.layout_timings_lv);
        btn_add = (Button) v.findViewById(R.id.layout_timings_btn_add);
        btn_clear = (Button) v.findViewById(R.id.layout_timings_btn_clear);
        btn_save = (Button) v.findViewById(R.id.layout_timings_btn_save);

        // Open TimePickerDialog
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar temp = Calendar.getInstance();
                TimePickerDialog tpd = new TimePickerDialog(v.getContext(), TimePickerDialog.THEME_HOLO_DARK,
                        listener, temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.MINUTE), true);
                tpd.show();
            }
        });
        // Clears timings
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timings.clear();
                ArrayAdapter adapter = (ArrayAdapter) timings_dialog_lv.getAdapter();
                adapter.notifyDataSetChanged();
            }
        });
        // Sends results to EditActivity
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTimeListDialogFinishedListener(timings);
                dismiss();
            }
        });

        TimingsAdapter adapter = new TimingsAdapter(this.getActivity(), timings);
        timings_dialog_lv.setAdapter(adapter);
        return v;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TimeOfDay tod = new TimeOfDay(formatInt(hourOfDay, 2), formatInt(minute, 2));
        timings.add(tod);
        ArrayAdapter adapter = (ArrayAdapter) timings_dialog_lv.getAdapter();
        adapter.notifyDataSetChanged();
    }

    public interface TimeListDialogListener { //used by editActivity
        void onTimeListDialogFinishedListener(ArrayList<TimeOfDay> timings);
    }

    public void setTimeListDialogListener(TimeListDialogListener listener) {
        mListener = listener;
    }

    private class TimingsAdapter extends ArrayAdapter<TimeOfDay> {

        private TimingsAdapter(Context context, List<TimeOfDay> objects) {
            super(context, 0, objects);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.layout_time_list_dialog_items, parent, false);
            }

            // convert to viewHolder pattern?
            TextView tv_name = (TextView) convertView.findViewById(R.id.layout_time_list_dialog_items_tv_name);
            ImageView iv_delete = (ImageView) convertView.findViewById(R.id.layout_time_list_dialog_items_iv_delete);

            final TimeOfDay tod = timings.get(position);

            tv_name.setText(tod.toString());

            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timings.remove(tod);
                    ArrayAdapter adapter = (ArrayAdapter) timings_dialog_lv.getAdapter();
                    adapter.notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }
}
