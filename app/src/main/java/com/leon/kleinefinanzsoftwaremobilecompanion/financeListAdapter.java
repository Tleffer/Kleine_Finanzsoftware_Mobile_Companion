package com.leon.kleinefinanzsoftwaremobilecompanion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class financeListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] items;
    private ImportEntry[] importEntries;
    private final Import ImportData;
    private final MainActivity m;

    public financeListAdapter(Context context, String[] items, Import importData, MainActivity m) {
        super(context, R.layout.list_row, items);
        this.context = context;
        this.items = items;
        this.ImportData = importData;
        importEntries = new ImportEntry[importData.data.size()];
        importData.data.toArray(importEntries);
        this.m = m;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Inflate layout
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_row, parent, false);
        }

        // Set text
        TextView abg_text = convertView.findViewById(R.id.text_abg);
        if(importEntries[position].gebucht) {
            abg_text.setText("Abgebucht");
            abg_text.setTextColor(Color.GREEN);
        } else {
            abg_text.setText("Nicht Abgebucht");
            abg_text.setTextColor(Color.RED);
        }

        TextView ein_text = convertView.findViewById(R.id.text_ein);
        if(importEntries[position].einnahme) {
            ein_text.setText("Einnahme");
            ein_text.setTextColor(Color.GREEN);
        } else {
            ein_text.setText("Ausgabe");
            ein_text.setTextColor(Color.RED);
        }

        TextView amount_text = convertView.findViewById(R.id.text_amount);
        amount_text.setText(importEntries[position].amount + " €");

        TextView date_text = convertView.findViewById(R.id.text_date);
        Date date;
        String time = toCalendar(importEntries[position].date).get(Calendar.HOUR_OF_DAY) + ":" + toCalendar(importEntries[position].date).get(Calendar.MINUTE);
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            date = sdf.parse(time);

        } catch (ParseException e) {
            //throw new RuntimeException(e);
            date = new Date();
            Log.d("parse-eror", e.toString());
        }

        date_text.setText(toCalendar(importEntries[position].date).get(Calendar.DAY_OF_MONTH) + "."
                + (toCalendar(importEntries[position].date).get(Calendar.MONTH) + 1) + "."
                + toCalendar(importEntries[position].date).get(Calendar.YEAR) + " "
                + date.toString().substring(11,16)
                );
        TextView usage_text = convertView.findViewById(R.id.text_usage);
        usage_text.setText(importEntries[position].usage);

        CheckBox sel = convertView.findViewById(R.id.sel);

        sel.setChecked(importEntries[position].check);

        // Handle button click

        Button abgButton = convertView.findViewById(R.id.abg_button);
        abgButton.setOnClickListener(v ->
                on_abgebucht(position)
        );

        sel.setOnClickListener(v ->
                on_check(position, sel.isChecked())
        );

        return convertView;
    }

    private void on_check(int position, boolean c) {
        m.checked(position, c);
    }
    private void on_abgebucht(int position) {
        m.abgebucht(position);
    }
    public Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
