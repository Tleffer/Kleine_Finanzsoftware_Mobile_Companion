package com.leon.kleinefinanzsoftwaremobilecompanion;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class HinzuActivity extends AppCompatActivity {
    int year;
    int month;
    int day;
    int hour;
    int minute;
    boolean income = false;

    boolean geb = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hinzu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Context cont = this;
        Button add_bt = findViewById(R.id.h_button_add);
        ImageButton back_bt = findViewById(R.id.back_button);
        EditText date = findViewById(R.id.editTextDate);
        EditText time = findViewById(R.id.editTextDate2);
        Button income_tg = findViewById(R.id.income_toggle);
        Button geb_tg = findViewById(R.id.gebucht_toggle);
        EditText usage = findViewById(R.id.usage_field);
        EditText amount = findViewById(R.id.betrag_field);

        add_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            add();
            }
        });
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(cont,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int pyear,
                                                  int pmonthOfYear, int pdayOfMonth) {

                                date.setText(pdayOfMonth + "/" + (pmonthOfYear + 1) + "/" + pyear);
                                year = pyear;
                                month = pmonthOfYear;
                                day = pdayOfMonth;

                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(cont,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int phourOfDay,
                                                  int pminute) {

                                Date datevar;
                                String timevar = phourOfDay + ":" + pminute;
                                DateFormat sdf = new SimpleDateFormat("HH:mm");
                                try {
                                    datevar = sdf.parse(timevar);

                                } catch (ParseException e) {
                                    //throw new RuntimeException(e);
                                    datevar = new Date();
                                    Log.d("parse-eror", e.toString());
                                }
                                time.setText(datevar.toString().substring(11,16));
                                hour = phourOfDay;
                                minute = pminute;
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        income_tg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                income = !income;
                if(income) {
                    income_tg.setBackgroundColor(getResources().getColor(R.color.toggle_on));
                    income_tg.setText("Einnahme");
                } else {
                    income_tg.setBackgroundColor(getResources().getColor(R.color.toggle_off));
                    income_tg.setText("Ausgabe");
                }
            }
        });
        geb_tg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geb = !geb;
                if(geb) {
                    geb_tg.setBackgroundColor(getResources().getColor(R.color.toggle_on));
                    geb_tg.setText("Gebucht");
                } else {
                    geb_tg.setBackgroundColor(getResources().getColor(R.color.toggle_off));
                    geb_tg.setText("Nicht Gebucht");
                }
            }
        });

        add_bt.setEnabled(false);
        add_bt.setBackgroundColor(getResources().getColor(R.color.grayed_out));

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if((!amount.getText().toString().isEmpty()) && (!usage.getText().toString().isEmpty())) {
                    add_bt.setEnabled(true);
                    add_bt.setBackgroundColor(getResources().getColor(R.color.add_color));
                } else {
                    add_bt.setEnabled(false);
                    add_bt.setBackgroundColor(getResources().getColor(R.color.grayed_out));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        usage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if((!amount.getText().toString().isEmpty()) && (!usage.getText().toString().isEmpty())) {
                    add_bt.setEnabled(true);
                    add_bt.setBackgroundColor(getResources().getColor(R.color.add_color));
                } else {
                    add_bt.setEnabled(false);
                    add_bt.setBackgroundColor(getResources().getColor(R.color.grayed_out));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        //date
        date.setText(day + "/" + (month + 1) + "/" + year);
        //time
        Date datevar;
        String timevar = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            datevar = sdf.parse(timevar);

        } catch (ParseException e) {
            //throw new RuntimeException(e);
            datevar = new Date();
            Log.d("parse-eror", e.toString());
        }
        time.setText(datevar.toString().substring(11,16));


    }
    private void add() {
        EditText usage = findViewById(R.id.usage_field);
        EditText amount = findViewById(R.id.betrag_field);
        Intent resultIntent = new Intent();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        Date date = calendar.getTime();
        Log.d("time", date.toString() + "  |  " + calendar.toString());
        Log.d("time", year + "/" + month+ "/" + day+ " " + hour + ":" + minute);
        resultIntent.putExtra("entry", new ImportEntry(Double.parseDouble(amount.getText().toString()),usage.getText().toString(), geb, income, date));
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    private void back() {
        setResult(RESULT_CANCELED);
        finish();
    }
}